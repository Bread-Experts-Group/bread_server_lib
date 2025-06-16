package org.bread_experts_group.coder.format.gif

enum class GIFDisposalMethod(val code: Int) {
	UNSPECIFIED(0),
	DO_NOT_DISPOSE(1),
	RESTORE_TO_BACKGROUND(2),
	RESTORE_TO_PREVIOUS(3);

	companion object {
		val mapping: Map<Int, GIFDisposalMethod> = entries.associateBy(GIFDisposalMethod::code)
	}
}