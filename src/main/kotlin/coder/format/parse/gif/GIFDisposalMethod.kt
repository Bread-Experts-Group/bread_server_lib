package org.bread_experts_group.coder.format.parse.gif

import org.bread_experts_group.coder.Mappable

enum class GIFDisposalMethod(
	override val id: Int,
	override val tag: String
) : Mappable<GIFDisposalMethod, Int> {
	UNSPECIFIED(0, "Unknown"),
	DO_NOT_DISPOSE(1, "Keep Canvas"),
	RESTORE_TO_BACKGROUND(2, "Restore Canvas To Background"),
	RESTORE_TO_PREVIOUS(3, "Restore Canvas To Previous Image");

	override fun toString(): String = stringForm()
}