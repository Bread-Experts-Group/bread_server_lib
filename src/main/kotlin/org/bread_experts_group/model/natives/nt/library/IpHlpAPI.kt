package org.bread_experts_group.model.natives.nt.library

import org.bread_experts_group.model.natives.Library
import org.bread_experts_group.model.natives.LookupBacked
import org.bread_experts_group.model.natives.nt.datatype.PIP_ADAPTER_ADDRESSES
import org.bread_experts_group.model.natives.nt.datatype.PULONG
import org.bread_experts_group.model.natives.nt.datatype.PVOID
import org.bread_experts_group.model.natives.nt.datatype.ULONG

@Suppress("FunctionName", "LocalVariableName")
@LookupBacked("Iphlpapi.dll")
abstract class IpHlpAPI internal constructor() : Library {
	// TODO: Windows date
	abstract fun GetAdaptersAddresses(
		Family: ULONG,
		Flags: ULONG,
		Reserved: PVOID,
		AdapterAddresses: PIP_ADAPTER_ADDRESSES?,
		SizePointer: PULONG
	): ULONG
}