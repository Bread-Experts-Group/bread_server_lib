package org.bread_experts_group.model.natives.nt.datatype

import org.bread_experts_group.generic.Mappable
import org.bread_experts_group.model.natives.DatatypeBacked

@DatatypeBacked("int")
enum class WHV_REGISTER_NAME(override val id: Int) : Mappable<WHV_REGISTER_NAME, Int> {
	WHvX64RegisterRax(0x00000000),
	WHvX64RegisterRip(0x00000010),
	WHvX64RegisterRflags(0x00000011),

	WHvX64RegisterCs(0x00000013);

	override val tag: String = name
}