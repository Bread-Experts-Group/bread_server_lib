package org.bread_experts_group.model.natives.nt.library

import org.bread_experts_group.model.natives.Library
import org.bread_experts_group.model.natives.LookupBacked
import org.bread_experts_group.model.natives.Pointer
import org.bread_experts_group.model.natives.nt.datatype.*
import org.bread_experts_group.model.natives.nt.datatype.roapi.RO_INIT_TYPE
import java.lang.foreign.MemorySegment

@Suppress("FunctionName")
@LookupBacked("api-ms-win-core-winrt-l1-1-0.dll")
abstract class RuntimeObject internal constructor() : Library {
	// TODO: DLL date
	abstract fun WindowsCreateString(sourceString: PCNZWCH, length: UINT32, string: Pointer<HSTRING>): HRESULT

	abstract fun RoInitialize(initType: RO_INIT_TYPE): HRESULT
	abstract fun RoGetActivationFactory(
		activatableClassId: HSTRING, iid: REFIID,
		factory: Pointer<MemorySegment>
	): HRESULT
}