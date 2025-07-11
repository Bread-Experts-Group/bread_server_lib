package org.bread_experts_group.coder.format.parse.flac.block

import org.bread_experts_group.coder.Mappable

enum class FLACPictureType(
	override val id: Int,
	override val tag: String
) : Mappable<FLACPictureType, Int> {
	FRONT_COVER(3, "Front Cover"),
	BACK_COVER(4, "Back Cover"),
	OTHER(-1, "Other");

	override fun other(): FLACPictureType? = OTHER
	override fun toString(): String = stringForm()
}