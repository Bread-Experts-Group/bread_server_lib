package org.bread_experts_group.model.natives.nt.library

import org.bread_experts_group.model.natives.Library
import org.bread_experts_group.model.natives.LookupBacked
import org.bread_experts_group.model.natives.NativeArray
import org.bread_experts_group.model.natives.c.char_t
import org.bread_experts_group.model.natives.c.int_t

@LookupBacked("msvcrt")
abstract class MSVCRT internal constructor() : Library {
	abstract fun printf(format: NativeArray<char_t>): int_t
}