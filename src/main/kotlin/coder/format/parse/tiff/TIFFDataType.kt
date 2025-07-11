package org.bread_experts_group.coder.format.parse.tiff

import org.bread_experts_group.coder.Mappable

enum class TIFFDataType(
	override val id: Int,
	override val tag: String
) : Mappable<TIFFDataType, Int> {
	BYTE(1, "Unsigned Byte (8-bit)"),
	STRING(2, "ASCII String"),
	SHORT(3, "Unsigned Short (16-bit)"),
	LONG(4, "Unsigned Integer (32-bit)"),
	RATIONAL(5, "Unsigned Composite Fractional (32/32-bit)"),
	SBYTE(6, "Signed Byte (8-bit)"),
	UNDEFINE(7, "Byte (8-bit)"),
	SSHORT(8, "Signed Short (16-bit)"),
	SLONG(9, "Signed Integer (32-bit)"),
	SRATIONAL(10, "Signed Composite Fractional (32/32-bit)"),
	FLOAT(11, "Floating Point (32-bit)"),
	DOUBLE(12, "Floating Point (64-bit)"),
	OTHER(-1, "Unknown");

	override fun toString(): String = stringForm()
	override fun other(): TIFFDataType? = OTHER
}