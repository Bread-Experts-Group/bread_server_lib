package org.bread_experts_group.api.compile.ebc.efi

import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.Address
import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.access32
import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.access64
import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.plus

object EFITableHeader {
	@JvmStatic
	fun signature(at: Address?): Long = if (at != null) access64(at) else 0

	@JvmStatic
	fun revision(at: Address): Int = access32(at + 8)

	@JvmStatic
	fun headerSize(at: Address): Int = access32(at + 12)

	@JvmStatic
	fun crc32(at: Address): Int = access32(at + 16)

	@JvmStatic
	fun reserved(at: Address): Int = access32(at + 20)

	const val OFFSET: Long = 24L
}