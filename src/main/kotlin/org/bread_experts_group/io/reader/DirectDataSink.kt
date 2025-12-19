package org.bread_experts_group.io.reader

import java.nio.ByteOrder

interface DirectDataSink<T> {
	var order: ByteOrder

	fun flush()

	fun writeU8(at: T, b: UByte) = writeS8(at, b.toByte())
	fun writeU16(at: T, s: UShort) = writeS16(at, s.toShort())
	fun writeU32(at: T, i: UInt) = writeS32(at, i.toInt())
	fun writeU64(at: T, l: ULong) = writeS64(at, l.toLong())

	fun writeS8(at: T, b: Byte)
	fun writeS16(at: T, s: Short)
	fun writeS32(at: T, i: Int)
	fun writeS64(at: T, l: Long)

	fun writeU8I(at: T, b: Int) = writeU8(at, b.toUByte())
	fun writeU16I(at: T, s: Int) = writeU16(at, s.toUShort())
	fun writeU32L(at: T, i: Long) = writeU32(at, i.toUInt())

	fun write(at: T, b: ByteArray, offset: Int = 0, length: Int = b.size)
	fun fill(at: T, n: T, v: Byte = 0)
}