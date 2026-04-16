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
	external operator fun Address?.plus(by: Long): Address

	@JvmStatic
	external infix fun Address?.nat(by: Long): Address

	@JvmStatic
	external fun accessN(at: Address): Address

	@JvmStatic
	external fun access64(at: Address): Long

	@JvmStatic
	external fun access32(at: Address): Int

	@JvmStatic
	external fun access16(at: Address): Short

	@JvmStatic
	external fun access8(at: Address): Byte
}