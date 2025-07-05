package org.bread_experts_group.protocol.ssh.packet

import org.bread_experts_group.stream.read32
import java.io.InputStream
import java.io.OutputStream

class SSHECDHKeyExchangeInitializationPacket(
	val publicKey: ByteArray
) : SSHIdentifiablePacket(SSHPacketIdentity.SSH_MSG_KEX_ECDH_INIT) {
	override fun identityComputeSize(): Int {
		TODO("Not yet implemented")
	}

	override fun identityWrite(stream: OutputStream) {
		TODO("Not yet implemented")
	}

	override fun toString(): String = super.toString() + "[public key: ${publicKey.toHexString()}]"

	companion object {
		fun decode(stream: InputStream) = SSHECDHKeyExchangeInitializationPacket(stream.readNBytes(stream.read32()))
	}
}