package org.bread_experts_group.api.secure.cryptography.feature.hash

import org.bread_experts_group.api.secure.blob.SecuredByteArray

interface HashingCommon {
	/**
	 * Adds the specified byte to the hash value.
	 * @author Miko Elbrecht
	 * @since D0F0N0P0
	 */
	operator fun plusAssign(b: Byte)

	/**
	 * Adds the specified bytes to the hash value.
	 * @author Miko Elbrecht
	 * @since D0F0N0P0
	 */
	operator fun plusAssign(b: ByteArray)

	/**
	 * Adds the specified bytes to the hash value.
	 * @author Miko Elbrecht
	 * @since D0F0N0P0
	 */
	operator fun plusAssign(b: SecuredByteArray) = plusAssign(b.around)

	/**
	 * Exports the computed hash value, returning it as a byte array. The internally computed value is unchanged.
	 * @author Miko Elbrecht
	 * @since D0F0N0P0
	 * @see flush
	 */
	fun export(): ByteArray

	/**
	 * Exports the computed hash value, returning it as a secured byte array.
	 * The internally computed value is unchanged.
	 * @author Miko Elbrecht
	 * @since D0F0N0P0
	 * @see flushSecure
	 */
	fun exportSecure(): SecuredByteArray = SecuredByteArray(export())

	/**
	 * Flushes the computed hash value, returning it as a byte array. The internally computed value is reset.
	 * @author Miko Elbrecht
	 * @since D0F0N0P0
	 * @see export
	 */
	fun flush(): ByteArray

	/**
	 * Flushes the computed hash value, returning it as a secured byte array. The internally computed value is reset.
	 * @author Miko Elbrecht
	 * @since D0F0N0P0
	 * @see exportSecure
	 */
	fun flushSecure(): SecuredByteArray = SecuredByteArray(flush())

	/**
	 * Resets the internally computed value without returning data.
	 * @author Miko Elbrecht
	 * @since D0F0N0P0
	 */
	fun reset()
}