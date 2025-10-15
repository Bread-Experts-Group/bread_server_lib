package org.bread_experts_group.api.compile.ebc.efi

import java.lang.foreign.MemorySegment

interface EFISystemTable {
	val header: EFITableHeader
	val firmwareVendor: MemorySegment
	val firmwareRevision: Int
	val consoleInHandle: MemorySegment
	val conIn: Any?
	val consoleOutHandle: MemorySegment
	val conOut: EFISimpleTextOutputProtocol
	val standardErrorHandle: MemorySegment
	val stdErr: EFISimpleTextOutputProtocol
	val runtimeServices: EFIRuntimeServicesTable
	val bootServices: EFIBootServicesTable
}