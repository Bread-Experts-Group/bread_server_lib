package org.bread_experts_group.ffi

import org.bread_experts_group.hex
import org.bread_experts_group.logging.ColoredHandler
import java.io.IOException
import java.lang.foreign.*
import java.lang.invoke.MethodHandle
import java.nio.ByteOrder
import java.nio.charset.Charset
import java.util.logging.Logger
import kotlin.jvm.optionals.getOrNull

private val nativeLogger: Logger = ColoredHandler.newLoggerResourced("ffi")

abstract class NativeObjectNotFoundException(name: String) : IOException(name)
class NativeLibraryNotFoundException(library: String) : NativeObjectNotFoundException(library)
class SymbolNotFoundException(symbol: String) : NativeObjectNotFoundException(symbol)

fun Arena.getLookup(library: String): SymbolLookup = try {
	nativeLogger.fine { "Getting the library lookup for \"$library\", standby" }
	SymbolLookup.libraryLookup(library, this).also {
		nativeLogger.info { "Library lookup for \"$library\" created" }
	}
} catch (_: IllegalArgumentException) {
	nativeLogger.severe { "Library \"$library\" was not located." }
	throw NativeLibraryNotFoundException(library)
}

fun MemorySegment.composeFlags(): String = '[' + buildList {
	if (this@composeFlags.isMapped) {
		if (this@composeFlags.isLoaded) add("M.LOADED")
		else add("MAPPED")
	}
	if (this@composeFlags.isNative) add("NATIVE")
	if (this@composeFlags.isReadOnly) add("READONLY")
}.joinToString(",") + ']'

fun MemorySegment.debugString(): String = "{${hex(this.address().toULong())}}; ${this.composeFlags()}"

fun SymbolLookup.getAddress(name: String): MemorySegment {
	nativeLogger.fine { "Getting the address for \"$name\", standby" }
	val address: MemorySegment? = this.find(name).getOrNull()
	if (address == null) {
		nativeLogger.severe { "\"$name\" was not located." }
		throw SymbolNotFoundException(name)
	}
	nativeLogger.info { "\"$name\" was located at the address ${address.debugString()}" }
	return address
}

fun MemorySegment.getDowncall(linker: Linker, vararg layouts: ValueLayout): MethodHandle {
	val descriptor = FunctionDescriptor.of(layouts[0], *layouts.sliceArray(1..<layouts.size))
	return linker.downcallHandle(this, descriptor)
}

fun MemorySegment.getDowncallVoid(linker: Linker, vararg layouts: ValueLayout): MethodHandle {
	val descriptor = FunctionDescriptor.ofVoid(*layouts)
	return linker.downcallHandle(this, descriptor)
}

fun SymbolLookup.getDowncall(
	linker: Linker, name: String,
	layouts: Array<out ValueLayout>,
	options: List<Linker.Option>
): MethodHandle {
	val descriptor = FunctionDescriptor.of(layouts[0], *layouts.sliceArray(1..<layouts.size))
	return linker.downcallHandle(this.getAddress(name), descriptor, *options.toTypedArray())
}

fun SymbolLookup.getDowncall(linker: Linker, name: String, vararg layouts: ValueLayout): MethodHandle {
	return getDowncall(linker, name, layouts, emptyList())
}

fun SymbolLookup.getDowncallVoid(linker: Linker, name: String, vararg layouts: ValueLayout): MethodHandle {
	val descriptor = FunctionDescriptor.ofVoid(*layouts)
	return linker.downcallHandle(this.getAddress(name), descriptor)
}

private val gcArena = Arena.ofAuto()
val capturedStateLayout: StructLayout = Linker.Option.captureStateLayout()

private val tlsCSS = ThreadLocal.withInitial {
	gcArena.allocate(capturedStateLayout)
}
val capturedStateSegment: MemorySegment
	get() = tlsCSS.get()

/**
 * @param length The length in characters of the target string, or `-1` if null terminated
 * @author Miko Elbrecht
 */
fun MemorySegment.readString(coding: Charset, length: Int = -1): String = when (coding) {
	Charsets.UTF_16, Charsets.UTF_32 ->
		if (
			this.get(ValueLayout.JAVA_SHORT, 0L) == 0xFEFF.toShort() &&
			ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN
		) readStringSE(coding, length)
		else readStringDE(coding, length)

	Charsets.UTF_16BE, Charsets.UTF_32BE ->
		if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) readStringSE(coding, length)
		else readStringDE(coding, length)

	Charsets.UTF_16LE, Charsets.UTF_32LE ->
		if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) readStringSE(coding, length)
		else readStringDE(coding, length)


	Charsets.US_ASCII, Charsets.UTF_8, Charsets.ISO_8859_1 -> {
		var concatenated = ""
		var offset = 0L
		val size = this.byteSize()
		if (length == -1) while (offset < size) {
			val next = this.get(ValueLayout.JAVA_BYTE, offset++).toUShort()
			if (next == UShort.MIN_VALUE) break
			concatenated += Char(next)
		} else while (offset < length) {
			val next = this.get(ValueLayout.JAVA_BYTE, offset++).toUShort()
			concatenated += Char(next)
		}
		concatenated
	}

	else -> throw IllegalArgumentException("Unsupported charset \"${coding.displayName()}\"")
}

private fun MemorySegment.readStringSE(coding: Charset, length: Int): String {
	var concatenated = ""
	var offset = 0L
	val size = this.byteSize()
	when (coding) {
		Charsets.UTF_16LE, Charsets.UTF_16BE, Charsets.UTF_16 -> if (length == -1) while (offset < size) {
			val next = this.get(ValueLayout.JAVA_SHORT, offset).toUShort()
			offset += 2
			if (next == UShort.MIN_VALUE) break
			concatenated += Char(next)
		} else while (offset < length * 2) {
			val next = this.get(ValueLayout.JAVA_SHORT, offset).toUShort()
			offset += 2
			concatenated += Char(next)
		}

		Charsets.UTF_32LE, Charsets.UTF_32BE, Charsets.UTF_32 -> if (length == -1) while (offset < size) {
			val next = this.get(ValueLayout.JAVA_INT, offset).toUInt()
			offset += 4
			if (next == UInt.MIN_VALUE) break
			concatenated += Char(next.toUShort())
			concatenated += Char((next shr 16).toUShort())
		} else while (offset < length * 4) {
			val next = this.get(ValueLayout.JAVA_INT, offset).toUInt()
			offset += 4
			concatenated += Char(next.toUShort())
			concatenated += Char((next shr 16).toUShort())
		}

		else -> throw IllegalStateException()
	}
	return concatenated
}

private fun MemorySegment.readStringDE(coding: Charset, length: Int): String = when (coding) {
	else -> throw IllegalArgumentException("Unsupported charset \"${coding.displayName()}\"")
}