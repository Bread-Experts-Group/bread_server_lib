package org.bread_experts_group.socket.windows

import org.bread_experts_group.coder.Mappable

enum class WSASocketType(override val id: Int, override val tag: String) : Mappable<WSASocketType, Int> {
	SOCK_STREAM(1, "Stream-oriented"),
	SOCK_DGRAM(2, "Datagram-oriented"),
	SOCK_RAW(3, "Raw"),
	SOCK_RDM(4, "Reliable message datagram-oriented"),
	SOCK_SEQPACKET(5, "Pseudo-stream datagram-oriented");

	override fun toString(): String = stringForm()
}