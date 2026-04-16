package org.bread_experts_group.api.compile.ebc.efi

import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.Address
import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.accessN

object EFISimpleTextOutputProtocol {
	@JvmStatic
	@ExternalCall
	private external fun reset(pPtr: Address, pThis: Address, extendedVerification: Boolean): Long

	@JvmStatic
	fun reset(pThis: Address?, extendedVerification: Boolean): Long {
		if (pThis == null) return -1
		return this.reset(
			accessN(pThis),
			pThis, extendedVerification
		)
	}
}