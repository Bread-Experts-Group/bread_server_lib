package org.bread_experts_group.model.natives.nt.datatype

import java.lang.foreign.MemorySegment

typealias WHV_EMULATOR_GET_VIRTUAL_PROCESSOR_REGISTERS_CALLBACK = (
	Context: MemorySegment,
	RegisterNames: MemorySegment, // TODO NativeArray
	RegisterCount: UINT32,
	RegisterValues: MemorySegment
) -> HRESULT