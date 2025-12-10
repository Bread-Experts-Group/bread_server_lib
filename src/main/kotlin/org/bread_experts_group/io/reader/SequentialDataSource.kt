package org.bread_experts_group.io.reader

import java.nio.ByteOrder

interface SequentialDataSource {
	var order: ByteOrder

	fun readU8k(): UByte = readS8().toUByte()
	fun readU16k(): UShort = readS16().toUShort()
	fun readU32k(): UInt = readS32().toUInt()
	fun readU64k(): ULong = readS64().toULong()

	fun readU8i(): Int = readS8().toInt() and 0xFF
	fun readU16i(): Int = readS16().toInt() and 0xFFFF
	fun readU32l(): Long = readS32().toLong() and 0xFFFFFFFF

	fun readS8(): Byte
	fun readS16(): Short
	fun readS32(): Int
	fun readS64(): Long

	fun readN(n: Int): ByteArray
	fun skip(n: Long)
}