package org.bread_experts_group.api.compile.ebc

import java.lang.classfile.Label

sealed class BranchParameters {
	abstract val target: Label

	data class Unconditional(override val target: Label) : BranchParameters()
	data class Conditional(override val target: Label, val set: Boolean) : BranchParameters()
}