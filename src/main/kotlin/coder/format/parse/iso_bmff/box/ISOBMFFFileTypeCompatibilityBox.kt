package org.bread_experts_group.coder.format.parse.iso_bmff.box

import org.bread_experts_group.stream.write32
import org.bread_experts_group.stream.writeString
import java.io.OutputStream

class ISOBMFFFileTypeCompatibilityBox(
	val majorBrand: String,
	val minorVersion: Int,
	val compatibleBrands: List<String>
) : ISOBMFFBox("ftyp", byteArrayOf()) {
	init {
		require(majorBrand.length == 4) { "majorBrand must be 4 bytes long" }
		require(compatibleBrands.all { it.length == 4 }) { "all compatible brands must be 4 bytes long" }
	}

	override fun toString(): String = "ISOBMFFBox.\"$tag\"[$majorBrand, $minorVersion, $compatibleBrands]"
	override fun computeSize(): Long = 8 + (compatibleBrands.size * 4L)
	override fun write(stream: OutputStream) {
		super.write(stream)
		stream.writeString(majorBrand)
		stream.write32(minorVersion)
		compatibleBrands.forEach { stream.writeString(it) }
	}
}