package org.bread_experts_group.model.natives.nt.datatype.projectedfslib

import org.bread_experts_group.model.natives.Order
import org.bread_experts_group.model.natives.Structure

abstract class PRJ_CALLBACKS : Structure<PRJ_CALLBACKS> {
	@Order(0)
	abstract var StartDirectoryEnumerationCallback: PRJ_START_DIRECTORY_ENUMERATION_CB

	@Order(1)
	abstract var EndDirectoryEnumerationCallback: PRJ_END_DIRECTORY_ENUMERATION_CB

	@Order(2)
	abstract var GetDirectoryEnumerationCallback: PRJ_GET_DIRECTORY_ENUMERATION_CB

	@Order(3)
	abstract var GetPlaceholderInfoCallback: PRJ_GET_PLACEHOLDER_INFO_CB

	@Order(4)
	abstract var GetFileDataCallback: PRJ_GET_FILE_DATA_CB

//	@Order(5)
//	abstract var QueryFileNameCallback: PRJ_QUERY_FILE_NAME_CB
//
//	@Order(6)
//	abstract var NotificationCallback: PRJ_NOTIFICATION_CB
//
//	@Order(7)
//	abstract var CancelCommandCallback: PRJ_CANCEL_COMMAND_CB
}