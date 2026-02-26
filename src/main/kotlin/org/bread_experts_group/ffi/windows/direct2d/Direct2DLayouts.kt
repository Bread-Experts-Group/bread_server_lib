package org.bread_experts_group.ffi.windows.direct2d

import org.bread_experts_group.ffi.GUID
import org.bread_experts_group.ffi.globalArena
import org.bread_experts_group.ffi.windows.*
import org.bread_experts_group.ffi.windows.directx.DXGI_FORMAT
import java.lang.foreign.MemoryLayout
import java.lang.foreign.MemoryLayout.PathElement.groupElement
import java.lang.foreign.StructLayout
import java.lang.invoke.MethodHandle
import java.lang.invoke.VarHandle

val D2D1_FACTORY_OPTIONS: StructLayout = MemoryLayout.structLayout(
	D2D1_DEBUG_LEVEL.withName("debugLevel")
)
val PD2D1_FACTORY_OPTIONS = `void*`

@OptIn(ExperimentalUnsignedTypes::class)
val nativeIID_ID2D1Factory = GUID(
	0x06152247u,
	0x6F50u,
	0x465Au,
	ubyteArrayOf(0x92u, 0x45u),
	ubyteArrayOf(0x11u, 0x8Bu, 0xFDu, 0x3Bu, 0x60u, 0x07u)
).allocate(globalArena)

val D2D1_PIXEL_FORMAT: StructLayout = MemoryLayout.structLayout(
	DXGI_FORMAT.withName("format"),
	D2D1_ALPHA_MODE.withName("alphaMode")
)

val D2D1_RENDER_TARGET_PROPERTIES: StructLayout = MemoryLayout.structLayout(
	D2D1_RENDER_TARGET_TYPE.withName("type"),
	D2D1_PIXEL_FORMAT.withName("pixelFormat"),
	FLOAT.withName("dpiX"),
	FLOAT.withName("dpiY"),
	D2D1_RENDER_TARGET_USAGE.withName("usage"),
	D2D1_FEATURE_LEVEL.withName("minLevel")
)
val PD2D1_RENDER_TARGET_PROPERTIES = `void*`

val D2D_SIZE_U: StructLayout = MemoryLayout.structLayout(
	UINT32.withName("width"),
	UINT32.withName("height")
)
val PD2D1_SIZE_U = `void*`

val D2D1_SIZE_U = D2D_SIZE_U
val D2D1_SIZE_U_width: VarHandle = D2D1_SIZE_U.varHandle(groupElement("width"))
val D2D1_SIZE_U_height: VarHandle = D2D1_SIZE_U.varHandle(groupElement("height"))

val D2D1_HWND_RENDER_TARGET_PROPERTIES: StructLayout = MemoryLayout.structLayout(
	HWND.withName("hwnd"),
	D2D1_SIZE_U.withName("pixelSize"),
	D2D1_PRESENT_OPTIONS.withName("presentOptions")
)
val PD2D1_HWND_RENDER_TARGET_PROPERTIES = `void*`
val D2D1_HWND_RENDER_TARGET_PROPERTIES_hwnd: VarHandle = D2D1_HWND_RENDER_TARGET_PROPERTIES.varHandle(
	groupElement("hwnd")
)
val D2D1_HWND_RENDER_TARGET_PROPERTIES_pixelSize: MethodHandle = D2D1_HWND_RENDER_TARGET_PROPERTIES.sliceHandle(
	groupElement("pixelSize")
)

val PID2D1HwndRenderTarget = `void*`
val D2D1_TAG = UINT64
val PD2D1_TAG = `void*`

val D3DCOLORVALUE: StructLayout = MemoryLayout.structLayout(
	FLOAT.withName("r"),
	FLOAT.withName("g"),
	FLOAT.withName("b"),
	FLOAT.withName("a")
)
val D3DCOLORVALUE_r: VarHandle = D3DCOLORVALUE.varHandle(groupElement("r"))
val D3DCOLORVALUE_g: VarHandle = D3DCOLORVALUE.varHandle(groupElement("g"))
val D3DCOLORVALUE_b: VarHandle = D3DCOLORVALUE.varHandle(groupElement("b"))
val D3DCOLORVALUE_a: VarHandle = D3DCOLORVALUE.varHandle(groupElement("a"))

val D2D_COLOR_F = D3DCOLORVALUE
val D2D1_COLOR_F = D2D_COLOR_F
val PD2D1_COLOR_F = `void*`

