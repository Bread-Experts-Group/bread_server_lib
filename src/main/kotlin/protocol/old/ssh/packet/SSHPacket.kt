package org.bread_experts_group.protocol.old.ssh.packet

import org.bread_experts_group.stream.LongInputStream
import java.io.OutputStream

class SSHPacket(val data: LongInputStream) : SSHBasePacket() {
	override fun toString(): String = "SSHPacket[#${data.longAvailable()}]"
	override fun subComputeSize(): Int = data.available()
	override fun subwrite(stream: OutputStream) {
		data.transferTo(stream)
	}
}