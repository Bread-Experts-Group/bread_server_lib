package org.bread_experts_group.coder.format.parse.gif.block

import org.bread_experts_group.hex

open class GIFExtensionBlock(
	val label: Byte,
	data: ByteArray
) : GIFBlock(0x21, data) {
	@OptIn(ExperimentalStdlibApi::class)
	override fun toString(): String = "GIFExtensionBlock[${hex(label.toUByte())}, ${data.size} bytes]"
}