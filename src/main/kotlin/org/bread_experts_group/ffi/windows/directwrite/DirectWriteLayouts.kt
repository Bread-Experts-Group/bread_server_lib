package org.bread_experts_group.ffi.windows.directwrite

import org.bread_experts_group.ffi.GUID
import org.bread_experts_group.ffi.globalArena
import org.bread_experts_group.ffi.windows.`void*`

@OptIn(ExperimentalUnsignedTypes::class)
val nativeIID_IDWriteFactory = GUID(
	0xB859EE5Au,
	0xD838u,
	0x4B5Bu,
	ubyteArrayOf(0xA2u, 0xE8u),
	ubyteArrayOf(0x1Au, 0xDCu, 0x7Du, 0x93u, 0xDBu, 0x48u)
).allocate(globalArena)

val PIDWriteTextFormat = `void*`
val PIDWriteFontCollection = `void*`