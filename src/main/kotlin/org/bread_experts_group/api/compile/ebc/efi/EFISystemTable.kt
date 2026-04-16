package org.bread_experts_group.api.compile.ebc.efi

import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.Address
import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.access32
import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.access64
import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.accessN
import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.nat
import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.plus

object EFISystemTable {
	const val SIGNATURE: Long = 0x5453595320494249
	const val REVISION_2_100: Int = ((2 shl 16) or (100))
	const val REVISION_2_90: Int = ((2 shl 16) or (90))
	const val REVISION_2_80: Int = ((2 shl 16) or (80))
	const val REVISION_2_70: Int = ((2 shl 16) or (70))
	const val REVISION_2_60: Int = ((2 shl 16) or (60))
	const val REVISION_2_50: Int = ((2 shl 16) or (50))
	const val REVISION_2_40: Int = ((2 shl 16) or (40))
	const val REVISION_2_31: Int = ((2 shl 16) or (31))
	const val REVISION_2_30: Int = ((2 shl 16) or (30))
	const val REVISION_2_20: Int = ((2 shl 16) or (20))
	const val REVISION_2_10: Int = ((2 shl 16) or (10))
	const val REVISION_2_00: Int = ((2 shl 16) or (0))
	const val REVISION_1_10: Int = ((1 shl 16) or (10))
	const val REVISION_1_02: Int = ((1 shl 16) or (2))

	@JvmStatic
	fun firmwareVendor(at: Address?): Address = accessN(at + EFITableHeader.OFFSET)

	@JvmStatic
	fun firmwareRevision(at: Address?): Int = access32((at + EFITableHeader.OFFSET) nat 1)

	// TODO: Alignment appears to put a 4-byte padding here on x86-64
	//		whether using a natural to account for this is a good idea or not, we'll see.
	@JvmStatic
	fun consoleInHandle(at: Address?): Address = (at + EFITableHeader.OFFSET) nat 2

	@JvmStatic
	fun conIn(at: Address?): Address = accessN((at + EFITableHeader.OFFSET) nat 3)

	@JvmStatic
	fun consoleOutHandle(at: Address?): Address = (at + EFITableHeader.OFFSET) nat 4

	@JvmStatic
	fun conOut(at: Address?): Address = accessN((at + EFITableHeader.OFFSET) nat 5)

	@JvmStatic
	fun standardErrorHandle(at: Address?): Address = (at + EFITableHeader.OFFSET) nat 6

	@JvmStatic
	fun stdErr(at: Address?): Address = accessN((at + EFITableHeader.OFFSET) nat 7)

	@JvmStatic
	fun runtimeServices(at: Address?): Address = accessN((at + EFITableHeader.OFFSET) nat 8)

	@JvmStatic
	fun bootServices(at: Address?): Address = accessN((at + EFITableHeader.OFFSET) nat 9)

	@JvmStatic
	fun numberOfTableEntries(at: Address?): Long = access64((at + EFITableHeader.OFFSET) nat 10)

	@JvmStatic
	fun configurationTable(at: Address?): Address = accessN((at + EFITableHeader.OFFSET) nat 11)
}