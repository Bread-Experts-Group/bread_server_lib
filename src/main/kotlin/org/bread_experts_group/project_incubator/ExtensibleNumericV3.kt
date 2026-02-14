package org.bread_experts_group.project_incubator

import org.bread_experts_group.generic.io.reader.BSLWriter
import java.math.BigInteger

/**
 * See document (BEG Format / Extensible Numeric, version 3) for information on the ENv3 format.
 * @param n The number to write in ENv3N[pN]M[pM]C[pC].
 * @param pN The parameter "N" for writing. (data byte count bit allocation).
 * @param pM The parameter "M" for writing. (data byte count multiplier).
 * @param pC The parameter "C" for writing. (data byte count constant addend).
 * @author Miko Elbrecht
 * @since D1F5N6P0
 */
fun BSLWriter<*, *>.writeExtensibleNumeric(
	n: BigInteger,
	pN: Int = 4,
	pM: BigInteger = BigInteger.ONE,
	pC: BigInteger = BigInteger.ZERO
) {
	TODO("ENv3 experimental")
}

//fun BSLReader<*, *>.readExtensibleNumeric(): BigInteger {
//	var composed = BigInteger.ZERO
//	var shift = 0
//	do {
//		val header = this.readU8k().toUInt()
//		val size = header shr 4
//		composed = composed or (BigInteger.valueOf((header and 0b1111u).toLong()) shl shift)
//		shift += 4
//		repeat(min(size.toInt(), 14)) {
//			composed = composed or (BigInteger.valueOf(this.readU8k().toLong()) shl shift)
//			shift += 8
//		}
//	} while (size == 15u)
//	return composed
//}

//fun BSLWriter<*, *>.writeExtensibleNumeric(n: BigInteger) {
//	val data = n.toByteArray()
//	var dataLength = data.size
//	val store = ByteArray(15)
//	var storeUsed = 0
//	var offs4 = false
//	while (dataLength > 0) {
//		if (storeUsed == 0) {
//			val b4 = data[dataLength - 1].toInt().let {
//				if (offs4) {
//					dataLength--
//					(it and 0b11110000) ushr 4
//				} else it and 0b1111
//			}
//			store[storeUsed++] = b4.toByte()
//			offs4 = !offs4
//			if (dataLength == 1) {
//				if (offs4) {
//					if ((data[0].toInt() and 0b11110000) == 0) break
//				} else if (data[0].toInt() == 0) break
//			}
//			continue
//		}
//		val l = if (offs4) (data[dataLength - 1].toInt() and 0b11110000) ushr 4
//		else data[dataLength - 1].toInt() and 0b1111
//		val m = if (offs4) (if (dataLength > 1) data[dataLength - 2].toInt() and 0b1111 else 0)
//		else (data[dataLength - 1].toInt() and 0b11110000) ushr 4
//		dataLength--
//		store[storeUsed++] = ((m shl 4) or l).toByte()
//		if (dataLength == 0) break
//		if (storeUsed == 15) {
//			store[0] = store[0] or 0b11110000.toByte()
//			this.write(store)
//			storeUsed = 0
//		}
//	}
//	if (storeUsed > 0) {
//		store[0] = store[0] or ((storeUsed - 1) shl 4).toByte()
//		this.write(store, length = storeUsed)
//	}
//}