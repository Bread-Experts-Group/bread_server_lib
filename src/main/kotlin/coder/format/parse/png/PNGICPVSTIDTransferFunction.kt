package org.bread_experts_group.coder.format.parse.png

import org.bread_experts_group.coder.Mappable

enum class PNGICPVSTIDTransferFunction(
	override val id: Int,
	override val tag: String
) : Mappable<PNGICPVSTIDTransferFunction, Int> {
	RESERVED(0, "Reserved"),
	BT1886(1, "BT.1886"),
	UNSPECIFIED(2, "Unspecified"),
	RESERVED_3(3, "Reserved"),
	GAMMA22(4, "Gamma 2.2"),
	GAMMA28(5, "Gamma 2.8"),
	ST240(6, "SMPTE ST 240"),
	EXT_LINEAR(7, "Extended Linear"),
	LOG100(8, "Log 100:1"),
	LOG316(9, "Log 316:1"),
	XVYCC(10, "xvYCC"),
	SRGB(11, "sRGB OETF"),
	EXT_SRGB(12, "Extended sRGB"),
	ST2084_PQ(13, "ST.2084 (PQ)"),
	ST428(14, "ST 428‑1"),
	HLG(15, "HLG (ARIB STD‑B67)");

	override fun other(): PNGICPVSTIDTransferFunction? = UNSPECIFIED
	override fun toString(): String = tag
}