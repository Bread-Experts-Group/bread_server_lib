package org.bread_experts_group.api.compile.ebc.efi

interface EFIBootServicesTable {
	val header: EFITableHeader
	fun allocatePool(poolType: EFIMemoryType, size: Long): EFIStatusReturned1
}