package org.bread_experts_group.api.secure.blob

import org.bread_experts_group.api.feature.CheckedImplementation
import org.bread_experts_group.api.feature.NoFeatureAvailableException

abstract class SecureDataBlobProvider : CheckedImplementation {
	companion object {
		/**
		 * Opens a new [SecureDataBlob] for use. The [SecureDataBlob] is not initialized for writing / reading.
		 * @author Miko Elbrecht
		 * @since D0F0N0P0
		 */
		fun open(): SecureDataBlob {
			throw NoFeatureAvailableException("Secure In-Memory Data")
		}
	}

	abstract fun new(): SecureDataBlob
}