package org.bread_experts_group.ffi.windows

import java.lang.foreign.AddressLayout
import java.lang.foreign.MemoryLayout
import java.lang.foreign.MemoryLayout.PathElement.groupElement
import java.lang.foreign.StructLayout
import java.lang.foreign.ValueLayout
import java.lang.invoke.VarHandle

val LPSECURITY_ATTRIBUTES: AddressLayout = ValueLayout.ADDRESS
val CREATEFILE3_EXTENDED_PARAMETERS: StructLayout = MemoryLayout.structLayout(
	DWORD.withName("dwSize"),
	DWORD.withName("dwFileAttributes"),
	DWORD.withName("dwFileFlags"),
	DWORD.withName("dwSecurityQosFlags"),
	LPSECURITY_ATTRIBUTES.withName("lpSecurityAttributes"),
	HANDLE.withName("hTemplateFile")
)
val CREATEFILE3_EXTENDED_PARAMETERS_dwSize: VarHandle = CREATEFILE3_EXTENDED_PARAMETERS.varHandle(
	groupElement("dwSize")
)
val CREATEFILE3_EXTENDED_PARAMETERS_dwFileAttributes: VarHandle = CREATEFILE3_EXTENDED_PARAMETERS.varHandle(
	groupElement("dwFileAttributes")
)
val CREATEFILE3_EXTENDED_PARAMETERS_dwFileFlags: VarHandle = CREATEFILE3_EXTENDED_PARAMETERS.varHandle(
	groupElement("dwFileFlags")
)

val CREATEFILE2_EXTENDED_PARAMETERS: StructLayout = MemoryLayout.structLayout(
	DWORD.withName("dwSize"),
	DWORD.withName("dwFileAttributes"),
	DWORD.withName("dwFileFlags"),
	DWORD.withName("dwSecurityQosFlags"),
	LPSECURITY_ATTRIBUTES.withName("lpSecurityAttributes"),
	HANDLE.withName("hTemplateFile")
)
val CREATEFILE2_EXTENDED_PARAMETERS_dwSize: VarHandle = CREATEFILE2_EXTENDED_PARAMETERS.varHandle(
	groupElement("dwSize")
)
val CREATEFILE2_EXTENDED_PARAMETERS_dwFileAttributes: VarHandle = CREATEFILE2_EXTENDED_PARAMETERS.varHandle(
	groupElement("dwFileAttributes")
)
val CREATEFILE2_EXTENDED_PARAMETERS_dwFileFlags: VarHandle = CREATEFILE2_EXTENDED_PARAMETERS.varHandle(
	groupElement("dwFileFlags")
)

val FILE_BASIC_INFO: StructLayout = MemoryLayout.structLayout(
	LARGE_INTEGER.withName("CreationTime"),
	LARGE_INTEGER.withName("LastAccessTime"),
	LARGE_INTEGER.withName("LastWriteTime"),
	LARGE_INTEGER.withName("ChangeTime"),
	DWORD.withName("FileAttributes"),
	MemoryLayout.paddingLayout(4)
)
val FILE_BASIC_INFO_CreationTime: VarHandle = FILE_BASIC_INFO.varHandle(groupElement("CreationTime"))
val FILE_BASIC_INFO_LastAccessTime: VarHandle = FILE_BASIC_INFO.varHandle(groupElement("LastAccessTime"))
val FILE_BASIC_INFO_LastWriteTime: VarHandle = FILE_BASIC_INFO.varHandle(groupElement("LastWriteTime"))
val FILE_BASIC_INFO_ChangeTime: VarHandle = FILE_BASIC_INFO.varHandle(groupElement("ChangeTime"))

val FILE_STANDARD_INFO: StructLayout = MemoryLayout.structLayout(
	LARGE_INTEGER.withName("AllocationSize"),
	LARGE_INTEGER.withName("EndOfFile"),
	DWORD.withName("NumberOfLinks"),
	BOOLEAN.withName("DeletePending"),
	BOOLEAN.withName("Directory"),
	MemoryLayout.paddingLayout(2)
)
val FILE_STANDARD_INFO_Directory: VarHandle = FILE_STANDARD_INFO.varHandle(groupElement("Directory"))

val WIN32_FILE_ATTRIBUTE_DATA: StructLayout = MemoryLayout.structLayout(
	DWORD.withName("dwFileAttributes"),
	FILETIME.withName("ftCreationTime"),
	FILETIME.withName("ftLastAccessTime"),
	FILETIME.withName("ftLastWriteTime"),
	DWORD.withName("nFileSizeHigh"),
	DWORD.withName("nFileSizeLow")
)
val WIN32_FILE_ATTRIBUTE_DATA_ftCreationTime: VarHandle = WIN32_FILE_ATTRIBUTE_DATA.varHandle(
	groupElement("ftCreationTime")
)
val WIN32_FILE_ATTRIBUTE_DATA_ftLastWriteTime: VarHandle = WIN32_FILE_ATTRIBUTE_DATA.varHandle(
	groupElement("ftLastWriteTime")
)
val WIN32_FILE_ATTRIBUTE_DATA_ftLastAccessTime: VarHandle = WIN32_FILE_ATTRIBUTE_DATA.varHandle(
	groupElement("ftLastAccessTime")
)