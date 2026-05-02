package org.bread_experts_group.api.compile.ebc.efi

import org.bread_experts_group.api.compile.ebc.EBCIntrinsics
import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.accessN
import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.plus

object EFISimpleFileSystemProtocol {
	@JvmStatic
	@ExternalCall
	private external fun openVolume(
		ptr: EBCIntrinsics.Address,
		pThis: EBCIntrinsics.Address?,
		root: EBCIntrinsics.Address?
	): EFIStatus

	@JvmStatic
	fun openVolume(
		pThis: EBCIntrinsics.Address?,
		root: EBCIntrinsics.Address?
	): EFIStatus = this.openVolume(
		accessN(pThis + 8), // TODO: Padding!
		pThis, root
	)
}