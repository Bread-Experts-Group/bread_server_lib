package org.bread_experts_group.api.secure.cryptography

import org.bread_experts_group.api.CheckedImplementation
import org.bread_experts_group.api.NoFeatureAvailableException
import java.util.*

abstract class CryptographySystemProvider : CheckedImplementation {
	companion object {
		/**
		 * Opens a new [CryptographySystem] for use.
		 * @author Miko Elbrecht
		 * @since 4.0.0
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