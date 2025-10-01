package org.bread_experts_group.ffi

import org.bread_experts_group.hex
import org.bread_experts_group.logging.ColoredHandler
import java.io.IOException
import java.lang.foreign.*
import java.lang.invoke.MethodHandle
import java.util.logging.Logger
import kotlin.jvm.optionals.getOrNull

private val nativeLogger: Logger = ColoredHandler.newLoggerResourced("ffi")

abstract class NativeObjectNotFoundException(name: String) : IOException(name)
class NativeLibraryNotFoundException(library: String) : NativeObjectNotFoundException(library)

fun Arena.getLookup(library: String): SymbolLookup = try {
	nativeLogger.finer { "Getting the library lookup for \"$library\"" }
	SymbolLookup.libraryLookup(library, this).also {
		nativeLogger.fine { "Library lookup for \"$library\" created" }
	}
} catch (_: IllegalArgumentException) {
	nativeLogger.warning { "Library \"$library\" was not located." }
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

fun SymbolLookup.getAddress(name: String): MemorySegment? {
	nativeLogger.finer { "Getting the address for \"$name\"" }
	val address: MemorySegment? = this.find(name).getOrNull()
	if (address == null) {
		nativeLogger.warning { "\"$name\" was not located." }
		return null
	}
	nativeLogger.fine { "\"$name\" was located at the address ${address.debugString()}" }
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
): MethodHandle? {
	val descriptor = FunctionDescriptor.of(layouts[0], *layouts.sliceArray(1..<layouts.size))
	return linker.downcallHandle(
		this.getAddress(name) ?: return null,
		descriptor, *options.toTypedArray()
	)
}

fun SymbolLookup.getDowncall(linker: Linker, name: String, vararg layouts: ValueLayout): MethodHandle? {
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