package org.bread_experts_group.protocol.old.ssh.packet

import java.io.OutputStream

class SSHBannerPacket(val banner: String) : SSHBasePacket() {
	override fun subwrite(stream: OutputStream) = throw UnsupportedOperationException()
	override fun subComputeSize(): Int = throw UnsupportedOperationException()
	override fun toString(): String = "SSHBannerPacket[\"$banner\"]"
}