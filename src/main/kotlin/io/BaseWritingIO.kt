package org.bread_experts_group.io

interface BaseWritingIO {
	fun put(b: ByteArray)
	fun u8(n: UByte)
	fun u16(n: UShort)
	fun u32(n: UInt)
	fun flush()
}