package org.bread_experts_group.api.computer

/**
 * A memory module for a Bread Mod computer, with a settable capacity.
 *
 * **TODO**: Write to disk for very large capacities.
 *
 * **TODO**: Use an array solution with support for [UInt]s.
 * @param capacity The capacity of the memory module.
 * @param effectiveAddress The address on which this memory segment begins for the computer (not set for contiguous).
 * @throws IllegalArgumentException If the capacity is negative.
 * @since D0F0N0P0
 * @author Miko Elbrecht
 */
class MemoryModule(val capacity: UInt, val effectiveAddress: ULong? = null) {
	@OptIn(ExperimentalUnsignedTypes::class)
	private var memory: UByteArray = UByteArray(this.capacity.toInt()) {
		(0x00u).toUByte()
	}

	@OptIn(ExperimentalUnsignedTypes::class)
	fun erase() {
		this.memory = UByteArray(this.capacity.toInt()) { (0x00u).toUByte() }
	}

	/**
	 * Gets the value at the specified address.
	 * @param address The address to read from.
	 * @return The value at the specified address.
	 * @throws ArrayIndexOutOfBoundsException If the address is out of bounds.
	 * @throws IllegalArgumentException If the address is negative.
	 * @see set
	 * @since D0F0N0P0
	 * @author Miko Elbrecht
	 */
	@OptIn(ExperimentalUnsignedTypes::class)
	operator fun get(address: Int): UByte = this.memory[address]

	/**
	 * Sets the value at the specified address.
	 * @param address The address to write to.
	 * @param value The value to write.
	 * @throws ArrayIndexOutOfBoundsException If the address is out of bounds.
	 * @throws IllegalArgumentException If the address is negative.
	 * @see get
	 * @since D0F0N0P0
	 * @author Miko Elbrecht
	 */
	@OptIn(ExperimentalUnsignedTypes::class)
	operator fun set(address: Int, value: UByte) {
		this.memory[address] = value
	}
}