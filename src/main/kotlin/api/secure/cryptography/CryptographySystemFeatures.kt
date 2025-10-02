package org.bread_experts_group.api.secure.cryptography

import org.bread_experts_group.api.FeatureExpression
import org.bread_experts_group.api.secure.cryptography.feature.HashingFeature

object CryptographySystemFeatures {
	val HASHING_SHA1 = object : FeatureExpression<HashingFeature> {
		override val name: String = "Hashing: SHA-1"
	}

	val HASHING_SHA256 = object : FeatureExpression<HashingFeature> {
		override val name: String = "Hashing: SHA-256"
	}

	val HASHING_SHA384 = object : FeatureExpression<HashingFeature> {
		override val name: String = "Hashing: SHA-384"
	}

	val HASHING_SHA512 = object : FeatureExpression<HashingFeature> {
		override val name: String = "Hashing: SHA-512"
	}

	val HASHING_MD5 = object : FeatureExpression<HashingFeature> {
		override val name: String = "Hashing: MD5"
	}

	val HASHING_MD4 = object : FeatureExpression<HashingFeature> {
		override val name: String = "Hashing: MD4"
	}

	val HASHING_MD2 = object : FeatureExpression<HashingFeature> {
		override val name: String = "Hashing: MD2"
	}

	val HASHING_AES_CMAC = object : FeatureExpression<HashingFeature> {
		override val name: String = "Hashing: AES CMAC"
	}

	val HASHING_SHA3_256 = object : FeatureExpression<HashingFeature> {
		override val name: String = "Hashing: SHA3-256"
	}

	val HASHING_SHA3_384 = object : FeatureExpression<HashingFeature> {
		override val name: String = "Hashing: SHA3-384"
	}

	val HASHING_SHA3_512 = object : FeatureExpression<HashingFeature> {
		override val name: String = "Hashing: SHA3-512"
	}

	val HASHING_CSHAKE128 = object : FeatureExpression<HashingFeature> {
		override val name: String = "Hashing: CSHAKE-128"
	}

	val HASHING_CSHAKE256 = object : FeatureExpression<HashingFeature> {
		override val name: String = "Hashing: CSHAKE-256"
	}

	val HASHING_KMAC128 = object : FeatureExpression<HashingFeature> {
		override val name: String = "Hashing: KMAC-128"
	}

	val HASHING_KMAC256 = object : FeatureExpression<HashingFeature> {
		override val name: String = "Hashing: KMAC-256"
	}
}