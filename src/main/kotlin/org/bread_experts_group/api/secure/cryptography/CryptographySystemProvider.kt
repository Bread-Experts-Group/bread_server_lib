package org.bread_experts_group.api.secure.cryptography

import org.bread_experts_group.api.feature.CheckedImplementation
import org.bread_experts_group.api.feature.NoFeatureAvailableException
import java.util.*

abstract class CryptographySystemProvider : CheckedImplementation {
	companion object {
		/**
		 * Opens a new [CryptographySystem] for use.
		 * @author Miko Elbrecht
		 * @since D0F0N0P0
		 */
		fun open(): CryptographySystem {
			val system = ServiceLoader.load(CryptographySystemProvider::class.java)
				.filter {
					try {
						it.supported()
					} catch (_: NoFeatureAvailableException) {
						false
					}
				}
				.minByOrNull { it.source } ?: throw NoFeatureAvailableException("Cryptographic Operations")
			return system.new()
		}
	}

	abstract fun new(): CryptographySystem
}