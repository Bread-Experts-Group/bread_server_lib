package bread_experts_group.dns.ssh

enum class SSHType(val code: Int) {
	SHA_1(1),
	SHA_256(2);

	companion object {
		val mapping = entries.associateBy(SSHType::code)
	}
}