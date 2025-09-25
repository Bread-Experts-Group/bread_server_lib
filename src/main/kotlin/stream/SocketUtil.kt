package org.bread_experts_group.stream

import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.nio.channels.FileChannel
import java.nio.charset.Charset

fun Short.le(): Short = java.lang.Short.reverseBytes(this)
fun Int.le(): Int = Integer.reverseBytes(this)
fun Long.le(): Long = java.lang.Long.reverseBytes(this)
fun Int.maskI(): Int = (1 shl this) - 1

fun InputStream.read16(): Short = this.read16u().toShort()
fun InputStream.read16u(): UShort = ((this.read() shl 8) or this.read()).toUShort()
fun InputStream.read16ui(): Int = this.read16u().toInt()
fun InputStream.read24(): Int = this.read24u().toInt()
fun InputStream.read24u(): UInt = ((this.read16ui() shl 8) or this.read()).toUInt()
fun InputStream.read32(): Int = (this.read24() shl 8) or this.read()
fun InputStream.read32u(): UInt = this.read32().toUInt()
fun InputStream.read32ul(): Long = this.read32u().toLong()
fun InputStream.read64(): Long = this.read64u().toLong()
fun InputStream.read64u(): ULong = ((this.read32().toULong() shl 32) or this.read32u().toULong())
fun InputStream.readString(n: Int, c: Charset = Charsets.UTF_8): String = this.readNBytes(n).toString(c)
fun InputStream.readString(c: Charset = Charsets.UTF_8): String {
	val enc = ByteArrayOutputStream()
	try {
		while (true) when (c) {
			Charsets.UTF_32 -> {
				val next = this.read32ul()
				if (next < 1L) break
				enc.write32(next)
			}

			Charsets.UTF_16 -> {
				val next = this.read16ui()
				if (next < 1) break
				enc.write16(next)
			}

			Charsets.ISO_8859_1, Charsets.UTF_8, Charsets.US_ASCII -> {
				val next = this.read()
				if (next < 1) break
				enc.write(next)
			}

			else -> throw UnsupportedOperationException(c.displayName())
		}
	} catch (e: FailQuickInputStream.EndOfStream) {
		if (enc.size() == 0) throw e
	}
	return enc.toByteArray().toString(c)
}

fun OutputStream.write16(data: Int): Unit = this.write(data shr 8).also { this.write(data) }
fun OutputStream.write16(data: Short): Unit = this.write16(data.toInt())
fun OutputStream.write24(data: Int): Unit = this.write(data shr 16).also { this.write16(data) }
fun OutputStream.write32(data: Int): Unit = this.write(data shr 24).also { this.write24(data) }
fun OutputStream.write32(data: Long): Unit = this.write32(data.toInt())
fun OutputStream.write64(data: Long): Unit = this.write32((data shr 32).toInt()).also { this.write32((data).toInt()) }
fun OutputStream.write64u(data: ULong): Unit = this.write64(data.toLong())
fun OutputStream.writeString(s: String, c: Charset = Charsets.UTF_8): Unit = this.write(s.toByteArray(c))

fun <R> FileChannel.resetPosition(offset: Long, run: FileChannel.() -> R): R {
	val save = this.position()
	this.position(offset)
	val returned = run()
	this.position(save)
	return returned
}