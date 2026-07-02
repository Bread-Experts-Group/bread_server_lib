package org.bread_experts_group.model.natives.nt.datatype

import org.bread_experts_group.model.natives.*

const val MAX_DNS_SUFFIX_STRING_LENGTH = 256L

abstract class _IP_ADAPTER_DNS_SUFFIX : Structure<_IP_ADAPTER_DNS_SUFFIX> {
	@Order(0)
	abstract var Next: Pointer<_IP_ADAPTER_DNS_SUFFIX>

	@Order(1)
	abstract var String: @ArraySize(MAX_DNS_SUFFIX_STRING_LENGTH) NativeArray<WCHAR>
}

typealias IP_ADAPTER_DNS_SUFFIX = _IP_ADAPTER_DNS_SUFFIX
typealias PIP_ADAPTER_DNS_SUFFIX = Pointer<_IP_ADAPTER_DNS_SUFFIX>