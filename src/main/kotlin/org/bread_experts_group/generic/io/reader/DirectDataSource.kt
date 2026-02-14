package org.bread_experts_group.generic.io.reader

import java.nio.ByteOrder

interface DirectDataSource<T> {
	var order: ByteOrder

	fun readU8K(at: T): UByte = readS8(at).toUByte()
	fun readU16K(at: T): UShort = readS16(at).toUShort()
	fun readU32K(at: T): UInt = readS32(at).toUInt()
	fun readU64K(at: T): ULong = readS64(at).toULong()

	fun readU8I(at: T): Int = readS8(at).toInt() and 0xFF
	fun readU16I(at: T): Int = readS16(at).toInt() and 0xFFFF
	fun readU32L(at: T): Long = readS32(at).toLong() and 0xFFFFFFFF

	fun readS8(at: T): Byte
	fun readS16(at: T): Short
	fun readS32(at: T): Int
	fun readS64(at: T): Long

	fun readN(at: T, n: Int): ByteArray
}