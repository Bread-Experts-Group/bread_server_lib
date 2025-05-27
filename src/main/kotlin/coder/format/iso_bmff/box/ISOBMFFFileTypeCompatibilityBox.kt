package org.bread_experts_group.coder.format.iso_bmff.box

class ISOBMFFFileTypeCompatibilityBox(
	val majorBrand: String,
	val minorVersion: Int,
	val compatibleBrands: List<String>
) : ISOBMFFBox("ftyp", byteArrayOf()) {
	override fun toString(): String = "ISOBMFFBox.\"$name\"[$majorBrand, $minorVersion, $compatibleBrands]"
}