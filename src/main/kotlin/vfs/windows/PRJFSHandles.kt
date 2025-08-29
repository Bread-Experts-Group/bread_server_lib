package org.bread_experts_group.vfs.windows

import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.getDowncallVoid
import org.bread_experts_group.ffi.getLookup
import java.lang.foreign.Arena
import java.lang.foreign.Linker
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle

private val handleArena = Arena.ofAuto()
private val prjFSLookup: SymbolLookup = handleArena.getLookup("PROJECTEDFSLIB.dll")
private val linker: Linker = Linker.nativeLinker()

val nativePrjMarkDirectoryAsPlaceholder: MethodHandle = prjFSLookup.getDowncall(
	linker, "PrjMarkDirectoryAsPlaceholder", ValueLayout.JAVA_INT,
	ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS,
	ValueLayout.ADDRESS
)

val nativePrjStartVirtualizing: MethodHandle = prjFSLookup.getDowncall(
	linker, "PrjStartVirtualizing", ValueLayout.JAVA_INT,
	ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS,
	ValueLayout.ADDRESS, ValueLayout.ADDRESS
)

val nativePrjStopVirtualizing: MethodHandle = prjFSLookup.getDowncallVoid(
	linker, "PrjStopVirtualizing", ValueLayout.ADDRESS
)

val nativePrjFillDirEntryBuffer: MethodHandle = prjFSLookup.getDowncall(
	linker, "PrjFillDirEntryBuffer", ValueLayout.JAVA_INT,
	ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS
)

val nativePrjWritePlaceholderInfo: MethodHandle = prjFSLookup.getDowncall(
	linker, "PrjWritePlaceholderInfo", ValueLayout.JAVA_INT,
	ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS,
	ValueLayout.JAVA_INT
)

val nativePrjAllocateAlignedBuffer: MethodHandle = prjFSLookup.getDowncall(
	linker, "PrjAllocateAlignedBuffer", ValueLayout.ADDRESS,
	ValueLayout.ADDRESS, ValueLayout.JAVA_LONG
)

val nativePrjWriteFileData: MethodHandle = prjFSLookup.getDowncall(
	linker, "PrjWriteFileData", ValueLayout.JAVA_INT,
	ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS,
	ValueLayout.JAVA_LONG, ValueLayout.JAVA_INT
)

val nativePrjFreeAlignedBuffer: MethodHandle = prjFSLookup.getDowncallVoid(
	linker, "PrjFreeAlignedBuffer", ValueLayout.ADDRESS
)

val nativePrjFileNameMatch: MethodHandle = prjFSLookup.getDowncall(
	linker, "PrjFileNameMatch", ValueLayout.JAVA_BOOLEAN,
	ValueLayout.ADDRESS, ValueLayout.ADDRESS
)

val nativePrjGetOnDiskFileState: MethodHandle = prjFSLookup.getDowncall(
	linker, "PrjGetOnDiskFileState", ValueLayout.JAVA_INT,
	ValueLayout.ADDRESS, ValueLayout.ADDRESS
)

val nativePrjDeleteFile: MethodHandle = prjFSLookup.getDowncall(
	linker, "PrjDeleteFile", ValueLayout.JAVA_INT,
	ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS
)