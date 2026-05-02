package org.bread_experts_group.api.compile.ebc.efi

import org.bread_experts_group.api.compile.ebc.EBCIntrinsics
import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.accessN
import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.nat
import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.plus

object EFILoadedImageProtocol {
	@JvmStatic
	fun deviceHandle(
		pThis: EBCIntrinsics.Address?
	): EBCIntrinsics.Address = accessN((pThis + 8) nat 2) // TODO: Padding!
}