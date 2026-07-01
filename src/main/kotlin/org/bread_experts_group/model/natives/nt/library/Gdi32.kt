package org.bread_experts_group.model.natives.nt.library

import org.bread_experts_group.model.natives.Library
import org.bread_experts_group.model.natives.LookupBacked
import org.bread_experts_group.model.natives.nt.datatype.BOOL
import org.bread_experts_group.model.natives.nt.datatype.HBRUSH
import org.bread_experts_group.model.natives.nt.datatype.HGDIOBJ
import org.bread_experts_group.model.natives.nt.datatype.wingdi.COLORREF

@Suppress("FunctionName")
@LookupBacked("Gdi32.dll")
abstract class Gdi32 internal constructor() : Library {
	// TODO: Windows NT 3.51
	abstract fun CreateSolidBrush(color: COLORREF): HBRUSH
	abstract fun DeleteObject(ho: HGDIOBJ): BOOL

	companion object {
		fun RGB(r: Int, g: Int, b: Int): Int = r or (g shl 8) or (b shl 16)
	}
}