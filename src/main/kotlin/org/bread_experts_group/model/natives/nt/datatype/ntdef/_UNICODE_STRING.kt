package org.bread_experts_group.model.natives.nt.datatype.ntdef

import org.bread_experts_group.model.natives.Order
import org.bread_experts_group.model.natives.Pointer
import org.bread_experts_group.model.natives.Structure
import org.bread_experts_group.model.natives.nt.datatype.PWSTR
import org.bread_experts_group.model.natives.nt.datatype.USHORT

@Suppress("ClassName", "PropertyName")
abstract class _UNICODE_STRING : Structure<_UNICODE_STRING> {
	@Order(0)
	abstract var Length: USHORT

	@Order(1)
	abstract var MaximumLength: USHORT

	@Order(2)
	abstract var Buffer: PWSTR
}

typealias UNICODE_STRING = _UNICODE_STRING
typealias PUNICODE_STRING = Pointer<_UNICODE_STRING>