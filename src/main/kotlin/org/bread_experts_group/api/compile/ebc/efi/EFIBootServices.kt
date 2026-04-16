package org.bread_experts_group.api.compile.ebc.efi

import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.Address
import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.accessN
import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.nat
import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.plus

object EFIBootServices {
	const val SIGNATURE: Long = 0x56524553544F4F42

	// raise tpl 0
	// restore tpl 1

	// allocatepages 2
	// freepages 3
	// getmemorymap 4
	// allocatepool 5
	// freepool 6

	// createevent 7
	// settimer 8
	// waitforevent 9
	// signalevent 10
	// closeevent 11
	// checkevent 12

	// installprotocolinterface 13
	// reinstallprotocolinterface 14
	// uninstallprotocolinterface 15
	// handleprotocol 16
	// void* reserved 17
	// registerprotocolnotify 18
	// locatehandle 19
	// locatedevicepath 20
	// installconfigurationtable 21

	// loadimage 22
	// startimage 23
	// exit 24
	// unloadimage 25
	// exitbootservices 26

	@JvmStatic
	@ExternalCall
	private external fun getMemoryMapN(
		pPtr: Address,
		memoryMapSize: Address?,
		memoryMap: Address?,
		mapKey: Address?,
		descriptorSize: Address?,
		descriptorVersion: Address?
	): EFIStatus

	@JvmStatic
	fun getMemoryMap(
		bootServices: Address?,
		memoryMapSize: Address?,
		memoryMap: Address?,
		mapKey: Address?,
		descriptorSize: Address?,
		descriptorVersion: Address?
	): EFIStatus = this.getMemoryMapN(
		accessN((bootServices + EFITableHeader.OFFSET) nat 4),
		memoryMapSize, memoryMap, mapKey, descriptorSize, descriptorVersion
	)

	@JvmStatic
	@ExternalCall
	private external fun allocatePoolN(
		pPtr: Address,
		poolType: Int,
		size: UINTN,
		buffer: Address?
	): EFIStatus

	@JvmStatic
	fun allocatePool(
		bootServices: Address?,
		poolType: Int,
		size: UINTN,
		buffer: Address?
	): EFIStatus {
		if (bootServices == null || buffer == null) return -1
		return this.allocatePoolN(
			accessN((bootServices + EFITableHeader.OFFSET) nat 5),
			poolType, size, buffer
		)
	}

	@JvmStatic
	@ExternalCall
	private external fun exitN(
		pPtr: Address,
		imageHandle: Address, exitStatus: EFIStatus, exitDataSize: UINTN, exitData: Address?
	): EFIStatus

	@JvmStatic
	fun exit(
		bootServices: Address?,
		imageHandle: Address?,
		exitStatus: EFIStatus,
		exitDataSize: UINTN,
		exitData: Address?
	): EFIStatus {
		if (bootServices == null || imageHandle == null) return -1
		return this.exitN(
			accessN((bootServices + EFITableHeader.OFFSET) nat 24),
			imageHandle, exitStatus, exitDataSize, exitData
		)
	}

	@JvmStatic
	@ExternalCall
	private external fun exitBootServicesN(
		pPtr: Address,
		imageHandle: Address, mapKey: UINTN
	): EFIStatus

	@JvmStatic
	fun exitBootServices(
		bootServices: Address?,
		imageHandle: Address?, mapKey: UINTN
	): EFIStatus {
		if (bootServices == null || imageHandle == null) return -1
		return this.exitBootServicesN(
			accessN((bootServices + EFITableHeader.OFFSET) nat 26),
			imageHandle, mapKey
		)
	}
}