package org.bread_experts_group.coder.format.gif.block

import org.bread_experts_group.coder.format.gif.GIFDisposalMethod
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class GIFGraphicControlExtensionBlock(
	val delayTime: Long,
	val transparentColorIndex: Int?,
	val dispose: GIFDisposalMethod,
	val wantUserInput: Boolean
) : GIFExtensionBlock(0xF9.toByte(), byteArrayOf()) {
	override fun toString(): String = "GIFGraphicControlExtensionBlock[" + buildList {
		add("${delayTime.toDuration(DurationUnit.MILLISECONDS)}")
		if (transparentColorIndex != null) add("transparent color #: $transparentColorIndex")
		add(dispose.name)
		if (wantUserInput) add("want user input")
	}.joinToString(", ") + ']'
}