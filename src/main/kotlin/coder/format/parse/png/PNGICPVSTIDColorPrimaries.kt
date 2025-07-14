package org.bread_experts_group.coder.format.parse.png

import org.bread_experts_group.coder.Mappable

enum class PNGICPVSTIDColorPrimaries(
	override val id: Int,
	override val tag: String
) : Mappable<PNGICPVSTIDColorPrimaries, Int> {
	RESERVED(0, "Reserved"),
	BT709_SRGB(1, "BT.709 / sRGB"),
	UNSPECIFIED(2, "Unspecified"),
	BT470M(4, "BT.470‑6 System M"),
	BT470BG(5, "BT.470‑6 System B/G / BT.601‑625"),
	BT601_525(6, "BT.601‑525 / SMPTE 170M"),
	SMPTE240M(7, "SMPTE 240M"),
	GENERIC_FILM(8, "Generic film (Illuminant C)"),
	BT2020(9, "BT.2020 / BT.2100"),
	XYZ(10, "SMPTE ST 428‑1 (CIE XYZ)"),
	DCI_P3(11, "SMPTE RP 431‑2 (DCI‑P3)"),
	DISPLAY_P3(12, "SMPTE EG 432‑1 (Display P3)"),
	EBU3213(22, "EBU Tech. 3213‑E");

	override fun toString(): String = stringForm()
	override fun other(): PNGICPVSTIDColorPrimaries? = UNSPECIFIED
}