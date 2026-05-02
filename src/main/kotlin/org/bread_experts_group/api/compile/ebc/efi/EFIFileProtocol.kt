package org.bread_experts_group.api.compile.ebc.efi

import org.bread_experts_group.api.compile.ebc.EBCIntrinsics
import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.accessN
import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.nat
import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.plus

object EFIFileProtocol {
	@JvmStatic
	@ExternalCall
	private external fun open(
		ptr: EBCIntrinsics.Address,
		pThis: EBCIntrinsics.Address?,
		newHandle: EBCIntrinsics.Address?,
		fileName: EBCIntrinsics.Address?,
		openMode: Long,
		attributes: Long
	): EFIStatus

	@JvmStatic
	fun open(
		pThis: EBCIntrinsics.Address?,
		newHandle: EBCIntrinsics.Address?,
		fileName: EBCIntrinsics.Address?,
		openMode: Long,
		attributes: Long
	): EFIStatus = this.open(
		accessN(pThis + 8), // TODO: Padding!
		pThis, newHandle, fileName, openMode, attributes
	)

	@JvmStatic
	@ExternalCall
	private external fun read(
		ptr: EBCIntrinsics.Address,
		pThis: EBCIntrinsics.Address?,
		bufferSize: EBCIntrinsics.Address?,
		buffer: EBCIntrinsics.Address?
	): EFIStatus

	@JvmStatic
	fun read(
		pThis: EBCIntrinsics.Address?,
		bufferSize: EBCIntrinsics.Address?,
		buffer: EBCIntrinsics.Address?
	): EFIStatus = this.read(
		accessN((pThis + 8) nat 3), // TODO: Padding!
		pThis, bufferSize, buffer
	)
}