package org.bread_experts_group.coder.format.parse.png

import org.bread_experts_group.coder.Mappable

enum class PNGICPVSTIDMatrixCoefficients(
	override val id: Int,
	override val tag: String
) : Mappable<PNGICPVSTIDMatrixCoefficients, Int> {
	IDENTITY(0, "Identity"),
	BT709(1, "BT.709"),
	FCC(2, "FCC"),
	BT601(4, "BT.601"),
	SMPTE240(5, "SMPTE 240"),
	BT2020(9, "BT.2020"),
	ICTCP(14, "ICtCp"),
	YCGCO(8, "YCgCo");

	override fun toString(): String = tag
}