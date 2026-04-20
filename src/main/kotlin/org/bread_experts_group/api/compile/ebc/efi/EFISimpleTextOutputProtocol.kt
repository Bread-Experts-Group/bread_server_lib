package org.bread_experts_group.api.compile.ebc.efi

import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.Address
import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.accessN
import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.nat

object EFISimpleTextOutputProtocol {
	@JvmStatic
	@ExternalCall
	private external fun reset(pPtr: Address, pThis: Address, extendedVerification: Boolean): EFIStatus

	@JvmStatic
	fun reset(pThis: Address?, extendedVerification: Boolean): EFIStatus {
		if (pThis == null) return -1
		return this.reset(
			accessN(pThis),
			pThis, extendedVerification
		)
	}

	@JvmStatic
	@ExternalCall
	external fun outputString(pPtr: Address, pThis: Address, string: Address): EFIStatus

	@JvmStatic
	fun outputString(pThis: Address?, string: Address?): EFIStatus {
		if (pThis == null || string == null) return -1
		return this.outputString(
			accessN(pThis nat 1),
			pThis, string
		)
	}
}