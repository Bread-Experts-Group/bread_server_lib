package org.bread_experts_group.coder.format.parse.png

import org.bread_experts_group.coder.Mappable

enum class PNGDisposeOperation(
	override val id: Int,
	override val tag: String
) : Mappable<PNGDisposeOperation, Int> {
	/**
	 * No disposal is done on this frame before rendering the next; the contents of the output buffer are left as is.
	 */
	APNG_DISPOSE_OP_NONE(0, "No Disposal"),

	/**
	 * The frame's region of the output buffer is to be cleared to fully transparent black before rendering
	 * the next frame.
	 */
	APNG_DISPOSE_OP_BACKGROUND(1, "Dispose To Background"),

	/**
	 * The frame's region of the output buffer is to be reverted to the previous contents before rendering
	 * the next frame.
	 */
	APNG_DISPOSE_OP_PREVIOUS(2, "Dispose To Previous");

	override fun toString(): String = stringForm()
}