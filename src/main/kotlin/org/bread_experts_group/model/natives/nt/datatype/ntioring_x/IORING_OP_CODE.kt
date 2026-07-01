package org.bread_experts_group.model.natives.nt.datatype.ntioring_x

import org.bread_experts_group.generic.Mappable
import org.bread_experts_group.model.natives.DatatypeBacked

@DatatypeBacked("int")
enum class IORING_OP_CODE : Mappable<IORING_OP_CODE, Int> {
	IORING_OP_NOP,
	IORING_OP_READ,
	IORING_OP_REGISTER_FILES,
	IORING_OP_REGISTER_BUFFERS,
	IORING_OP_CANCEL,
	IORING_OP_WRITE,
	IORING_OP_FLUSH,
	IORING_OP_READ_SCATTER,
	IORING_OP_WRITE_GATHER;

	override val id: Int = ordinal
	override val tag: String = name
}