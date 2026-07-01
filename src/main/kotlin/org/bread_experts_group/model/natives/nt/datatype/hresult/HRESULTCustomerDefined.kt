package org.bread_experts_group.model.natives.nt.datatype.hresult

import org.bread_experts_group.model.natives.SystemStatus

class HRESULTCustomerDefined(val code: Int) : SystemStatus.Undefined(
	"Customer defined status 32-bit code. \"${code.toUInt().toHexString()}\""
), HRESULT