package org.bread_experts_group.model.natives.nt.datatype

import org.bread_experts_group.generic.Mappable
import org.bread_experts_group.model.natives.DatatypeBacked

@DatatypeBacked("int")
enum class WHV_TRANSLATE_GVA_RESULT_CODE : Mappable<WHV_TRANSLATE_GVA_RESULT_CODE, Int> {
	WHvTranslateGvaResultSuccess,
	WHvTranslateGvaResultPageNotPresent,
	WHvTranslateGvaResultPrivilegeViolation,
	WHvTranslateGvaResultInvalidPageTableFlags,
	WHvTranslateGvaResultGpaUnmapped,
	WHvTranslateGvaResultGpaNoReadAccess,
	WHvTranslateGvaResultGpaNoWriteAccess,
	WHvTranslateGvaResultGpaIllegalOverlayAccess,
	WHvTranslateGvaResultIntercept;

	override val id: Int = ordinal
	override val tag: String = name
}