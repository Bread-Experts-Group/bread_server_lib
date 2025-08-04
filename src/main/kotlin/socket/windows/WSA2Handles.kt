package org.bread_experts_group.socket.windows

import org.bread_experts_group.getDowncall
import org.bread_experts_group.getLookup
import java.lang.foreign.Arena
import java.lang.foreign.Linker
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle

private val handleArena = Arena.ofAuto()
private val ws2Lookup: SymbolLookup = handleArena.getLookup("Ws2_32.dll")
private val linker: Linker = Linker.nativeLinker()

fun makeWord(highByte: UByte, lowByte: UByte): UShort = (highByte.toInt() shl 8 or lowByte.toInt()).toUShort()

val nativeWSAStartup: MethodHandle = ws2Lookup.getDowncall(
	linker, "WSAStartup", ValueLayout.JAVA_INT,
	ValueLayout.JAVA_SHORT, ValueLayout.ADDRESS
)

val nativeWSCEnumProtocols: MethodHandle = ws2Lookup.getDowncall(
	linker, "WSCEnumProtocols", ValueLayout.JAVA_INT,
	ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS
)

val nativeWSAGetLastError: MethodHandle = ws2Lookup.getDowncall(
	linker, "WSAGetLastError", ValueLayout.JAVA_INT
)

val nativeWSASocketW: MethodHandle = ws2Lookup.getDowncall(
	linker, "WSASocketW", ValueLayout.JAVA_INT,
	ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT,
	ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT
)

val nativeWSAIoctl: MethodHandle = ws2Lookup.getDowncall(
	linker, "WSAIoctl", ValueLayout.JAVA_INT,
	ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS,
	ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT,
	ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS
)

val nativeWSARecvFrom: MethodHandle = ws2Lookup.getDowncall(
	linker, "WSARecvFrom", ValueLayout.JAVA_INT,
	ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT,
	ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS,
	ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS
)

val nativeCloseSocket: MethodHandle = ws2Lookup.getDowncall(
	linker, "closesocket", ValueLayout.JAVA_INT,
	ValueLayout.JAVA_INT
)

val nativeGetSockName: MethodHandle = ws2Lookup.getDowncall(
	linker, "getsockname", ValueLayout.JAVA_INT,
	ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS
)

val nativeBind: MethodHandle = ws2Lookup.getDowncall(
	linker, "bind", ValueLayout.JAVA_INT,
	ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT
)