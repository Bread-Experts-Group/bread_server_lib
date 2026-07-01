package org.bread_experts_group.model.natives.nt.datatype.projectedfslib

import org.bread_experts_group.model.natives.nt.datatype.GUID
import org.bread_experts_group.model.natives.nt.datatype.HRESULT
import org.bread_experts_group.model.natives.nt.datatype.PCWSTR

typealias PrjGetDirectoryEnumerationCb = (
	callbackData: PRJ_CALLBACK_DATA,
	enumerationId: GUID,
	searchExpression: PCWSTR?,
	dirEntryBufferHandle: PRJ_DIR_ENTRY_BUFFER_HANDLE
) -> HRESULT