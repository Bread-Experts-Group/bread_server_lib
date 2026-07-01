package org.bread_experts_group.model.natives.nt.datatype.projectedfslib

import org.bread_experts_group.model.natives.nt.datatype.HRESULT
import org.bread_experts_group.model.natives.nt.datatype.UINT32
import org.bread_experts_group.model.natives.nt.datatype.UINT64

typealias PrjGetFileDataCb = (callbackData: PRJ_CALLBACK_DATA, byteOffset: UINT64, length: UINT32) -> HRESULT