package org.bread_experts_group.model.natives.nt.datatype

import org.bread_experts_group.model.natives.Order
import org.bread_experts_group.model.natives.Pointer
import org.bread_experts_group.model.natives.Structure

abstract class _SOCKET_ADDRESS : Structure<_SOCKET_ADDRESS> {
	@Order(0)
	abstract var lpSockaddr: LPSOCKADDR

	@Order(1)
	abstract var iSockaddrLength: INT
}

typealias SOCKET_ADDRESS = _SOCKET_ADDRESS
typealias PSOCKET_ADDRESS = Pointer<_SOCKET_ADDRESS>
typealias LPSOCKET_ADDRESS = Pointer<_SOCKET_ADDRESS>