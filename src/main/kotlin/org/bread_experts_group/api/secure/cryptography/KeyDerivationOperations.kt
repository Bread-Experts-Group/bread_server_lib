package org.bread_experts_group.api.secure.cryptography

enum class KeyDerivationOperations {
	SP800_108_CTR_HMAC,
	SP800_56A_CONCAT,
	PBKDF2,
	CAPI_KDF,
	TLS1_1_KDF,
	TLS1_2_KDF,
	HKDF
}