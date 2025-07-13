package org.bread_experts_group.coder.format.parse.gif.block

import org.bread_experts_group.coder.format.parse.gif.GIFDisposalMethod
import kotlin.time.Duration

class GIFGraphicControlExtensionBlock(
	val delay: Duration,
	val transparentColorIndex: Int?,
	val dispose: GIFDisposalMethod,
	val wantUserInput: Boolean
) : GIFExtensionBlock(0xF9.toByte(), byteArrayOf()) {
	override fun toString(): String = "GIFGraphicControlExtensionBlock[" + buildList {
		add("$delay")
		if (transparentColorIndex != null) add("transparent color #: $transparentColorIndex")
		add(dispose.name)
		if (wantUserInput) add("want user input")
	}.joinToString(", ") + ']'
}