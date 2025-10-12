package org.bread_experts_group.io.reader

import org.bread_experts_group.io.BaseReadingIO
import org.bread_experts_group.io.IOEndian
import java.io.EOFException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.ReadableByteChannel
import kotlin.reflect.KMutableProperty

open class ReadingByteBuffer(
	private val from: ReadableByteChannel,
	val buffer: ByteBuffer,
	val lengthMarker: KMutableProperty<Long>?
) : BaseReadingIO, AutoCloseable {
	init {
		buffer.order(ByteOrder.nativeOrder())
		buffer.flip()
	}

	protected var present = 0
	open fun refill(amount: Int) {
		lengthMarker?.setter?.call(lengthMarker.getter.call() - amount)
		val toFill = amount - present
		if (toFill < 1) return
		buffer.compact()
		while (toFill > present) {
			val read = from.read(buffer)
			if (read == -1) throw EOFException()
			present += read
		}
		buffer.flip()
	}

	override var pass: Array<Any?>? = null

	fun get(b: ByteArray) {
		refill(b.size)
		present -= b.size
		buffer.get(b)
	}

	override fun enter(name: Any) {
		TODO("Not yet implemented")
	}

	override fun exit() {
		TODO("Not yet implemented")
	}

	override fun invalidateData() {
		buffer.clear()
		buffer.limit(0)
		this.present = 0
	}

	override fun get(n: Int): ByteArray {
		val array = ByteArray(n)
		this.get(array)
		return array
	}

	fun skip(n: Int) {
		refill(n)
		present -= n
		buffer.position(buffer.position() + n)
	}

	override var order: IOEndian = IOEndian.NATIVE

	fun f64(): Double {
		refill(8)
		present -= 8
		return buffer.double
	}

	fun f32(): Float {
		refill(4)
		present -= 4
		return buffer.float
	}

	fun i64(): Long {
		refill(8)
		present -= 8
		return buffer.long
	}

	override fun i32(): Int = when (val order = this.order) {
		IOEndian.BIG, IOEndian.LITTLE -> {
			refill(4)
			present -= 4
			if (order == IOEndian.NATIVE) buffer.int else Integer.reverseBytes(buffer.int)
		}

		IOEndian.BOTH_LE_BE -> {
			refill(8)
			present -= 8
			when (IOEndian.NATIVE) {
				IOEndian.LITTLE -> {
					val readOff = buffer.int
					buffer.int
					readOff
				}

				IOEndian.BIG -> {
					buffer.int
					buffer.int
				}

				else -> throw UnsupportedOperationException("CPU with LEBE architecture should not exist")
			}
		}

		else -> throw UnsupportedOperationException("Order: ${this.order}")
	}

	override fun i16(): Short = when (val order = this.order) {
		IOEndian.BIG, IOEndian.LITTLE -> {
			refill(2)
			present -= 2
			if (order == IOEndian.NATIVE) buffer.short else java.lang.Short.reverseBytes(buffer.short)
		}

		IOEndian.BOTH_LE_BE -> {
			refill(4)
			present -= 4
			when (IOEndian.NATIVE) {
				IOEndian.LITTLE -> {
					val readOff = buffer.short
					buffer.short
					readOff
				}

				IOEndian.BIG -> {
					buffer.short
					buffer.short
				}

				else -> throw UnsupportedOperationException("CPU with LEBE architecture should not exist")
			}
		}

		else -> throw UnsupportedOperationException("Order: ${this.order}")
	}

	override fun i8(): Byte {
		refill(1)
		present -= 1
		return buffer.get()
	}

	fun u64(): ULong = i64().toULong()
	override fun u32(): UInt = i32().toUInt()
	override fun u16(): UShort = i16().toUShort()
	override fun u8(): UByte = i8().toUByte()

	override fun close() = from.close()
}