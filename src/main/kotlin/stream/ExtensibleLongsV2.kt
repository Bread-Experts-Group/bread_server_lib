package org.bread_experts_group.stream

import java.io.InputStream
import java.io.OutputStream
import java.math.BigInteger

// V2: ELLLSDDD
//     DDDDDDDD ...
//     E(xtension)
//     L(ength)
//     S(ign)
//     D(ata)
// E | L, when below 15, indicate the size in octets of the following numeric data,
// |'ed and <<'ed with the initial data.
// E | L, when exactly 15, indicates there is a following extensible long V2, in its unsigned format,
// which is appended to the current long in the same form as above.

fun InputStream.readExtensibleLongV2(n: Int = 0): BigInteger {
	val blockOne = this.read()
	var data = BigInteger.valueOf((blockOne and 0b00000111).toLong())
	val sign = blockOne and 0b00001000 != 0
	val size = (blockOne and 0b11110000) shr 4
	var bits = n + 3
	this.readNBytes(size).forEachIndexed { i, byte ->
		var add = BigInteger.valueOf(byte.toLong() and 0xFF)
		if (i == 14) add = add.clearBit(7)
		data = data or (add shl bits)
		bits += 8
	}
	if (size == 15) data = data or this.readExtensibleLongV2(bits)
	return if (sign) data.negate() else data
}

fun InputStream.readExtensibleLongV2L(): Long = this.readExtensibleLongV2().longValueExact()

val bi7: BigInteger = BigInteger.valueOf(0b111)
val maxSafe: BigInteger = BigInteger.TWO.pow(123).minus(BigInteger.ONE)
fun OutputStream.writeExtensibleLongV2(long: BigInteger) {
	var safeLong = long.abs() and maxSafe
	val writeAfter = if (long.bitLength() > 123) {
		safeLong = safeLong.setBit(122)
		long.abs() shr 123
	} else null
	val data = (safeLong shr 3).toByteArray().let {
		if (it[0] == 0.toByte()) it.drop(1).toByteArray()
		else it
	}.reversedArray()
	var blockOne = (if (long < BigInteger.ZERO) 0b00001000 else 0b00000000)
	blockOne = blockOne or (safeLong and bi7).toInt()
	blockOne = blockOne or (data.size shl 4)
	this.write(blockOne)
	this.write(data)
	if (writeAfter != null) this.writeExtensibleLongV2(writeAfter)
}

fun OutputStream.writeExtensibleLongV2L(n: Long) = this.writeExtensibleLongV2(BigInteger.valueOf(n))