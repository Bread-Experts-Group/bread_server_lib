package org.bread_experts_group.coder.format.parse.nbt.tag

import org.bread_experts_group.coder.Mappable

enum class NBTTagType(override val id: UByte, override val tag: String) : Mappable<NBTTagType, UByte> {
	END_OF_COMPOUND(0u, "<End Of Compound Tag>"),
	BYTE(1u, "Byte (i8)"),
	SHORT(2u, "Short (i16)"),
	INTEGER(3u, "Integer (i32)"),
	LONG(4u, "Long (i64)"),
	FLOAT(5u, "Float (f32)"),
	DOUBLE(6u, "Double (f64)"),
	BYTE_ARRAY(7u, "Byte Array"),
	UTF_8(8u, "UTF-8 String"),
	LIST(9u, "List [V]"),
	COMPOUND(10u, "Compound {K:V}"),
	INT_ARRAY(11u, "Integer (i32) Array"),
	LONG_ARRAY(12u, "Long (i64) Array");

	override fun toString(): String = stringForm()
}