val D2D_MATRIX_3X2_F: StructLayout = MemoryLayout.structLayout(
	MemoryLayout.unionLayout(
		MemoryLayout.structLayout(
			FLOAT.withName("m11"),
			FLOAT.withName("m12"),
			FLOAT.withName("m21"),
			FLOAT.withName("m22"),
			FLOAT.withName("dx"),
			FLOAT.withName("dy")
		).withName("DUMMYSTRUCTNAME"),
		MemoryLayout.structLayout(
			FLOAT.withName("_11"),
			FLOAT.withName("_12"),
			FLOAT.withName("_21"),
			FLOAT.withName("_22"),
			FLOAT.withName("_31"),
			FLOAT.withName("_32")
		).withName("DUMMYSTRUCTNAME2"),
		MemoryLayout.sequenceLayout(
			2,
			MemoryLayout.sequenceLayout(3, FLOAT)
		).withName("m")
	).withName("DUMMYUNIONNAME")
)
val D2D_MATRIX_3X2_F__11: VarHandle = D2D_MATRIX_3X2_F.varHandle(
	groupElement("DUMMYUNIONNAME"),
	groupElement("DUMMYSTRUCTNAME2"),
	groupElement("_11")
)
val D2D_MATRIX_3X2_F__12: VarHandle = D2D_MATRIX_3X2_F.varHandle(
	groupElement("DUMMYUNIONNAME"),
	groupElement("DUMMYSTRUCTNAME2"),
	groupElement("_12")
)
val D2D_MATRIX_3X2_F__21: VarHandle = D2D_MATRIX_3X2_F.varHandle(
	groupElement("DUMMYUNIONNAME"),
	groupElement("DUMMYSTRUCTNAME2"),
	groupElement("_21")
)
val D2D_MATRIX_3X2_F__22: VarHandle = D2D_MATRIX_3X2_F.varHandle(
	groupElement("DUMMYUNIONNAME"),
	groupElement("DUMMYSTRUCTNAME2"),
	groupElement("_22")
)
val D2D_MATRIX_3X2_F__31: VarHandle = D2D_MATRIX_3X2_F.varHandle(
	groupElement("DUMMYUNIONNAME"),
	groupElement("DUMMYSTRUCTNAME2"),
	groupElement("_31")
)
val D2D_MATRIX_3X2_F__32: VarHandle = D2D_MATRIX_3X2_F.varHandle(
	groupElement("DUMMYUNIONNAME"),
	groupElement("DUMMYSTRUCTNAME2"),
	groupElement("_32")
)

val D2D1_MATRIX_3X2_F = D2D_MATRIX_3X2_F
val PD2D1_MATRIX_3X2_F = `void*`

val D2D_SIZE_F: StructLayout = MemoryLayout.structLayout(
	FLOAT.withName("width"),
	FLOAT.withName("height")
)

val D2D1_SIZE_F = D2D_SIZE_F
val D2D1_SIZE_F_width: VarHandle = D2D_SIZE_F.varHandle(groupElement("width"))
val D2D1_SIZE_F_height: VarHandle = D2D_SIZE_F.varHandle(groupElement("height"))
val PD2D1_SIZE_F = `void*`

val D2D_POINT_2F: StructLayout = MemoryLayout.structLayout(
	FLOAT.withName("x"),
	FLOAT.withName("y")
)
val D2D1_POINT_2F = D2D_POINT_2F
val D2D1_POINT_2F_x: VarHandle = D2D_POINT_2F.varHandle(groupElement("x"))
val D2D1_POINT_2F_y: VarHandle = D2D_POINT_2F.varHandle(groupElement("y"))
val PD2D1_POINT_2F = `void*`

val D2D1_BRUSH_PROPERTIES: StructLayout = MemoryLayout.structLayout(
	FLOAT.withName("opacity"),
	D2D1_MATRIX_3X2_F.withName("transform")
)
val PD2D1_BRUSH_PROPERTIES = `void*`

val PID2D1Brush = `void*`
val PID2D1StrokeStyle = `void*`
val PID2D1SolidColorBrush = `void*`

val D2D_RECT_F: StructLayout = MemoryLayout.structLayout(
	FLOAT.withName("left"),
	FLOAT.withName("top"),
	FLOAT.withName("right"),
	FLOAT.withName("bottom")
)
val D2D1_RECT_F = D2D_RECT_F
val D2D1_RECT_F_left: VarHandle = D2D1_RECT_F.varHandle(groupElement("left"))
val D2D1_RECT_F_top: VarHandle = D2D1_RECT_F.varHandle(groupElement("top"))
val D2D1_RECT_F_right: VarHandle = D2D1_RECT_F.varHandle(groupElement("right"))
val D2D1_RECT_F_bottom: VarHandle = D2D1_RECT_F.varHandle(groupElement("bottom"))
val PD2D1_RECT_F = `void*`