package org.bread_experts_group.ffi

import org.bread_experts_group.hex
import org.bread_experts_group.logging.ColoredHandler
import java.lang.foreign.*
import java.lang.invoke.MethodHandle
import java.util.logging.Logger
import kotlin.jvm.optionals.getOrNull

private val nativeLogger: Logger = ColoredHandler.newLoggerResourced("ffi")

val cLookup: SymbolLookup = nativeLinker.defaultLookup()

fun Arena.getLookup(library: String): SymbolLookup? = try {
	nativeLogger.finer { "Getting the library lookup for \"$library\"" }
	SymbolLookup.libraryLookup(library, this).also {
		nativeLogger.fine { "Library lookup for \"$library\" created" }
	}
} catch (_: IllegalArgumentException) {
	nativeLogger.warning { "Library \"$library\" was not located." }
	null
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
	val descriptor = FunctionDescriptor.of(layouts[0], *layouts.sliceArray(1 until layouts.size))
	return linker.downcallHandle(this, descriptor)
}

fun MemorySegment.getDowncallVoid(linker: Linker, vararg layouts: ValueLayout): MethodHandle {
	val descriptor = FunctionDescriptor.ofVoid(*layouts)
	return linker.downcallHandle(this, descriptor)
}

fun SymbolLookup?.getDowncall(
	linker: Linker, name: String,
	layouts: Array<out ValueLayout>,
	options: List<Linker.Option?>
): MethodHandle? {
	val descriptor = FunctionDescriptor.of(layouts[0], *layouts.sliceArray(1 until layouts.size))
	return linker.downcallHandle(
		this?.getAddress(name) ?: return null,
		descriptor, *options.toTypedArray()
	)
}

fun SymbolLookup?.getDowncall(linker: Linker, name: String, vararg layouts: ValueLayout): MethodHandle? {
	return this?.getDowncall(linker, name, layouts, emptyList())
}

fun SymbolLookup?.getDowncallVoid(linker: Linker, name: String, vararg layouts: ValueLayout): MethodHandle? {
	val descriptor = FunctionDescriptor.ofVoid(*layouts)
	return linker.downcallHandle(
		this?.getAddress(name) ?: return null,
		descriptor
	)
}

val globalArena: Arena
	get() = Arena.global()
val nativeLinker: Linker
	get() = Linker.nativeLinker()
val capturedStateLayout: StructLayout = Linker.Option.captureStateLayout()

private val tlsCSS = ThreadLocal.withInitial {
	globalArena.allocate(capturedStateLayout)
}
val capturedStateSegment: MemorySegment
	get() = tlsCSS.get()

fun MemorySegment.getFirstNull2Offset(): Long {
	var offset = 0L
	while (offset < this.byteSize()) {
		if (this.get(ValueLayout.JAVA_SHORT, offset) == 0.toShort()) return offset
		offset += 2
	}
	return -1
}

private val tlsPTR = ThreadLocal.withInitial {
	globalArena.allocate(ValueLayout.ADDRESS)
}
private val tlsInt = ThreadLocal.withInitial {
	globalArena.allocate(ValueLayout.JAVA_INT)
}
val threadLocalPTR: MemorySegment
	get() = tlsPTR.get()
val threadLocalInt: MemorySegment
	get() = tlsInt.get()