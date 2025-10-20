package org.bread_experts_group.api.compile.ebc.efi.protocol

import org.bread_experts_group.api.compile.ebc.efi.EFIStatusReturned1
import java.lang.foreign.MemorySegment

interface EFISimpleFileSystemProtocol {
	val segment: MemorySegment
	val revision: Long
	fun openVolume(): EFIStatusReturned1
}