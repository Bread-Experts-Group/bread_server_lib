package org.bread_experts_group.protocol.old.dns.ssh

enum class SSHType(val code: Int) {
	SHA_1(1),
	SHA_256(2);

	companion object {
		val mapping: Map<Int, SSHType> = entries.associateBy(SSHType::code)
	}
}