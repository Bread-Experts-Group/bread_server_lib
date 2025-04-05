package bread_experts_group

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress

fun InputStream.read16() = (this.read() shl 8) or this.read()
fun InputStream.read24() = (this.read16() shl 8) or this.read()
fun InputStream.read32() = (this.read24() shl 8) or this.read()
fun InputStream.readInet4(): Inet4Address = Inet4Address.getByAddress(this.readNBytes(4)) as Inet4Address
fun InputStream.readInet6(): Inet6Address = Inet6Address.getByAddress(this.readNBytes(16)) as Inet6Address
fun InputStream.readString(n: Int): String = this.readNBytes(n).decodeToString()

fun OutputStream.write16(data: Int) = this.write(data shr 8).also { this.write(data) }
fun OutputStream.write24(data: Int) = this.write(data shr 16).also { this.write16(data) }
fun OutputStream.write32(data: Int) = this.write(data shr 24).also { this.write24(data) }
fun OutputStream.writeInet(data: InetAddress) = data.address.forEach { this.write(it.toInt()) }
fun OutputStream.writeString(s: String) = this.write(s.encodeToByteArray())

fun InputStream.scanDelimiter(lookFor: String): String {
	var bucket = ""
	var pool = ""
	while (bucket.length != lookFor.length) {
		val charCode = this.read()
		if (charCode == -1) throw IOException("Communication terminated")
		val next = Char(charCode)
		if (lookFor[bucket.length] == next) bucket += next
		else {
			pool += bucket + next
			bucket = ""
		}
	}
	return pool
}