package org.bread_experts_group.model.natives.nt.datatype

import org.bread_experts_group.generic.Flaggable
import org.bread_experts_group.model.natives.DatatypeBacked

@DatatypeBacked("int")
enum class WHV_TRANSLATE_GVA_FLAGS : Flaggable {
	WHvTranslateGvaFlagValidateRead,
	WHvTranslateGvaFlagValidateWrite,
	WHvTranslateGvaFlagValidateExecute,
	WHvTranslateGvaFlagPrivilegeExempt,
	WHvTranslateGvaFlagSetPageTableBits;

	override val position: Long = 1L shl ordinal
}