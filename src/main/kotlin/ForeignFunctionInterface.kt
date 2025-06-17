package org.bread_experts_group

import org.bread_experts_group.logging.ColoredHandler
import java.io.IOException
import java.lang.foreign.*
import java.lang.invoke.MethodHandle
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
	val descriptor = FunctionDescriptor.of(layouts[0], *layouts.sliceArray(1..layouts.size - 1))
	return linker.downcallHandle(this, descriptor)
}

fun MemorySegment.getDowncallVoid(linker: Linker, vararg layouts: ValueLayout): MethodHandle {
	val descriptor = FunctionDescriptor.ofVoid(*layouts)
	return linker.downcallHandle(this, descriptor)
}

fun SymbolLookup.getDowncall(linker: Linker, name: String, vararg layouts: ValueLayout): MethodHandle {
	val descriptor = FunctionDescriptor.of(layouts[0], *layouts.sliceArray(1..layouts.size - 1))
	return linker.downcallHandle(this.getAddress(name), descriptor)
}

fun SymbolLookup.getDowncallVoid(linker: Linker, name: String, vararg layouts: ValueLayout): MethodHandle {
	val descriptor = FunctionDescriptor.ofVoid(*layouts)
	return linker.downcallHandle(this.getAddress(name), descriptor)
}