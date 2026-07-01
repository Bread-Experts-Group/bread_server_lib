@file:Suppress(
	"ClassName", "ClassName", "ClassName", "ClassName", "ClassName", "ClassName", "ClassName", "ClassName",
	"ClassName", "ClassName", "ClassName", "ClassName", "ClassName", "ClassName", "ClassName", "ClassName", "ClassName",
	"ClassName", "ClassName", "ClassName", "ClassName", "ClassName", "ClassName", "ClassName"
)

package org.bread_experts_group.model.natives.c

import org.bread_experts_group.model.natives.Datatype
import org.bread_experts_group.model.natives.Datatype.Companion.invoke
import org.bread_experts_group.model.natives.DatatypeBacked
import org.bread_experts_group.model.natives.NativeArray
import java.lang.foreign.MemoryLayout

@DatatypeBacked("char")
abstract class char_t : Number(), Comparable<char_t>, Datatype {
	companion object {
		fun nullTerminated(layouts: Map<String, MemoryLayout>, string: String): NativeArray<char_t> {
			val char = Datatype.getDatatype(layouts, char_t::class)
			val array = NativeArray.of<char_t>(layouts, string.length + 1L)
			string.forEachIndexed { index, ch -> array[index] = char(ch.code.toByte()) }
			return array
		}
	}
}