package org.bread_experts_group.io.reader

import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.nio.ByteOrder

class SegmentDirectDataProvisioner(val segment: MemorySegment) : DirectDataProvisioner<Long> {
	fun asUShortProvisioner() = object : DirectDataProvisioner<UShort> {
		override var order: ByteOrder
			get() = this@SegmentDirectDataProvisioner.order
			set(value) {
				this@SegmentDirectDataProvisioner.order = value
			}

		override fun writeS8(at: UShort, b: Byte) = this@SegmentDirectDataProvisioner.writeS8(at.toLong(), b)
		override fun writeS16(at: UShort, s: Short) = this@SegmentDirectDataProvisioner.writeS16(at.toLong(), s)
		override fun writeS32(at: UShort, i: Int) = this@SegmentDirectDataProvisioner.writeS32(at.toLong(), i)
		override fun writeS64(at: UShort, l: Long) = this@SegmentDirectDataProvisioner.writeS64(at.toLong(), l)
		override fun write(at: UShort, b: ByteArray, offset: Int, length: Int) =
			this@SegmentDirectDataProvisioner.write(at.toLong(), b, offset, length)

		override fun fill(at: UShort, n: UShort, v: Byte) = this@SegmentDirectDataProvisioner.fill(
			at.toLong(), n.toLong(), v
		)

		override fun readS8(at: UShort): Byte = this@SegmentDirectDataProvisioner.readS8(at.toLong())
		override fun readS16(at: UShort): Short = this@SegmentDirectDataProvisioner.readS16(at.toLong())
		override fun readS32(at: UShort): Int = this@SegmentDirectDataProvisioner.readS32(at.toLong())
		override fun readS64(at: UShort): Long = this@SegmentDirectDataProvisioner.readS64(at.toLong())
		override fun readN(at: UShort, n: Int): ByteArray = this@SegmentDirectDataProvisioner.readN(at.toLong(), n)
		override fun flush() {}
	}

	override fun readS8(at: Long): Byte = segment.get(ValueLayout.JAVA_BYTE, at)
	override fun readS16(at: Long): Short = segment.get(ValueLayout.JAVA_SHORT_UNALIGNED, at)
	override fun readS32(at: Long): Int = segment.get(ValueLayout.JAVA_INT_UNALIGNED, at)
	override fun readS64(at: Long): Long = segment.get(ValueLayout.JAVA_LONG_UNALIGNED, at)

	override fun readN(at: Long, n: Int): ByteArray {
		val slice = segment.asSlice(at)
		if (slice.byteSize() < n) throw IllegalArgumentException("Size $n too large for $slice from $segment")
		return slice.reinterpret(n.toLong()).toArray(ValueLayout.JAVA_BYTE)
	}

	override fun writeS8(at: Long, b: Byte) = segment.set(ValueLayout.JAVA_BYTE, at, b)
	override fun writeS16(at: Long, s: Short) = segment.set(ValueLayout.JAVA_SHORT_UNALIGNED, at, s)
	override fun writeS32(at: Long, i: Int) = segment.set(ValueLayout.JAVA_INT_UNALIGNED, at, i)
	override fun writeS64(at: Long, l: Long) = segment.set(ValueLayout.JAVA_LONG_UNALIGNED, at, l)

	override fun write(at: Long, b: ByteArray, offset: Int, length: Int) = MemorySegment.copy(
		b, offset,
		segment, ValueLayout.JAVA_BYTE, at,
		length
	)

	override fun fill(at: Long, n: Long, v: Byte) {
		segment.asSlice(n).fill(v)
	}

	override fun flush() {}
	override var order: ByteOrder
		get() = TODO("Not yet implemented")
		set(_) {}
}