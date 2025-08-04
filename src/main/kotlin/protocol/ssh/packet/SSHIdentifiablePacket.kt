package org.bread_experts_group.protocol.ssh.packet

import org.bread_experts_group.coder.Mappable.Companion.id
import java.io.OutputStream

sealed class SSHIdentifiablePacket(val identity: SSHPacketIdentity) : SSHBasePacket() {
	override fun toString(): String = "SSHIdentifiablePacket[$identity]"
	final override fun subComputeSize(): Int = 1 + identityComputeSize()
	final override fun subwrite(stream: OutputStream) {
		stream.write(identity.id)
		identityWrite(stream)
	}

	abstract fun identityComputeSize(): Int
	abstract fun identityWrite(stream: OutputStream)

	companion object {
		fun decode(from: SSHPacket): SSHIdentifiablePacket {
			val message = SSHPacketIdentity.entries.id(from.data.read()).enum
			return when (message) {
				SSHPacketIdentity.SSH_MSG_KEXINIT -> SSHAlgorithmNegotiationPacket.decode(from.data)
				SSHPacketIdentity.SSH_MSG_KEX_ECDH_INIT -> SSHECDHKeyExchangeInitializationPacket.decode(from.data)
				SSHPacketIdentity.SSH_MSG_KEX_ECDH_REPLY -> throw IllegalStateException()
			}.also { from.data.readAllBytes() }
		}
	}
}