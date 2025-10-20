package org.bread_experts_group.api.compile.ebc.efi.protocol

import java.lang.foreign.MemorySegment

interface EFIFileProtocol {
	val segment: MemorySegment
	val revision: Long
	fun getInfo(informationTypeGUID: MemorySegment, bufferSize: MemorySegment, buffer: MemorySegment): Long
}