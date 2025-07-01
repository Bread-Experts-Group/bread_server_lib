package org.bread_experts_group.io

import org.bread_experts_group.getDowncall
import org.bread_experts_group.getLookup
import java.lang.foreign.*
import java.lang.invoke.VarHandle
import java.nio.file.attribute.FileTime
import java.util.concurrent.TimeUnit

private val handleArena = Arena.ofAuto()
private val kernel32Lookup: SymbolLookup = handleArena.getLookup("Kernel32.dll")
private val linker: Linker = Linker.nativeLinker()

val getFileAttributesExW = kernel32Lookup.getDowncall(
	linker, "GetFileAttributesExW",
	ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS
)

val win32FileTime: StructLayout = MemoryLayout.structLayout(
	ValueLayout.JAVA_INT.withName("dwLowDateTime"),
	ValueLayout.JAVA_INT.withName("dwHighDateTime"),
)

val win32FileAttributeData: StructLayout = MemoryLayout.structLayout(
	ValueLayout.JAVA_INT.withName("dwFileAttributes"),
	win32FileTime.withName("ftCreationTime"),
	win32FileTime.withName("ftLastAccessTime"),
	win32FileTime.withName("ftLastWriteTime"),
	ValueLayout.JAVA_INT.withName("nFileSizeHigh"),
	ValueLayout.JAVA_INT.withName("nFileSizeLow"),
)
val win32FileAttributesVar: VarHandle = win32FileAttributeData.varHandle(
	MemoryLayout.PathElement.groupElement("dwFileAttributes")
)
val win32CreationLowTimeVar: VarHandle = win32FileAttributeData.varHandle(
	MemoryLayout.PathElement.groupElement("ftCreationTime"),
	MemoryLayout.PathElement.groupElement("dwLowDateTime")
)
val win32CreationHighTimeVar: VarHandle = win32FileAttributeData.varHandle(
	MemoryLayout.PathElement.groupElement("ftCreationTime"),
	MemoryLayout.PathElement.groupElement("dwHighDateTime")
)
val win32AccessLowTimeVar: VarHandle = win32FileAttributeData.varHandle(
	MemoryLayout.PathElement.groupElement("ftLastAccessTime"),
	MemoryLayout.PathElement.groupElement("dwLowDateTime")
)
val win32AccessHighTimeVar: VarHandle = win32FileAttributeData.varHandle(
	MemoryLayout.PathElement.groupElement("ftLastAccessTime"),
	MemoryLayout.PathElement.groupElement("dwHighDateTime")
)
val win32WriteLowTimeVar: VarHandle = win32FileAttributeData.varHandle(
	MemoryLayout.PathElement.groupElement("ftLastWriteTime"),
	MemoryLayout.PathElement.groupElement("dwLowDateTime")
)
val win32WriteHighTimeVar: VarHandle = win32FileAttributeData.varHandle(
	MemoryLayout.PathElement.groupElement("ftLastWriteTime"),
	MemoryLayout.PathElement.groupElement("dwHighDateTime")
)
val win32SizeLowVar: VarHandle = win32FileAttributeData.varHandle(
	MemoryLayout.PathElement.groupElement("nFileSizeLow")
)
val win32SizeHighVar: VarHandle = win32FileAttributeData.varHandle(
	MemoryLayout.PathElement.groupElement("nFileSizeHigh")
)

fun hlMerge(
	s: MemorySegment,
	l: VarHandle,
	h: VarHandle
): ULong {
	val low = (l.get(s, 0) as Int).toUInt().toULong()
	val high = (h.get(s, 0) as Int).toUInt().toULong()
	return (high shl 32) or low
}

fun win32FileTimeToJava(
	fileAttribute: MemorySegment,
	l: VarHandle,
	h: VarHandle
): FileTime = FileTime.from(
	((hlMerge(fileAttribute, l, h) - 0x019DB1DED53E8000uL) / 10000000uL).toLong(),
	TimeUnit.SECONDS
)