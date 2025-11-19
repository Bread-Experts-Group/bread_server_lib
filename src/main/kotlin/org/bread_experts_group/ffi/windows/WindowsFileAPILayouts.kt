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