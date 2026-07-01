package org.bread_experts_group.project_incubator.sim3a.aio

import java.nio.ByteBuffer
import java.nio.ByteOrder

class ByteBufferAIO(private val buffer: ByteBuffer) : ArrayIO.ReadSeekable<Int>, ArrayIO.WriteExact<Int> {
	override var position: Int
		get() = buffer.position()
		set(value) {
			buffer.position(value)
		}

	override var order: ByteOrder
		get() = buffer.order()
		set(value) {
			buffer.order(value)
		}

	override fun readByte(): Byte = buffer.get()
	override fun readShort(): Short = buffer.getShort()
	override fun readInt(): Int = buffer.getInt()
	override fun readLong(): Long = buffer.getLong()

	override fun skip(n: Int) {
		buffer.position(buffer.position() + n)
	}

	override fun readByte(at: Int): Byte = buffer.get(at)
	override fun readShort(at: Int): Short = buffer.getShort(at)
	override fun readInt(at: Int): Int = buffer.getInt(at)
	override fun readLong(at: Int): Long = buffer.getLong(at)

	override fun readInto(at: Int, array: ByteArray, offset: Int, size: Int) {
		TODO("Not yet implemented")
	}

	override fun writeByte(at: Int, v: Byte) {
		buffer.put(at, v)
	}

	override fun writeShort(at: Int, v: Short) {
		buffer.putShort(at, v)
	}

	override fun writeInt(at: Int, v: Int) {
		buffer.putInt(at, v)
	}

	override fun writeLong(at: Int, v: Long) {
		buffer.putLong(at, v)
	}
}

fun ByteBuffer.toArrayIO(): ArrayIO.ReadSeekable<Int> = ByteBufferAIO(this)