package org.bread_experts_group.channel

import org.bread_experts_group.stream.write16
import org.bread_experts_group.stream.write32
import java.io.ByteArrayOutputStream
import java.io.EOFException
import java.nio.BufferUnderflowException
import java.nio.ByteBuffer
import java.nio.channels.ReadableByteChannel
import java.nio.charset.Charset
import kotlin.reflect.KMutableProperty

class ReadingByteBuffer(
	private val from: ReadableByteChannel,
	val buffer: ByteBuffer,
	val lengthMarker: KMutableProperty<Long>?
) {
	init {
		buffer.flip()
	}

	private var present = 0
	fun refill(amount: Int) {
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

	fun get(b: ByteArray) {
		refill(b.size)
		present -= b.size
		buffer.get(b)
	}

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

	fun i32(): Int {
		refill(4)
		present -= 4
		return buffer.int
	}

	fun i16(): Short {
		refill(2)
		present -= 2
		return buffer.short
	}

	fun i8(): Byte {
		refill(1)
		present -= 1
		return buffer.get()
	}

	fun u64(): ULong = i64().toULong()
	fun u32(): UInt = i32().toUInt()
	fun u16(): UShort = i16().toUShort()
	fun u8(): UByte = i8().toUByte()

	fun u16i32() = u16().toInt()
	fun u8i32() = u8().toInt()

	fun decodeString(c: Charset = Charsets.UTF_8, pattern: IntArray = intArrayOf(0)): String {
		val enc = ByteArrayOutputStream()
		val bucket = IntArray(pattern.size)
		var bucketPosition = 0

		val read: () -> Unit
		val write: (Int) -> Unit
		when (c) {
			Charsets.ISO_8859_1, Charsets.UTF_8, Charsets.US_ASCII -> {
				read = { bucket[bucketPosition] = this.u8i32() }
				write = { enc.write(bucket[it]) }
			}

			Charsets.UTF_16 -> {
				read = { bucket[bucketPosition] = this.u16i32() }
				write = { enc.write16(bucket[it]) }
			}

			Charsets.UTF_32 -> {
				read = { bucket[bucketPosition] = this.i32() }
				write = { enc.write32(bucket[it]) }
			}

			else -> throw UnsupportedOperationException(c.displayName())
		}

		try {
			while (true) {
				read()
				if (bucket[bucketPosition] == pattern[bucketPosition]) {
					if (bucketPosition == pattern.lastIndex) break
					bucketPosition++
				} else {
					for (i in 0..bucketPosition) write(i)
					bucketPosition = 0
				}
			}
		} catch (e: BufferUnderflowException) {
			if (enc.size() == 0) throw e
		}
		return enc.toByteArray().toString(c)
	}

	fun transferTo(dst: ByteBuffer, atMost: Int): Int {
		val transfer = minOf(atMost, dst.remaining(), buffer.capacity())
		refill(transfer)
		val saved = buffer.limit()
		buffer.limit(transfer)
		dst.put(buffer)
		buffer.limit(saved)
		present -= transfer
		return transfer
	}

	fun channel(): ReadableByteChannel = object : ReadableByteChannel {
		override fun read(dst: ByteBuffer?): Int {
			TODO("Not yet implemented")
		}

		override fun isOpen(): Boolean {
			TODO("Not yet implemented")
		}

		override fun close() {
			TODO("Not yet implemented")
		}

	}
}