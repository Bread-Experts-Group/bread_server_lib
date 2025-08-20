package org.bread_experts_group.io

interface BaseReadingIO {
	var order: IOEndian
	var pass: Array<Any?>?
	fun get(n: Int): ByteArray
	fun i32(): Int
	fun i16(): Short
	fun i8(): Byte
	fun u32(): UInt
	fun u16(): UShort
	fun u8(): UByte
	fun invalidateData()
}