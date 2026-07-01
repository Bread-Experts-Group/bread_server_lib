package org.bread_experts_group.api.secure.cryptography

import org.bread_experts_group.api.feature.CheckedImplementation
import org.bread_experts_group.api.feature.NoFeatureAvailableException

abstract class CryptographySystemProvider : CheckedImplementation {
	companion object {
		/**
		 * Opens a new [CryptographySystem] for use.
		 * @author Miko Elbrecht
		 * @since D0F0N0P0
		 */
		fun open(): CryptographySystem {
			throw NoFeatureAvailableException("Cryptographic Operations")
		}
	}

	abstract fun new(): CryptographySystem
}