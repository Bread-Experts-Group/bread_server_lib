package org.bread_experts_group.api.secure.cryptography.feature.hash

import org.bread_experts_group.api.secure.cryptography.feature.CryptographySystemFeatureImplementation

abstract class CSHAKEXOFHashingFeature :
	CryptographySystemFeatureImplementation<CSHAKEXOFHashingFeature>(),
	XOFHashingCommon {
	/**
	 * Sets the function name to use during hash computation, also known as variable "N" in cSHAKE128/256.
	 * [NIST SP 800-185](https://nvlpubs.nist.gov/nistpubs/SpecialPublications/NIST.SP.800-185.pdf)
	 * @author Miko Elbrecht
	 * @since D0F0N0P0
	 */
	abstract fun setFunctionName(n: ByteArray)

	/**
	 * The customization string to use during hash computation, also known as variable "S" in cSHAKE128/256.
	 * [NIST SP 800-185](https://nvlpubs.nist.gov/nistpubs/SpecialPublications/NIST.SP.800-185.pdf)
	 * @author Miko Elbrecht
	 * @since D0F0N0P0
	 */
	abstract fun setCustomizationString(s: ByteArray)
}