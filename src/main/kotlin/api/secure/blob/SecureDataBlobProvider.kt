package org.bread_experts_group.api.secure.blob

import org.bread_experts_group.api.CheckedImplementation
import org.bread_experts_group.api.NoFeatureAvailableException
import java.util.*

abstract class SecureDataBlobProvider : CheckedImplementation {
	companion object {
		/**
		 * Opens a new [SecureDataBlob] for use. The [SecureDataBlob] is not initialized for writing / reading.
		 * @author Miko Elbrecht
		 * @since D0F0N0P0
		 */
		fun open(): SecureDataBlob {
			val blob = ServiceLoader.load(SecureDataBlobProvider::class.java)
				.filter {
					try {
						it.supported()
					} catch (_: NoFeatureAvailableException) {
						false
					}
				}
				.minByOrNull { it.source } ?: throw NoFeatureAvailableException("Secure In-Memory Data")
			return blob.new()
		}
	}

	abstract fun new(): SecureDataBlob
}