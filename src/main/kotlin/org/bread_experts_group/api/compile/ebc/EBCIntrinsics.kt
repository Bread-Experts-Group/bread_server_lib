package org.bread_experts_group.api.compile.ebc

object EBCIntrinsics {
	internal val internalName = EBCIntrinsics::class.qualifiedName!!.replace('.', '/')

	class Address private constructor() {
		companion object {
			internal val internalName = Address::class.qualifiedName!!
				.replace('.', '/')
				.reversed()
				.replaceFirst('/', '$')
				.reversed()
		}
	}

	@JvmStatic
	val Any.address: Address
		external get

	@JvmStatic
	external fun Address.toLong(): Long

	@JvmStatic
	external fun naturalSize(): Long

	@JvmStatic
	external fun allocateN(): Address

	@JvmStatic
	external fun allocate64(): Address

	@JvmStatic
	external fun allocate32(): Address

	@JvmStatic
	external operator fun Address?.plus(by: Long): Address

	@JvmStatic
	external infix fun Address?.nat(by: Long): Address

	@JvmStatic
	external fun accessN(at: Address): Address

	@JvmStatic
	external fun writeN(at: Address, what: Address)

	@JvmStatic
	external fun access64(at: Address): Long

	@JvmStatic
	external fun write64(at: Address, what: Long)

	@JvmStatic
	external fun access32(at: Address): Int

	@JvmStatic
	external fun write32(at: Address, what: Int)

	@JvmStatic
	external fun access16(at: Address): Short

	@JvmStatic
	external fun write16(at: Address, what: Short)

	@JvmStatic
	external fun access8(at: Address): Byte

	@JvmStatic
	external fun write8(at: Address, what: Byte)
}