package org.bread_experts_group.model.natives.nt.datatype.projectedfslib

import org.bread_experts_group.model.natives.Order
import org.bread_experts_group.model.natives.Pointer
import org.bread_experts_group.model.natives.Structure
import org.bread_experts_group.model.natives.nt.datatype.GUID
import org.bread_experts_group.model.natives.nt.datatype.INT32
import org.bread_experts_group.model.natives.nt.datatype.PCWSTR
import org.bread_experts_group.model.natives.nt.datatype.UINT32
import java.lang.foreign.MemorySegment

abstract class PRJ_CALLBACK_DATA : Structure<PRJ_CALLBACK_DATA> {
	@Order(0)
	abstract var Size: UINT32

	@Order(1)
	abstract var Flags: PRJ_CALLBACK_DATA_FLAGS

	@Order(2)
	abstract var NamespaceVirtualizationContext: PRJ_NAMESPACE_VIRTUALIZATION_CONTEXT

	@Order(3)
	abstract var CommandId: INT32

	@Order(4)
	abstract var FileId: GUID

	@Order(5)
	abstract var DataStreamId: GUID

	@Order(6)
	abstract var FilePathName: PCWSTR

	@Order(7)
	abstract var VersionInfo: Pointer<PRJ_PLACEHOLDER_VERSION_INFO>

	@Order(8)
	abstract var TriggeringProcessId: UINT32

	@Order(9)
	abstract var TriggeringProcessImageFileName: PCWSTR

	@Order(10)
	abstract var InstanceContext: MemorySegment

	override fun toString(): String = ""
}