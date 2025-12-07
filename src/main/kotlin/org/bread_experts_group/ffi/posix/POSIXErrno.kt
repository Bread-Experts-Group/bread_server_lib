package org.bread_experts_group.ffi.posix

import org.bread_experts_group.Mappable

enum class POSIXErrno(override val id: UInt) : Mappable<POSIXErrno, UInt> {
	EAFNOSUPPORT(97u);

	override val tag: String = name
	override fun toString(): String = stringForm()
}