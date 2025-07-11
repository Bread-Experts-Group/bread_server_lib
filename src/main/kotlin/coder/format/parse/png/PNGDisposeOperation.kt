package org.bread_experts_group.coder.format.parse.png

import org.bread_experts_group.coder.Mappable

enum class PNGDisposeOperation(
	override val id: Int,
	override val tag: String
) : Mappable<PNGDisposeOperation, Int> {
	APNG_DISPOSE_OP_NONE(0, "No Disposal"),
	APNG_DISPOSE_OP_BACKGROUND(1, "Dispose To Background"),
	APNG_DISPOSE_OP_PREVIOUS(2, "Dispose To Previous"),
	OTHER(-1, "Other");

	override fun other(): PNGDisposeOperation? = OTHER
	override fun stringForm(): String = stringForm()
}