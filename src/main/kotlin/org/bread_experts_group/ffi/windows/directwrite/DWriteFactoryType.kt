package org.bread_experts_group.ffi.windows.directwrite

import org.bread_experts_group.ffi.windows.DWORD
import org.bread_experts_group.generic.Mappable

enum class DWriteFactoryType(override val id: Int) : Mappable<DWriteFactoryType, Int> {
	DWRITE_FACTORY_TYPE_SHARED(0),
	DWRITE_FACTORY_TYPE_ISOLATED(1);

	override val tag: String = name
	override fun toString(): String = stringForm()
}

val DWRITE_FACTORY_TYPE = DWORD