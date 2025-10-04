package org.bread_experts_group.api.secure.cryptography.feature.hash

import org.bread_experts_group.api.secure.cryptography.feature.CryptographySystemFeatureImplementation

abstract class KMACXOFHashingFeature :
	CryptographySystemFeatureImplementation<KMACXOFHashingFeature>(),
	XOFHashingCommon, MACCommon {
	/**
	 * The customization string to use during MAC computation, also known as variable "S" in KMAC128/256.
	 * [NIST SP 800-185](https://nvlpubs.nist.gov/nistpubs/SpecialPublications/NIST.SP.800-185.pdf)
	 * @author Miko Elbrecht
	 * @since D0F0N0P0
	 */
	abstract fun setCustomizationString(s: ByteArray)
}