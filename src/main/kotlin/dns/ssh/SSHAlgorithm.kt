package org.bread_experts_group.dns.ssh

enum class SSHAlgorithm(val code: Int) {
	RSA(1),
	DSA(2),
	ECDSA(3),
	ED25519(4),
	ED448(6);

	companion object {
		val mapping = entries.associateBy(SSHAlgorithm::code)
	}
}