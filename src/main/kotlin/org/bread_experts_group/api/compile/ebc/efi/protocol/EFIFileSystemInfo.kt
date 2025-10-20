package org.bread_experts_group.api.compile.ebc.efi.protocol

import java.lang.foreign.MemorySegment

interface EFIFileSystemInfo {
	val segment: MemorySegment
	val structureSize: Long
	val readOnly: Boolean
	val volumeSize: Long
	val freeSpace: Long
	val blockSize: Int
}