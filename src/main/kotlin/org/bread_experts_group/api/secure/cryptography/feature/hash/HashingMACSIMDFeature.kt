package org.bread_experts_group.api.secure.cryptography.feature.hash

import org.bread_experts_group.api.secure.cryptography.feature.CryptographySystemFeatureImplementation

abstract class HashingMACSIMDFeature : CryptographySystemFeatureImplementation<HashingMACSIMDFeature>(),
	SIMDHashingCommon {
	/**
	 * Begins a new SIMD hash operation.
	 * @param key The private key to use within MAC computation.
	 * @param n The amount of hashes to compute for.
	 * @author Miko Elbrecht
	 * @since D0F0N0P0
	 */
	abstract fun start(key: ByteArray, n: Int)
}