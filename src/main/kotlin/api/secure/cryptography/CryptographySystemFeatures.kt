package org.bread_experts_group.api.secure.cryptography

import org.bread_experts_group.api.FeatureExpression
import org.bread_experts_group.api.secure.cryptography.feature.hash.*
import org.bread_experts_group.api.secure.cryptography.feature.random.RandomFeature
import org.bread_experts_group.api.secure.cryptography.feature.symencrypt.AESKey
import org.bread_experts_group.api.secure.cryptography.feature.symencrypt.SymmetricEncryptionFeature

/**
 * The generic set of features for [CryptographySystem].
 * * **HASHING**_*: Hashing / (H)MAC algorithms, with optional SIMD support.
 * @author Miko Elbrecht
 * @since D0F0N0P0
 */
object CryptographySystemFeatures {
	val HASHING_SHA1 = object : FeatureExpression<HashingFeature> {
		override val name: String = "Hashing: SHA-1"
	}

	val HASHING_SHA1_HMAC = object : FeatureExpression<HashingMACFeature> {
		override val name: String = "Hashing: SHA-1 (HMAC)"
	}

	val HASHING_SHA1_SIMD = object : FeatureExpression<SIMDHashingFeature> {
		override val name: String = "Hashing: SHA-1 (SIMD)"
	}

	val HASHING_SHA1_HMAC_SIMD = object : FeatureExpression<HashingMACSIMDFeature> {
		override val name: String = "Hashing: SHA-1 (HMAC / SIMD)"
	}

	val HASHING_SHA256 = object : FeatureExpression<HashingFeature> {
		override val name: String = "Hashing: SHA-256"
	}

	val HASHING_SHA256_HMAC = object : FeatureExpression<HashingMACFeature> {
		override val name: String = "Hashing: SHA-256 (HMAC)"
	}

	val HASHING_SHA256_SIMD = object : FeatureExpression<SIMDHashingFeature> {
		override val name: String = "Hashing: SHA-256 (SIMD)"
	}

	val HASHING_SHA256_HMAC_SIMD = object : FeatureExpression<HashingMACSIMDFeature> {
		override val name: String = "Hashing: SHA-256 (HMAC / SIMD)"
	}

	val HASHING_SHA384 = object : FeatureExpression<HashingFeature> {
		override val name: String = "Hashing: SHA-384"
	}

	val HASHING_SHA384_HMAC = object : FeatureExpression<HashingMACFeature> {
		override val name: String = "Hashing: SHA-384 (HMAC)"
	}

	val HASHING_SHA384_SIMD = object : FeatureExpression<SIMDHashingFeature> {
		override val name: String = "Hashing: SHA-384 (SIMD)"
	}

	val HASHING_SHA384_HMAC_SIMD = object : FeatureExpression<HashingMACSIMDFeature> {
		override val name: String = "Hashing: SHA-384 (HMAC / SIMD)"
	}

	val HASHING_SHA512 = object : FeatureExpression<HashingFeature> {
		override val name: String = "Hashing: SHA-512"
	}

	val HASHING_SHA512_HMAC = object : FeatureExpression<HashingMACFeature> {
		override val name: String = "Hashing: SHA-512 (HMAC)"
	}

	val HASHING_SHA512_SIMD = object : FeatureExpression<SIMDHashingFeature> {
		override val name: String = "Hashing: SHA-512 (SIMD)"
	}

	val HASHING_SHA512_HMAC_SIMD = object : FeatureExpression<HashingMACSIMDFeature> {
		override val name: String = "Hashing: SHA-512 (HMAC / SIMD)"
	}

	val HASHING_MD2 = object : FeatureExpression<HashingFeature> {
		override val name: String = "Hashing: MD2"
	}

	val HASHING_MD2_HMAC = object : FeatureExpression<HashingMACFeature> {
		override val name: String = "Hashing: MD2 (HMAC)"
	}

	val HASHING_MD2_SIMD = object : FeatureExpression<SIMDHashingFeature> {
		override val name: String = "Hashing: MD2 (SIMD)"
	}

	val HASHING_MD2_HMAC_SIMD = object : FeatureExpression<HashingMACSIMDFeature> {
		override val name: String = "Hashing: MD2 (HMAC / SIMD)"
	}

	val HASHING_MD4 = object : FeatureExpression<HashingFeature> {
		override val name: String = "Hashing: MD4"
	}

	val HASHING_MD4_HMAC = object : FeatureExpression<HashingMACFeature> {
		override val name: String = "Hashing: MD4 (HMAC)"
	}

	val HASHING_MD4_SIMD = object : FeatureExpression<SIMDHashingFeature> {
		override val name: String = "Hashing: MD4 (SIMD)"
	}

	val HASHING_MD4_HMAC_SIMD = object : FeatureExpression<HashingMACSIMDFeature> {
		override val name: String = "Hashing: MD4 (HMAC / SIMD)"
	}

	val HASHING_MD5 = object : FeatureExpression<HashingFeature> {
		override val name: String = "Hashing: MD5"
	}

	val HASHING_MD5_HMAC = object : FeatureExpression<HashingMACFeature> {
		override val name: String = "Hashing: MD5 (HMAC)"
	}

