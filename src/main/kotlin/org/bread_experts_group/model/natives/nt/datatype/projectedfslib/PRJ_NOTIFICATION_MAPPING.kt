package org.bread_experts_group.model.natives.nt.datatype.projectedfslib

import org.bread_experts_group.model.natives.Order
import org.bread_experts_group.model.natives.Structure
import org.bread_experts_group.model.natives.nt.datatype.PCWSTR

abstract class PRJ_NOTIFICATION_MAPPING : Structure<PRJ_NOTIFICATION_MAPPING> {
	@Order(0)
	abstract var NotificationBitMask: PRJ_NOTIFY_TYPES

	@Order(1)
	abstract var NotificationRoot: PCWSTR
}