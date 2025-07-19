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
	fun refill(amount: Int) {
		lengthMarker?.setter?.call(lengthMarker.getter.call() - amount)
		var toFill = amount - buffer.remaining()
		buffer.compact()
		while (toFill > 0) {
			val read = from.read(buffer)
			if (read == -1) throw EOFException()
			toFill -= read
		}
		buffer.flip()
	}

	fun get(b: ByteArray) {
		refill(b.size)
		buffer.get(b)
	}

	fun f64(): Double {
		refill(8)
		return buffer.double
	}

	fun f32(): Float {
		refill(4)
		return buffer.float
	}

	fun i64(): Long {
		refill(8)
		return buffer.long
	}

	fun i32(): Int {
		refill(4)
		return buffer.int
	}

	fun i16(): Short {
		refill(2)
		return buffer.short
	}

	fun i8(): Byte {
		refill(1)
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
		try {
			while (true) {
				when (c) {
					Charsets.UTF_32 -> bucket[bucketPosition] = this.i32()
					Charsets.UTF_16 -> bucket[bucketPosition] = this.u16i32()
					Charsets.ISO_8859_1, Charsets.UTF_8, Charsets.US_ASCII -> bucket[bucketPosition] = this.u8i32()
					else -> throw UnsupportedOperationException(c.displayName())
				}
				if (bucket[bucketPosition] == pattern[bucketPosition]) {
					if (bucketPosition == pattern.lastIndex) break
					bucketPosition++
				} else {
					for (i in 0..bucketPosition) when (c) {
						Charsets.UTF_32 -> enc.write32(bucket[i])
						Charsets.UTF_16 -> enc.write16(bucket[i])
						Charsets.ISO_8859_1, Charsets.UTF_8, Charsets.US_ASCII -> enc.write(bucket[i])
					}
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