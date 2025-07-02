package org.bread_experts_group.coder.format.id3.frame

import java.math.BigInteger

class ID3PopularimeterFrame(
	tag: String,
	flags: Int,
	val email: String,
	val rating: Int,
	val counter: BigInteger
) : ID3Frame<ID3GenericFlags>(tag, ID3GenericFlags.entries, flags, byteArrayOf()) {
	override fun toString(): String = super.toString() + "[${if (email.isNotBlank()) "\"$email\" | " else ""}" +
			"$rating/255 | $counter]"
}