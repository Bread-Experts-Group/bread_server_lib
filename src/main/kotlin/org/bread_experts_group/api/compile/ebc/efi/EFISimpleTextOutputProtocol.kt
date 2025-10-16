package org.bread_experts_group.api.compile.ebc.efi

import java.lang.foreign.MemorySegment

interface EFISimpleTextOutputProtocol {
	val segment: MemorySegment
	fun reset(extendedVerification: Boolean): Long
	fun outputString(string: String): Long
	fun outputStringAt(address: MemorySegment): Long
}