	val HASHING_MD5_SIMD = object : FeatureExpression<SIMDHashingFeature> {
		override val name: String = "Hashing: MD5 (SIMD)"
	}

	val HASHING_MD5_HMAC_SIMD = object : FeatureExpression<HashingMACSIMDFeature> {
		override val name: String = "Hashing: MD5 (HMAC / SIMD)"
	}

	val HASHING_SHA3_256 = object : FeatureExpression<HashingFeature> {
		override val name: String = "Hashing: SHA3-256"
	}

	val HASHING_SHA3_256_HMAC = object : FeatureExpression<HashingMACFeature> {
		override val name: String = "Hashing: SHA3-256 (HMAC)"
	}

	val HASHING_SHA3_256_SIMD = object : FeatureExpression<SIMDHashingFeature> {
		override val name: String = "Hashing: SHA3-256 (SIMD)"
	}

	val HASHING_SHA3_256_HMAC_SIMD = object : FeatureExpression<HashingMACSIMDFeature> {
		override val name: String = "Hashing: SHA3-256 (HMAC / SIMD)"
	}

	val HASHING_SHA3_384 = object : FeatureExpression<HashingFeature> {
		override val name: String = "Hashing: SHA3-384"
	}

	val HASHING_SHA3_384_HMAC = object : FeatureExpression<HashingMACFeature> {
		override val name: String = "Hashing: SHA3-384 (HMAC)"
	}

	val HASHING_SHA3_384_SIMD = object : FeatureExpression<SIMDHashingFeature> {
		override val name: String = "Hashing: SHA3-384 (SIMD)"
	}

	val HASHING_SHA3_384_HMAC_SIMD = object : FeatureExpression<HashingMACSIMDFeature> {
		override val name: String = "Hashing: SHA3-384 (HMAC / SIMD)"
	}

	val HASHING_SHA3_512 = object : FeatureExpression<HashingFeature> {
		override val name: String = "Hashing: SHA3-512"
	}

	val HASHING_SHA3_512_HMAC = object : FeatureExpression<HashingMACFeature> {
		override val name: String = "Hashing: SHA3-512 (HMAC)"
	}

	val HASHING_SHA3_512_SIMD = object : FeatureExpression<SIMDHashingFeature> {
		override val name: String = "Hashing: SHA3-512 (SIMD)"
	}

	val HASHING_SHA3_512_HMAC_SIMD = object : FeatureExpression<HashingMACSIMDFeature> {
		override val name: String = "Hashing: SHA3-512 (HMAC / SIMD)"
	}

	val HASHING_SHAKE128 = object : FeatureExpression<XOFHashingFeature> {
		override val name: String = "Hashing: SHAKE-128 (XOF)"
	}

	val HASHING_SHAKE256 = object : FeatureExpression<XOFHashingFeature> {
		override val name: String = "Hashing: SHAKE-256 (XOF)"
	}

	val HASHING_CSHAKE128 = object : FeatureExpression<CSHAKEXOFHashingFeature> {
		override val name: String = "Hashing: CSHAKE-128 (XOF, customizable)"
	}

	val HASHING_KMAC128 = object : FeatureExpression<KMACXOFHashingFeature> {
		override val name: String = "Hashing: KMAC-128"
	}

	val HASHING_CSHAKE256 = object : FeatureExpression<CSHAKEXOFHashingFeature> {
		override val name: String = "Hashing: CSHAKE-256 (XOF, customizable)"
	}

	val HASHING_KMAC256 = object : FeatureExpression<KMACXOFHashingFeature> {
		override val name: String = "Hashing: KMAC-256"
	}

	val HASHING_AES_CMAC = object : FeatureExpression<HashingMACFeature> {
		override val name: String = "Hashing: AES CMAC"
	}

	val HASHING_AES_CMAC_SIMD = object : FeatureExpression<HashingMACSIMDFeature> {
		override val name: String = "Hashing: AES CMAC (SIMD)"
	}

	val RANDOM_SYSTEM_PREFERRED = object : FeatureExpression<RandomFeature> {
		override val name: String = "Random Number Generation: System-Preferred"
	}

	val RANDOM = object : FeatureExpression<RandomFeature> {
		override val name: String = "Random Number Generation: Secure"
	}

	val RANDOM_FIPS_186_2_DSA = object : FeatureExpression<RandomFeature> {
		override val name: String = "Random Number Generation: FIPS 186-2 (DSA suitable)"
	}

	val SYMMETRIC_ENCRYPTION_AES_CBC = object : FeatureExpression<SymmetricEncryptionFeature<AESKey>> {
		override val name: String = "Symmetric Encryption: AES (CBC mode)"
	}

	val SYMMETRIC_ENCRYPTION_AES_ECB = object : FeatureExpression<SymmetricEncryptionFeature<AESKey>> {
		override val name: String = "Symmetric Encryption: AES (ECB mode)"
	}

	val SYMMETRIC_ENCRYPTION_AES_CCM = object : FeatureExpression<SymmetricEncryptionFeature<AESKey>> {
		override val name: String = "Symmetric Encryption: AES (CCM mode)"
	}

	val SYMMETRIC_ENCRYPTION_AES_GCM = object : FeatureExpression<SymmetricEncryptionFeature<AESKey>> {
		override val name: String = "Symmetric Encryption: AES (GCM mode)"
	}
}