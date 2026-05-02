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

	@JvmStatic
	@ExternalCall
	external fun queryMode(pPtr: Address, pThis: Address, modeNumber: UINTN, columns: Address, rows: Address): EFIStatus

	@JvmStatic
	fun queryMode(pThis: Address?, modeNumber: UINTN, columns: Address?, rows: Address?): EFIStatus {
		if (pThis == null || columns == null || rows == null) return -1
		return this.queryMode(
			accessN(pThis nat 3),
			pThis, modeNumber, columns, rows
		)
	}

	@JvmStatic
	@ExternalCall
	external fun setMode(pPtr: Address, pThis: Address, modeNumber: UINTN): EFIStatus

	@JvmStatic
	fun setMode(pThis: Address?, modeNumber: UINTN): EFIStatus {
		if (pThis == null) return -1
		return this.setMode(
			accessN(pThis nat 4),
			pThis, modeNumber
		)
	}

	@JvmStatic
	@ExternalCall
	external fun clearScreen(pPtr: Address, pThis: Address): EFIStatus

	@JvmStatic
	fun clearScreen(pThis: Address?): EFIStatus {
		if (pThis == null) return -1
		return this.clearScreen(
			accessN(pThis nat 6),
			pThis
		)
	}

	@JvmStatic
	@ExternalCall
	external fun setCursorPosition(pPtr: Address, pThis: Address, column: UINTN, row: UINTN): EFIStatus

	@JvmStatic
	fun setCursorPosition(pThis: Address?, column: UINTN, row: UINTN): EFIStatus {
		if (pThis == null) return -1
		return this.setCursorPosition(
			accessN(pThis nat 7),
			pThis, column, row
		)
	}
}