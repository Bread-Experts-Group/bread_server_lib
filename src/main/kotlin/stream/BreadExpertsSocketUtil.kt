package org.bread_experts_group.stream

import java.io.InputStream
import java.io.OutputStream
import kotlin.math.abs

fun InputStream.readExtensibleLong(): Long {
	val initialByte = this.read()
	var byte = initialByte
	var position = 6
	var joined = byte.toLong() and 0b00111111
	while (byte and 0b10000000 != 0) {
		byte = this.read()
		joined = joined or ((byte and 0b01111111).toLong() shl position)
		position += 7
	}
	if (joined == -4611686018427387905 && byte == 2) return Long.MIN_VALUE
	return joined * (if (initialByte and 0b01000000 != 0) -1 else 1)
}

fun OutputStream.writeExtensibleLong(value: Long) {
	val safeValue = if (value == Long.MIN_VALUE) value + 1 else value
	var remainder = abs(safeValue)
	var firstByte = (remainder and 0b00111111).toInt()
	if (safeValue < 0) firstByte = firstByte or 0b01000000
	if (remainder > 63) firstByte = firstByte or 0b10000000
	this.write(firstByte)
	remainder = remainder ushr 6
	while (remainder != 0L) {
		var toWrite = (remainder and 0b01111111).toInt()
		remainder = remainder ushr 7
		if (remainder > 0L) toWrite = toWrite or 0b10000000
		else if (value == Long.MIN_VALUE) toWrite++
		this.write(toWrite)
	}
}

fun InputStream.readExtensibleULong(): ULong {
	var position = 0
	var joined = 0uL
	do {
		val byte = this.read()
		joined = joined or ((byte and 0b01111111).toULong() shl position)
		position += 7
	} while (byte and 0b10000000 != 0)
	return joined
}

fun OutputStream.writeExtensibleULong(value: ULong) {
	var remainder = value
	do {
		var toWrite = (remainder and 0b01111111u).toInt()
		remainder = remainder shr 7
		if (remainder != 0uL) toWrite = toWrite or 0b10000000
		this.write(toWrite)
	} while (remainder != 0uL)
}