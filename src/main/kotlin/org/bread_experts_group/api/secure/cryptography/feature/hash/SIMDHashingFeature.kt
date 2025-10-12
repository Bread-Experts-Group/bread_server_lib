package org.bread_experts_group.api.secure.cryptography.feature.hash

import org.bread_experts_group.api.secure.cryptography.feature.CryptographySystemFeatureImplementation

abstract class SIMDHashingFeature : CryptographySystemFeatureImplementation<SIMDHashingFeature>(), SIMDHashingCommon {
	/**
	 * Begins a new SIMD hash operation.
	 * @param n The amount of hashes to compute for.
	 * @author Miko Elbrecht
	 * @since D0F0N0P0
	 */
	abstract fun start(n: Int)
}