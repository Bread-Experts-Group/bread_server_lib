package org.bread_experts_group.protocol.ssh.packet

import org.bread_experts_group.coder.Mappable

enum class SSHPacketIdentity(override val id: Int, override val tag: String) : Mappable<SSHPacketIdentity, Int> {
	SSH_MSG_KEXINIT(20, "Key Exchange - Algorithm Negotiation"),
	SSH_MSG_KEX_ECDH_INIT(30, "Key Exchange - ECDH Initialization"),
	SSH_MSG_KEX_ECDH_REPLY(31, "Key Exchange - ECDH Reply");

	override fun toString(): String = stringForm()
}