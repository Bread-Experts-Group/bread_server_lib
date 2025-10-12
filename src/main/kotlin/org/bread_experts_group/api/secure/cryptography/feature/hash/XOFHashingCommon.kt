package org.bread_experts_group.api.secure.cryptography.feature.hash

import org.bread_experts_group.api.secure.blob.SecuredByteArray

interface XOFHashingCommon : HashingCommon {
	/**
	 * Exports the computed hash value, returning it as a byte array. The internally computed value is unchanged.
	 * This method will use a default length that maintains full security of the XOF.
	 * @author Miko Elbrecht
	 * @since D0F0N0P0
	 * @see exportSecure
	 * @see exportX
	 * @see flush
	 */
	abstract override fun export(): ByteArray

	/**
	 * Exports the computed hash value with the specified length, returning it as a byte array. The internally computed
	 * value is unchanged.
	 * @author Miko Elbrecht
	 * @since D0F0N0P0
	 * @see export
	 * @see flushX
	 */
	fun exportX(length: Int): ByteArray

	/**
	 * Exports the computed hash value, returning it as a secured byte array.
	 * The internally computed value is unchanged.
	 * This method will use a default length that maintains full security of the XOF.
	 * @author Miko Elbrecht
	 * @since D0F0N0P0
	 * @see exportSecureX
	 */
	override fun exportSecure(): SecuredByteArray = SecuredByteArray(export())

	/**
	 * Exports the computed hash value with the specified length, returning it as a secured byte array. The internally
	 * computed value is unchanged.
	 * @author Miko Elbrecht
	 * @since D0F0N0P0
	 * @see exportSecure
	 */
	fun exportSecureX(length: Int): SecuredByteArray = SecuredByteArray(exportX(length))

	/**
	 * Flushes the computed hash value, returning it as a byte array. The internally computed value is reset.
	 * This method will use a default length that maintains full security of the XOF.
	 * @author Miko Elbrecht
	 * @since D0F0N0P0
	 * @see flushX
	 * @see flushSecure
	 * @see export
	 */
	abstract override fun flush(): ByteArray

	/**
	 * Flushes the computed hash value with the specified length, returning it as a byte array. The internally computed
	 * value is reset.
	 * @author Miko Elbrecht
	 * @since D0F0N0P0
	 * @see flush
	 * @see exportX
	 */
	fun flushX(length: Int): ByteArray

	/**
	 * Flushes the computed hash value, returning it as a secured byte array. The internally computed value is reset.
	 * This method will use a default length that maintains full security of the XOF.
	 * @author Miko Elbrecht
	 * @since D0F0N0P0
	 * @see flushSecureX
	 */
	override fun flushSecure(): SecuredByteArray = SecuredByteArray(flush())

	/**
	 * Flushes the computed hash value with the specified length, returning it as a secured byte array. The internally
	 * computed value is reset.
	 * @author Miko Elbrecht
	 * @since D0F0N0P0
	 * @see flushSecure
	 */
	fun flushSecureX(length: Int): SecuredByteArray = SecuredByteArray(flushX(length))

	/**
	 * Exports a part of the computed hash value with the specified length, returning it as a byte array.
	 * The internally computed value is unchanged. Subsequent calls to this function, until a call to [export], [flush],
	 * or their secure / XOF variants, will export the parts after this one.
	 * @author Miko Elbrecht
	 * @since D0F0N0P0
	 * @see exportIncrementalSecure
	 */
	fun exportIncremental(length: Int): ByteArray

	/**
	 * Exports a part of the computed hash value with the specified length, returning it as a secured byte array.
	 * The internally computed value is unchanged. Subsequent calls to this function, until a call to [export], [flush],
	 * or their secure / XOF variants, will export the parts after this one.
	 * @author Miko Elbrecht
	 * @since D0F0N0P0
	 * @see exportIncremental
	 */
	fun exportIncrementalSecure(length: Int): SecuredByteArray = SecuredByteArray(exportIncremental(length))
}