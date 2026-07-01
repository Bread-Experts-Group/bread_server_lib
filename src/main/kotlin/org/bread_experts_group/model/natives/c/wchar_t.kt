package org.bread_experts_group.model.natives.c

import org.bread_experts_group.model.natives.Datatype
import org.bread_experts_group.model.natives.Datatype.Companion.invoke
import org.bread_experts_group.model.natives.DatatypeBacked
import org.bread_experts_group.model.natives.NativeArray
import java.lang.foreign.MemoryLayout

@DatatypeBacked("wchar_t")
abstract class wchar_t : Number(), Comparable<wchar_t>, Datatype {
	companion object {
		fun nullTerminated(layouts: Map<String, MemoryLayout>, string: String): NativeArray<wchar_t> {
			val char = Datatype.getDatatype(layouts, wchar_t::class)
			val array = NativeArray.of<wchar_t>(layouts, string.length + 1L)
			string.forEachIndexed { index, ch -> array[index] = char(ch) }
			return array
		}
	}
}