package org.bread_experts_group.model.natives.nt.datatype

import org.bread_experts_group.model.natives.Order
import org.bread_experts_group.model.natives.Pointer
import org.bread_experts_group.model.natives.Structure

abstract class _NET_LUID_LH : Structure<NET_LUID_LH> {
	@Order(0)
	abstract var Value: ULONG64
}

typealias NET_LUID_LH = _NET_LUID_LH
typealias PNET_LUID_LH = Pointer<_NET_LUID_LH>