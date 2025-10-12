package org.bread_experts_group.ffi.windows.bcrypt

import org.bread_experts_group.Mappable

enum class WindowsBCryptInterface(
	override val id: UInt,
	override val tag: String
) : Mappable<WindowsBCryptInterface, UInt> {
	BCRYPT_CIPHER_INTERFACE(0x00000001u, "Cipher"),
	BCRYPT_HASH_INTERFACE(0x00000002u, "Hash"),
	BCRYPT_ASYMMETRIC_ENCRYPTION_INTERFACE(0x00000003u, "Asymmetric Encryption"),
	BCRYPT_SECRET_AGREEMENT_INTERFACE(0x00000004u, "Secret Agreement"),
	BCRYPT_SIGNATURE_INTERFACE(0x00000005u, "Signature"),
	BCRYPT_RNG_INTERFACE(0x00000006u, "Random Number Generation"),
	BCRYPT_KEY_DERIVATION_INTERFACE(0x00000007u, "Key Derivation"),
	NCRYPT_KEY_STORAGE_INTERFACE(0x00010001u, "NCrypt Key Storage"),
	NCRYPT_SCHANNEL_INTERFACE(0x00010002u, "NCrypt SChannel"),
	NCRYPT_SCHANNEL_SIGNATURE_INTERFACE(0x00010003u, "NCrypt SChannel Signature"),
	NCRYPT_KEY_PROTECTION_INTERFACE(0x00010004u, "NCrypt Key Protection");

	override fun toString(): String = stringForm()
}