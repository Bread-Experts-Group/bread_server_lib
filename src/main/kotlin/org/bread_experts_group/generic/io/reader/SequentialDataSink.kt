package org.bread_experts_group.generic.io.reader

import java.nio.ByteOrder
import kotlin.time.Duration

interface SequentialDataSink {
	var order: ByteOrder
	var timeout: Duration

	fun flush()

	fun write8(u: UByte) = write8(u.toByte())
	fun write16(u: UShort) = write16(u.toShort())
	fun write32(u: UInt) = write32(u.toInt())
	fun write64(u: ULong) = write64(u.toLong())

	fun write8(s: Byte)
	fun write16(s: Short)
	fun write32(s: Int)
	fun write64(s: Long)

	fun write8i(u: Int) = write8(u.toUByte())
	fun write16i(u: Int) = write16(u.toUShort())
	fun write32l(u: Long) = write32(u.toUInt())

	fun write(b: ByteArray, offset: Int = 0, length: Int = b.size)
	fun fill(n: Long, v: Byte = 0)
}