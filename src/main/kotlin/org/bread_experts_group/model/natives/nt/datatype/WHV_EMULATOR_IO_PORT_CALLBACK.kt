package org.bread_experts_group.model.natives.nt.datatype

import java.lang.foreign.MemorySegment

typealias WHV_EMULATOR_IO_PORT_CALLBACK = (
	Context: MemorySegment,
	IoAccess: WHV_EMULATOR_IO_ACCESS_INFO // TODO: Pointer<WHV_EMULATOR_IO_ACCESS_INFO>
) -> HRESULT