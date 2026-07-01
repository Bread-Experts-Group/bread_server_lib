package org.bread_experts_group.model.natives.nt.datatype.projectedfslib

import org.bread_experts_group.model.natives.Order
import org.bread_experts_group.model.natives.Pointer
import org.bread_experts_group.model.natives.Structure
import org.bread_experts_group.model.natives.nt.datatype.UINT32

abstract class PRJ_STARTVIRTUALIZING_OPTIONS : Structure<PRJ_STARTVIRTUALIZING_OPTIONS> {
	@Order(0)
	abstract var Flags: PRJ_STARTVIRTUALIZING_FLAGS

	@Order(1)
	abstract var PoolThreadCount: UINT32

	@Order(2)
	abstract var ConcurrentThreadCount: UINT32

	@Order(3)
	abstract var NotificationMappings: Pointer<PRJ_NOTIFICATION_MAPPING>

	@Order(4)
	abstract var NotificationMappingsCount: UINT32
}