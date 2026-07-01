package org.bread_experts_group.model.natives.nt.datatype.projectedfslib

import org.bread_experts_group.model.natives.Pointer
import org.bread_experts_group.model.natives.nt.datatype.BOOLEAN
import org.bread_experts_group.model.natives.nt.datatype.HRESULT
import org.bread_experts_group.model.natives.nt.datatype.PCWSTR

typealias PrjNotificationCb = (
	callbackData: PRJ_CALLBACK_DATA,
	isDirectory: BOOLEAN,
	notification: PRJ_NOTIFICATION,
	destinationFileName: PCWSTR?,
	operationParameters: Pointer<PRJ_NOTIFICATION_PARAMETERS>
) -> HRESULT