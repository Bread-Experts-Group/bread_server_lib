package org.bread_experts_group.api.secure.blob.feature

import org.bread_experts_group.api.secure.blob.SecureDataBlob

typealias SDBFIG<I> = SecureDataBlobFeatureImplementation<I>

abstract class EncryptedSecureDataBlobFeature<I : SDBFIG<I>> : SDBFIG<I>() {
	/**
	 * Initializes the [SecureDataBlob] with the specified amount of memory, in bytes. The amount of memory actually
	 * allocated may be higher than what is specified, to accommodate required encryption / decrypted parameters,
	 * however, an [IndexOutOfBoundsException] will still be thrown if an attempt is made to access memory beyond
	 * [size] bytes.
	 * @author Miko Elbrecht
	 * @since D0F0N0P0
	 */
	abstract fun initialize(size: Long)
}