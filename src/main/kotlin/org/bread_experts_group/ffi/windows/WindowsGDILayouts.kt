package org.bread_experts_group.ffi.windows

import java.lang.foreign.MemoryLayout
import java.lang.foreign.MemoryLayout.PathElement.groupElement
import java.lang.foreign.StructLayout

val BI_BITFIELDS = 3

val FXPT2DOT30 = long

val CIEXYZ: StructLayout = MemoryLayout.structLayout(
	FXPT2DOT30.withName("ciexyzX"),
	FXPT2DOT30.withName("ciexyzY"),
	FXPT2DOT30.withName("ciexyzZ")
)

val CIEXYZTRIPLE: StructLayout = MemoryLayout.structLayout(
	CIEXYZ.withName("ciexyzRed"),
	CIEXYZ.withName("ciexyzGreen"),
	CIEXYZ.withName("ciexyzBlue")
)

val BITMAPV5HEADER: StructLayout = MemoryLayout.structLayout(
	DWORD.withName("bV5Size"),
	LONG.withName("bV5Width"),
	LONG.withName("bV5Height"),
	WORD.withName("bV5Planes"),
	WORD.withName("bV5BitCount"),
	DWORD.withName("bV5Compression"),
	DWORD.withName("bV5SizeImage"),
	LONG.withName("bV5XPelsPerMeter"),
	LONG.withName("bV5YPelsPerMeter"),
	DWORD.withName("bV5ClrUsed"),
	DWORD.withName("bV5ClrImportant"),
	DWORD.withName("bV5RedMask"),
	DWORD.withName("bV5GreenMask"),
	DWORD.withName("bV5BlueMask"),
	DWORD.withName("bV5AlphaMask"),
	DWORD.withName("bV5CSType"),
	CIEXYZTRIPLE.withName("bV5Endpoints"),
	DWORD.withName("bV5GammaRed"),
	DWORD.withName("bV5GammaGreen"),
	DWORD.withName("bV5GammaBlue"),
	DWORD.withName("bV5Intent"),
	DWORD.withName("bV5ProfileData"),
	DWORD.withName("bV5ProfileSize"),
	DWORD.withName("bV5Reserved"),
)
val PBITMAPV5HEADER = `void*`
val BITMAPV5HEADER_bV5Size = BITMAPV5HEADER.varHandle(groupElement("bV5Size"))
val BITMAPV5HEADER_bV5Width = BITMAPV5HEADER.varHandle(groupElement("bV5Width"))
val BITMAPV5HEADER_bV5Height = BITMAPV5HEADER.varHandle(groupElement("bV5Height"))
val BITMAPV5HEADER_bV5Planes = BITMAPV5HEADER.varHandle(groupElement("bV5Planes"))
val BITMAPV5HEADER_bV5BitCount = BITMAPV5HEADER.varHandle(groupElement("bV5BitCount"))
val BITMAPV5HEADER_bV5Compression = BITMAPV5HEADER.varHandle(groupElement("bV5Compression"))
val BITMAPV5HEADER_bV5RedMask = BITMAPV5HEADER.varHandle(groupElement("bV5RedMask"))
val BITMAPV5HEADER_bV5GreenMask = BITMAPV5HEADER.varHandle(groupElement("bV5GreenMask"))
val BITMAPV5HEADER_bV5BlueMask = BITMAPV5HEADER.varHandle(groupElement("bV5BlueMask"))
val BITMAPV5HEADER_bV5AlphaMask = BITMAPV5HEADER.varHandle(groupElement("bV5AlphaMask"))