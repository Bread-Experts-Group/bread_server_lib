package org.bread_experts_group.model.natives.nt.datatype

import org.bread_experts_group.model.natives.c.int_t
import java.lang.foreign.MemorySegment

typealias WHV_EMULATOR_TRANSLATE_GVA_PAGE_CALLBACK = (
	Context: MemorySegment,
	Gva: WHV_GUEST_VIRTUAL_ADDRESS,
	TranslateFlags: int_t, // TODO IndexedEnumSet<WHV_TRANSLATE_GVA_FLAGS>,
	TranslationResult: MemorySegment, // TODO Pointer<WHV_TRANSLATE_GVA_RESULT_CODE>,
	Gpa: MemorySegment // TODO Pointer<WHV_GUEST_PHYSICAL_ADDRESS>
) -> HRESULT