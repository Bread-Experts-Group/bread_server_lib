package org.bread_experts_group.ffi.windows

import org.bread_experts_group.coder.Mappable
import java.lang.foreign.ValueLayout

enum class WindowsNTStatus(
	override val id: UInt,
	override val tag: String
) : Mappable<WindowsNTStatus, UInt> {
	STATUS_SUCCESS(0x00000000u, "The operation completed successfully."),
	STATUS_INVALID_HANDLE(0xC0000008u, "An invalid HANDLE was specified."),
	STATUS_INVALID_PARAMETER(0xC000000Du, "An invalid parameter was passed to a service or function."),
	STATUS_BUFFER_TOO_SMALL(
		0xC0000023u, "The buffer is too small to contain the entry." +
				" No information has been written to the buffer."
	);

	override fun toString(): String = stringForm()
}

val NTSTATUS: ValueLayout.OfInt = ValueLayout.JAVA_INT