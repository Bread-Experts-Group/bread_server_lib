package org.bread_experts_group.generic.io.reader

import java.nio.ByteOrder
import kotlin.time.Duration

interface SequentialDataSink {
	var order: ByteOrder
	var timeout: Duration

	fun flush(): List<WritingStatus>?

	fun write8(u: UByte): List<WritingStatus>? = write8(u.toByte())
	fun write16(u: UShort): List<WritingStatus>? = write16(u.toShort())
	fun write32(u: UInt): List<WritingStatus>? = write32(u.toInt())
	fun write64(u: ULong): List<WritingStatus>? = write64(u.toLong())

	fun write8(s: Byte): List<WritingStatus>?
	fun write16(s: Short): List<WritingStatus>?
	fun write32(s: Int): List<WritingStatus>?
	fun write64(s: Long): List<WritingStatus>?

	fun write8i(u: Int): List<WritingStatus>? = write8(u.toUByte())
	fun write16i(u: Int): List<WritingStatus>? = write16(u.toUShort())
	fun write32l(u: Long): List<WritingStatus>? = write32(u.toUInt())

	fun write(b: ByteArray, offset: Int = 0, length: Int = b.size): List<WritingStatus>?
	fun fill(n: Long, v: Byte = 0): List<WritingStatus>?
}