package org.bread_experts_group.coder.format.gif.block

import org.bread_experts_group.stream.ConsolidatedInputStream

class GIFApplicationExtensionBlock(
	val identifier: String,
	val authenticationCode: ByteArray,
	val applicationData: ConsolidatedInputStream
) : GIFExtensionBlock(0xFF.toByte(), byteArrayOf()) {
	@OptIn(ExperimentalStdlibApi::class)
	override fun toString(): String = "GIFApplicationExtensionBlock[\"$identifier\", " +
			"[${authenticationCode.toHexString()}], ${applicationData.available()} bytes]"
}