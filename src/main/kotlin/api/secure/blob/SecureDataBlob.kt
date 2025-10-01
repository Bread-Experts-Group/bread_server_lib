package org.bread_experts_group.api.secure.blob

import org.bread_experts_group.api.FeatureProvisioner
import org.bread_experts_group.api.secure.blob.feature.SecureDataBlobFeatureImplementation
import java.lang.AutoCloseable
import java.lang.ref.Cleaner

/**
 * [SecureDataBlob]s allow the secure storage of data in-memory by encrypting / decrypting it during use.
 * Certain operating systems (such as Windows) may allow you to control who can decrypt the data at a time, such as
 * a remote process, or using the local user account. [SecureDataBlob]s must be initialized through a feature in
 * [SecureDataBlobFeatures]. It is not possible to re-initialize a [SecureDataBlob] after it has been cleaned up.
 *
 * [SecureDataBlob]s cannot be written to disk, and their contents will be lost if the computer loses power. It is not
 * advised that you store data for a long time in [SecureDataBlob]s.
 * @see SecureDataBlobFeatures
 * @author Miko Elbrecht
 * @since 4.0.0
 */
abstract class SecureDataBlob : FeatureProvisioner<SecureDataBlobFeatureImplementation<*>>(
	SecureDataBlobFeatureImplementation::class.java
), AutoCloseable {
	private companion object {
		val sdbCleaner: Cleaner = Cleaner.create()
	}

	protected abstract fun cleanup()
	private val cleanupOp = sdbCleaner.register(this) { this.cleanup() }
	final override fun close() {
		cleanupOp.clean()
	}

	abstract var encrypt: () -> Unit
	abstract var decrypt: () -> Unit

	/**
	 * Retrieves a single byte from the [SecureDataBlob]. The memory is unencrypted, read, and finally, re-encrypted.
	 * @author Miko Elbrecht
	 * @since 4.0.0
	 */
	abstract operator fun get(index: Long): Byte

	/**
	 * Retrieves a range of bytes from the [SecureDataBlob]. The memory is unencrypted, read, and finally, re-encrypted.
	 * @author Miko Elbrecht
	 * @since 4.0.0
	 */
	abstract operator fun get(indices: LongRange): SecuredByteArray

	/**
	 * Writes a single byte to the [SecureDataBlob]. The memory is unencrypted, written, and finally, re-encrypted.
	 * @author Miko Elbrecht
	 * @since 4.0.0
	 */
	abstract operator fun set(index: Long, b: Byte)

	/**
	 * Writes a range of bytes to the [SecureDataBlob]. The memory is unencrypted, written, and finally, re-encrypted.
	 * @author Miko Elbrecht
	 * @since 4.0.0
	 */
	abstract operator fun set(index: Long, b: ByteArray)

	/**
	 * Writes a range of bytes to the [SecureDataBlob]. The memory is unencrypted, written, and finally, re-encrypted.
	 * @author Miko Elbrecht
	 * @since 4.0.0
	 */
	abstract operator fun set(index: Long, b: SecuredByteArray)

	/**
	 * Fills a range with a byte in the [SecureDataBlob]. The memory is unencrypted, written, and finally, re-encrypted.
	 * @author Miko Elbrecht
	 * @since 4.0.0
	 */
	abstract operator fun set(indices: LongRange, b: Byte)
}