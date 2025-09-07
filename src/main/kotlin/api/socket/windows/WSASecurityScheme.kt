package org.bread_experts_group.api.socket.windows

import org.bread_experts_group.coder.Mappable

enum class WSASecurityScheme(override val id: Int, override val tag: String) : Mappable<WSASecurityScheme, Int> {
	SECURITY_PROTOCOL_NONE(0, "No security scheme");

	override fun toString(): String = stringForm()
}