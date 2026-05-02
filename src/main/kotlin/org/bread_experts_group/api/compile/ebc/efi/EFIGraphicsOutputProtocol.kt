package org.bread_experts_group.api.compile.ebc.efi

import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.Address
import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.accessN
import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.nat

object EFIGraphicsOutputProtocol {
	// query mode 0
	// set mode 1
	// blt 2
	// mode 3

	@JvmStatic
	@ExternalCall
	private external fun blt(
		pPtr: Address,
		pThis: Address,
		bltBuffer: Address?,
		bltOperation: Int,
		sourceX: UINTN,
		sourceY: UINTN,
		destinationX: UINTN,
		destinationY: UINTN,
		width: UINTN,
		height: UINTN,
		delta: UINTN
	): EFIStatus

	@JvmStatic
	fun blt(
		pThis: Address?,
		bltBuffer: Address?,
		bltOperation: Int,
		sourceX: UINTN,
		sourceY: UINTN,
		destinationX: UINTN,
		destinationY: UINTN,
		width: UINTN,
		height: UINTN,
		delta: UINTN
	): EFIStatus {
		if (pThis == null) return -1
		return this.blt(
			accessN(pThis nat 2),
			pThis, bltBuffer, bltOperation, sourceX, sourceY, destinationX, destinationY,
			width, height, delta
		)
	}
}