package org.bread_experts_group.api.compile.ebc.efi

import java.lang.foreign.MemorySegment

interface EFITableHeader {
	val segment: MemorySegment
	val signature: Long
	val revision: Int
	val headerSize: Int
	val crc32: Int
	val reserved: Int
}