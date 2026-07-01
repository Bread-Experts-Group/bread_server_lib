package org.bread_experts_group.model.natives.nt.datatype

import java.lang.foreign.MemorySegment

typealias WHV_EMULATOR_MEMORY_CALLBACK = (
	Context: MemorySegment,
	MemoryAccess: WHV_EMULATOR_MEMORY_ACCESS_INFO // TODO:  Pointer<WHV_EMULATOR_MEMORY_ACCESS_INFO>
) -> HRESULT