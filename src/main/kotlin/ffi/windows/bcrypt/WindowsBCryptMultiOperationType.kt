package org.bread_experts_group.ffi.windows.bcrypt

import org.bread_experts_group.coder.Mappable
import java.lang.foreign.ValueLayout

enum class WindowsBCryptMultiOperationType(
	override val id: UInt
) : Mappable<WindowsBCryptMultiOperationType, UInt> {
	BCRYPT_OPERATION_TYPE_HASH(1u);

	override val tag: String = name
	override fun toString(): String = stringForm()
}

val BCRYPT_MULTI_OPERATION_TYPE: ValueLayout.OfInt = ValueLayout.JAVA_INT