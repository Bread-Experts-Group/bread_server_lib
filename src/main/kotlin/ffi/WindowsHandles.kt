package org.bread_experts_group.ffi

import java.lang.foreign.Arena
import java.lang.foreign.Linker
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle

private val handleArena = Arena.ofAuto()
private val ole32Lookup: SymbolLookup = handleArena.getLookup("Ole32.dll")
private val kernel32Lookup: SymbolLookup = handleArena.getLookup("Kernel32.dll")
private val linker: Linker = Linker.nativeLinker()

fun makeWord(highByte: UByte, lowByte: UByte): UShort = (highByte.toInt() shl 8 or lowByte.toInt()).toUShort()

val nativeCoCreateGuid: MethodHandle = ole32Lookup.getDowncall(
	linker, "CoCreateGuid", ValueLayout.JAVA_INT,
	ValueLayout.ADDRESS
)

val nativeFormatMessageW: MethodHandle = kernel32Lookup.getDowncall(
	linker, "FormatMessageW", ValueLayout.JAVA_INT,
	ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT,
	ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT,
	ValueLayout.ADDRESS
)

val nativeLocalFree: MethodHandle = kernel32Lookup.getDowncall(
	linker, "LocalFree", ValueLayout.ADDRESS,
	ValueLayout.ADDRESS
)