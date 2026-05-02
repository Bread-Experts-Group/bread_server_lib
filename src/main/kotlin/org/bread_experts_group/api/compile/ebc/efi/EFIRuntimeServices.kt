package org.bread_experts_group.api.compile.ebc.efi

import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.Address
import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.accessN
import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.nat
import org.bread_experts_group.api.compile.ebc.EBCIntrinsics.plus

object EFIRuntimeServices {
	// get time 0
	// set time 1
	// get wakeup time 2
	// set wakeup time 3

	// setvirtualaddressmap 4
	// convertpointer 5

	// get variable 6
	// get next variable name 7
	// set variable 8

	// get next high monotonic count 9
	// reset system 10

	// update capsule 11
	// query capsule capabilities 12

	// query variable info 13

	@JvmStatic
	@ExternalCall
	private external fun getNextVariableNameN(
		pPtr: Address,
		variableNameSize: Address,
		variableName: Address,
		vendorGuid: Address?
	): EFIStatus

	@JvmStatic
	fun getNextVariableName(
		runtimeServices: Address?,
		variableNameSize: Address?,
		variableName: Address?,
		vendorGuid: Address?
	): EFIStatus {
		if (runtimeServices == null || variableNameSize == null || variableName == null)
			return -1
		return this.getNextVariableNameN(
			accessN((runtimeServices + EFITableHeader.OFFSET) nat 7),
			variableNameSize, variableName, vendorGuid
		)
	}
}