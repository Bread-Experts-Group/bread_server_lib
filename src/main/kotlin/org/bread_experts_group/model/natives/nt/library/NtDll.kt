package org.bread_experts_group.model.natives.nt.library

import org.bread_experts_group.model.natives.Library
import org.bread_experts_group.model.natives.LookupBacked
import org.bread_experts_group.model.natives.Pointer
import org.bread_experts_group.model.natives.nt.datatype.BOOLEAN
import org.bread_experts_group.model.natives.nt.datatype.NTSTATUS
import org.bread_experts_group.model.natives.nt.datatype.PCUNICODE_STRING
import org.bread_experts_group.model.natives.nt.datatype.ULONG

@Suppress("FunctionName")
@LookupBacked("NtDll.dll")
abstract class NtDll internal constructor() : Library {
	// TODO: Windows NT 3.51
	abstract fun RtlAdjustPrivilege(
		Privilege: ULONG, Enable: BOOLEAN, Client: BOOLEAN, WasEnabled: Pointer<BOOLEAN>
	): NTSTATUS

	abstract fun NtDisplayString(String: PCUNICODE_STRING): NTSTATUS
}