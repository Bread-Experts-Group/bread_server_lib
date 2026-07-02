package org.bread_experts_group.model.natives.nt.datatype

import org.bread_experts_group.model.natives.*

abstract class sockaddr : Structure<sockaddr> {
	@Order(0)
	abstract var sa_family: ADDRESS_FAMILY

	@Order(1)
	abstract var sa_data: @ArraySize(14) NativeArray<CHAR>
}

typealias SOCKADDR = sockaddr
typealias PSOCKADDR = Pointer<sockaddr>
typealias LPSOCKADDR = Pointer<sockaddr>