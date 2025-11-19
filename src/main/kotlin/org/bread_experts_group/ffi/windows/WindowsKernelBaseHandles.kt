package org.bread_experts_group.ffi.windows

import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.getLookup
import org.bread_experts_group.ffi.globalArena
import org.bread_experts_group.ffi.nativeLinker
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle

private val kernelBaseLookup: SymbolLookup? = globalArena.getLookup("KernelBase.dll")

val nativePathCchAppendEx: MethodHandle? = kernelBaseLookup.getDowncall(
	nativeLinker, "PathCchAppendEx", HRESULT,
	PWSTR, ValueLayout.JAVA_LONG, LPCWSTR, ULONG
)

val nativePathCchRemoveBackslash: MethodHandle? = kernelBaseLookup.getDowncall(
	nativeLinker, "PathCchRemoveBackslash", HRESULT,
	PWSTR, ValueLayout.JAVA_LONG
)

val nativePathCchRemoveFileSpec: MethodHandle? = kernelBaseLookup.getDowncall(
	nativeLinker, "PathCchRemoveFileSpec", HRESULT,
	PWSTR, ValueLayout.JAVA_LONG
)