package org.bread_experts_group.api.secure.cryptography.feature.hash

interface SIMDHashingCommon {
	/**
	 * Adds the specified bytes to the hash values. Order is not guaranteed.
	 * @param i The hash index to add a byte to.
	 * @param b The byte to add to the hash.
	 * @throws IllegalArgumentException If [i] and [b] have different sizes, or [i] contains indices out of bounds.
	 * @author Miko Elbrecht
	 * @since D0F0N0P0
	 */
	fun add(i: IntArray, b: ByteArray)

	/**
	 * Adds the specified bytes to the hash values. Order is not guaranteed.
	 * @param i The hash index to add a byte to.
	 * @param b The byte to add to the hash.
	 * @throws IllegalArgumentException If [i] and [b] have different sizes, or [i] contains indices out of bounds.
	 * @author Miko Elbrecht
	 * @since D0F0N0P0
	 */
	fun add(i: IntArray, b: Array<ByteArray>)

	/**
	 * Adds the specified bytes to the hash values. Order is not guaranteed.
	 * @throws IllegalArgumentException If [b] does not have the same amount of elements of the current SIMD hash
	 * operation.
	 * @author Miko Elbrecht
	 * @since D0F0N0P0
	 */
	fun add(b: ByteArray)

	/**
	 * Adds the specified bytes to the hash values. Order is not guaranteed.
	 * @throws IllegalArgumentException If [b] does not have the same amount of elements of the current SIMD hash
	 * operation.
	 * @author Miko Elbrecht
	 * @since D0F0N0P0
	 */
	fun add(b: Array<ByteArray>)

	/**
	 * Flushes the specified hashes to an array of byte arrays. The contents of the specified hashes are reset.
	 * @throws IllegalArgumentException If [i] contains indices out of bounds.
	 * @author Miko Elbrecht
	 * @since D0F0N0P0
	 */
	fun flush(i: IntArray): Array<ByteArray>

	/**
	 * Flushes all hashes to an array of byte arrays. The contents of the hashes are reset.
	 * @author Miko Elbrecht
	 * @since D0F0N0P0
	 */
	fun flush(): Array<ByteArray>
}