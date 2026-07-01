package org.bread_experts_group.model.natives.nt.datatype.projectedfslib

import org.bread_experts_group.model.natives.nt.datatype.GUID
import org.bread_experts_group.model.natives.nt.datatype.HRESULT

typealias PrjStartDirectoryEnumerationCb = (callbackData: PRJ_CALLBACK_DATA, enumerationId: GUID) -> HRESULT