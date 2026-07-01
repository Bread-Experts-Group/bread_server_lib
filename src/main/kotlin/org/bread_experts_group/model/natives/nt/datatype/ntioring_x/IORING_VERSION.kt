@file:Suppress("ClassName")

package org.bread_experts_group.model.natives.nt.datatype.ntioring_x

import org.bread_experts_group.generic.Mappable
import org.bread_experts_group.model.natives.DatatypeBacked

@DatatypeBacked("int")
enum class IORING_VERSION(override val id: Int) : Mappable<IORING_VERSION, Int> {
	IORING_VERSION_INVALID(0),
	IORING_VERSION_1(1),
	IORING_VERSION_2(2),
	IORING_VERSION_3(300),
	IORING_VERSION_4(400);

	override val tag: String = name
}