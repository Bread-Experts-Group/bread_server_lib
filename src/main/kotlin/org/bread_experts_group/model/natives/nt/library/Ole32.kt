package org.bread_experts_group.model.natives.nt.library

import org.bread_experts_group.model.natives.Library
import org.bread_experts_group.model.natives.LookupBacked
import org.bread_experts_group.model.natives.Pointer
import org.bread_experts_group.model.natives.nt.datatype.GUID
import org.bread_experts_group.model.natives.nt.datatype.HRESULT

@Suppress("FunctionName")
@LookupBacked("Ole32.dll")
abstract class Ole32 internal constructor() : Library {
	// TODO: Windows NT 3.51, OLE 2.10.35.35
	abstract fun CoCreateGuid(pguid: Pointer<GUID>): HRESULT
}