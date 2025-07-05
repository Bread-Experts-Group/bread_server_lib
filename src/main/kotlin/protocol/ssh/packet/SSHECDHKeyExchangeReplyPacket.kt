package org.bread_experts_group.protocol.ssh.packet

import org.bread_experts_group.stream.write32
import java.io.OutputStream

class SSHECDHKeyExchangeReplyPacket(
	val publicKey: ByteArray,
	val ephemeralPublicKey: ByteArray,
	val signature: ByteArray,
) : SSHIdentifiablePacket(SSHPacketIdentity.SSH_MSG_KEX_ECDH_REPLY) {
	override fun identityComputeSize(): Int = 12 + publicKey.size + ephemeralPublicKey.size + signature.size
	override fun identityWrite(stream: OutputStream) {
		stream.write32(publicKey.size)
		stream.write(publicKey)
		stream.write32(ephemeralPublicKey.size)
		stream.write(ephemeralPublicKey)
		stream.write32(signature.size)
		stream.write(signature)
	}

	override fun toString(): String = super.toString() + "[public key: ${publicKey.toHexString()}, ephemeral " +
			"public key: ${ephemeralPublicKey.toHexString()}, signature: ${signature.toHexString()}]"
}