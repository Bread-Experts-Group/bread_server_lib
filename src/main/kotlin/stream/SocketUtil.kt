package org.bread_experts_group.stream

import java.io.FileInputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress
import java.nio.charset.Charset

fun InputStream.read16() = this.read16u().toShort()
fun InputStream.read16u() = ((this.read() shl 8) or this.read()).toUShort()
fun InputStream.read16ui() = this.read16u().toInt()
fun InputStream.read24() = this.read24u().toInt()
fun InputStream.read24u() = ((this.read16ui() shl 8) or this.read()).toUInt()
fun InputStream.read32() = (this.read24() shl 8) or this.read()
fun InputStream.read32u() = this.read32().toUInt()
fun InputStream.read32ul() = this.read32u().toLong()
fun InputStream.read64() = this.read64u().toLong()
fun InputStream.read64u() = ((this.read32().toULong() shl 32) or this.read32u().toULong())
fun InputStream.readInet4(): Inet4Address = Inet4Address.getByAddress(this.readNBytes(4)) as Inet4Address
fun InputStream.readInet6(): Inet6Address = Inet6Address.getByAddress(this.readNBytes(16)) as Inet6Address
fun InputStream.readString(n: Int, c: Charset = Charsets.UTF_8): String = this.readNBytes(n).toString(c)

fun OutputStream.write16(data: Int) = this.write(data shr 8).also { this.write(data) }
fun OutputStream.write24(data: Int) = this.write(data shr 16).also { this.write16(data) }
fun OutputStream.write32(data: Int) = this.write(data shr 24).also { this.write24(data) }
fun OutputStream.write32(data: Long) = this.write32(data.toInt())
fun OutputStream.write64(data: Long) = this.write32((data shr 32).toInt()).also { this.write32((data).toInt()) }
fun OutputStream.write64u(data: ULong) = this.write64(data.toLong())
fun OutputStream.writeInet(data: InetAddress) = data.address.forEach { this.write(it.toInt()) }
fun OutputStream.writeString(s: String, c: Charset = Charsets.UTF_8) = this.write(s.toByteArray(c))

fun InputStream.scanDelimiter(lookFor: String): String {
	var bucket = ""
	var pool = ""
	while (bucket.length < lookFor.length) {
		val read = this.read()
		if (read == -1) break
		val next = read.toChar()
		if (lookFor[bucket.length] == next) bucket += next
		else {
			pool += bucket + next
			bucket = ""
		}
	}
	return pool
}

fun <R> FileInputStream.resetPosition(offset: Long, run: FileInputStream.() -> R): R {
	val save = this.channel.position()
	this.channel.position(offset)
	val returned = run()
	this.channel.position(save)
	return returned
}