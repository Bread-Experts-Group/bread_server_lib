package org.bread_experts_group.ffi.windows.bcrypt

import org.bread_experts_group.generic.Mappable
import java.lang.foreign.ValueLayout

enum class WindowsBCryptHashOperationType(
	override val id: UInt
) : Mappable<WindowsBCryptHashOperationType, UInt> {
	BCRYPT_HASH_OPERATION_HASH_DATA(1u),
	BCRYPT_HASH_OPERATION_FINISH_HASH(2u);

	override val tag: String = name
	override fun toString(): String = stringForm()
}

val BCRYPT_HASH_OPERATION_TYPE: ValueLayout.OfInt = ValueLayout.JAVA_INT