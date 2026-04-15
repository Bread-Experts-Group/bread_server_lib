package org.bread_experts_group.ffi.windows.directwrite

import org.bread_experts_group.ffi.GUID
import org.bread_experts_group.ffi.globalArena
import org.bread_experts_group.ffi.windows.*
import java.lang.foreign.MemoryLayout
import java.lang.foreign.MemoryLayout.PathElement.groupElement
import java.lang.foreign.StructLayout
import java.lang.invoke.VarHandle

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
val PIDWriteTextLayout = `void*`
val PIDWriteFontFamily = `void*`
val PIDWriteFont = `void*`
val PIDWriteFontFace = `void*`
val PIDWriteLocalizedStrings = `void*`

val DWRITE_HIT_TEST_METRICS: StructLayout = MemoryLayout.structLayout(
	UINT32.withName("textPosition"),
	UINT32.withName("length"),
	FLOAT.withName("left"),
	FLOAT.withName("top"),
	FLOAT.withName("width"),
	FLOAT.withName("height"),
	UINT32.withName("bidiLevel"),
	BOOL.withName("isText"),
	BOOL.withName("isTrimmed")
)
val PDWRITE_HIT_TEST_METRICS = `void*`
val DWRITE_HIT_TEST_METRICS_textPosition: VarHandle = DWRITE_HIT_TEST_METRICS.varHandle(groupElement("textPosition"))
val DWRITE_HIT_TEST_METRICS_length: VarHandle = DWRITE_HIT_TEST_METRICS.varHandle(groupElement("length"))
val DWRITE_HIT_TEST_METRICS_left: VarHandle = DWRITE_HIT_TEST_METRICS.varHandle(groupElement("left"))
val DWRITE_HIT_TEST_METRICS_top: VarHandle = DWRITE_HIT_TEST_METRICS.varHandle(groupElement("top"))
val DWRITE_HIT_TEST_METRICS_width: VarHandle = DWRITE_HIT_TEST_METRICS.varHandle(groupElement("width"))
val DWRITE_HIT_TEST_METRICS_height: VarHandle = DWRITE_HIT_TEST_METRICS.varHandle(groupElement("height"))
val DWRITE_HIT_TEST_METRICS_bidiLevel: VarHandle = DWRITE_HIT_TEST_METRICS.varHandle(groupElement("bidiLevel"))
val DWRITE_HIT_TEST_METRICS_isText: VarHandle = DWRITE_HIT_TEST_METRICS.varHandle(groupElement("isText"))
val DWRITE_HIT_TEST_METRICS_isTrimmed: VarHandle = DWRITE_HIT_TEST_METRICS.varHandle(groupElement("isTrimmed"))

val DWRITE_FONT_METRICS: StructLayout = MemoryLayout.structLayout(
	UINT16.withName("designUnitsPerEm"),
	UINT16.withName("ascent"),
	UINT16.withName("descent"),
	INT16.withName("lineGap"),
	UINT16.withName("capHeight"),
	UINT16.withName("xHeight"),
	INT16.withName("underlinePosition"),
	UINT16.withName("underlineThickness"),
	INT16.withName("strikethroughPosition"),
	UINT16.withName("strikethroughThickness")
)
val PDWRITE_FONT_METRICS = `void*`
val DWRITE_FONT_METRICS_designUnitsPerEm: VarHandle = DWRITE_FONT_METRICS.varHandle(groupElement("designUnitsPerEm"))
val DWRITE_FONT_METRICS_ascent: VarHandle = DWRITE_FONT_METRICS.varHandle(groupElement("ascent"))
val DWRITE_FONT_METRICS_descent: VarHandle = DWRITE_FONT_METRICS.varHandle(groupElement("descent"))
val DWRITE_FONT_METRICS_lineGap: VarHandle = DWRITE_FONT_METRICS.varHandle(groupElement("lineGap"))
val DWRITE_FONT_METRICS_capHeight: VarHandle = DWRITE_FONT_METRICS.varHandle(groupElement("capHeight"))
val DWRITE_FONT_METRICS_xHeight: VarHandle = DWRITE_FONT_METRICS.varHandle(groupElement("xHeight"))
val DWRITE_FONT_METRICS_underlinePosition: VarHandle = DWRITE_FONT_METRICS.varHandle(groupElement("underlinePosition"))
val DWRITE_FONT_METRICS_underlineThickness: VarHandle = DWRITE_FONT_METRICS.varHandle(
	groupElement("underlineThickness")
)
val DWRITE_FONT_METRICS_strikethroughPosition: VarHandle = DWRITE_FONT_METRICS.varHandle(
	groupElement("strikethroughPosition")
)
val DWRITE_FONT_METRICS_strikethroughThickness: VarHandle = DWRITE_FONT_METRICS.varHandle(
	groupElement("strikethroughThickness")
)