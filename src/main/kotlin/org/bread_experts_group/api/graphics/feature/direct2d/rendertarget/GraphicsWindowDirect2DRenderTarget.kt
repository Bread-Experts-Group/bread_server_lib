package org.bread_experts_group.api.graphics.feature.direct2d.rendertarget

import org.bread_experts_group.api.graphics.feature.direct2d.DeviceIndependent2DSize
import org.bread_experts_group.api.graphics.feature.direct2d.Direct2DPoint2Float
import org.bread_experts_group.api.graphics.feature.direct2d.Direct2DRectangleFloat
import org.bread_experts_group.api.graphics.feature.direct2d.brush.GraphicsWindowDirect2DSolidColorBrush
import org.bread_experts_group.api.graphics.feature.directwrite.textformat.DirectWriteTextFormat
import org.bread_experts_group.ffi.*
import org.bread_experts_group.ffi.windows.*
import org.bread_experts_group.ffi.windows.direct2d.*
import org.bread_experts_group.ffi.windows.directwrite.DWRITE_MEASURING_MODE
import org.bread_experts_group.ffi.windows.directwrite.DWriteMeasuringMode
import org.bread_experts_group.ffi.windows.directwrite.PIDWriteTextFormat
import org.bread_experts_group.generic.FlagSet
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.invoke.MethodHandle

open class GraphicsWindowDirect2DRenderTarget(
	handle: MemorySegment
) : GraphicsWindowDirect2DResource(
	handle
) {
	var createSolidColorBrush: (MemorySegment, MemorySegment, MemorySegment) -> Int = { clr, prop, o ->
		val nativeCreateSolidColorBrush: MethodHandle = getLocalVTblAddress(
			GraphicsWindowDirect2DRenderTarget::class.java, 4
		).getDowncall(
			nativeLinker,
			HRESULT,
			`void*`.withName("this"),
			PD2D1_COLOR_F.withName("color"),
			PD2D1_BRUSH_PROPERTIES.withName("brushProperties"),
			PID2D1SolidColorBrush.withName("solidColorBrush")
		)
		createSolidColorBrush = { clr, prop, o ->
			nativeCreateSolidColorBrush.invokeExact(ptr, clr, prop, o) as Int
		}
		createSolidColorBrush(clr, prop, o)
	}

	private var drawLine: (MemorySegment, MemorySegment, MemorySegment, Float, MemorySegment) -> Unit =
		{ p0, p1, b, sW, sS ->
			// TODO: Passed-by-value parameters might incur dangerous effects on other platforms
			val nativeDrawLine: MethodHandle = getLocalVTblAddress(
				GraphicsWindowDirect2DRenderTarget::class.java, 11
			).getDowncallVoid(
				nativeLinker,
				`void*`.withName("this"),
				UINT64.withName("point0"),
				UINT64.withName("point1"),
				PID2D1Brush.withName("brush"),
				FLOAT.withName("strokeWidth"),
				PID2D1StrokeStyle.withName("strokeStyle")
			)
			drawLine = { p0, p1, b, sW, sS ->
				var pl = (D2D1_POINT_2F_x.get(p0, 0) as Float).toRawBits().toLong() and 0xFFFFFFFF
				pl = pl or ((D2D1_POINT_2F_y.get(p0, 0) as Float).toRawBits().toLong() shl 32)
				val p0 = pl
				pl = ((D2D1_POINT_2F_x.get(p1, 0) as Float).toRawBits().toLong() and 0xFFFFFFFF)
				pl = pl or ((D2D1_POINT_2F_y.get(p1, 0) as Float).toRawBits().toLong() shl 32)
				val p1 = pl
				nativeDrawLine.invokeExact(ptr, p0, p1, b, sW, sS)
			}
			drawLine(p0, p1, b, sW, sS)
		}

	private var drawRectangle: (MemorySegment, MemorySegment, Float, MemorySegment) -> Unit = { r, b, sW, sS ->
		val nativeDrawRectangle: MethodHandle = getLocalVTblAddress(
			GraphicsWindowDirect2DRenderTarget::class.java, 12
		).getDowncallVoid(
			nativeLinker,
			`void*`.withName("this"),
			PD2D1_RECT_F.withName("rect"),
			PID2D1Brush.withName("brush"),
			FLOAT.withName("strokeWidth"),
			PID2D1StrokeStyle.withName("strokeStyle")
		)
		drawRectangle = { r, b, sW, sS ->
			nativeDrawRectangle.invokeExact(ptr, r, b, sW, sS)
		}
		nativeDrawRectangle.invokeExact(ptr, r, b, sW, sS)
	}

	private var fillRectangle: (MemorySegment, MemorySegment) -> Unit = { r, b ->
		val nativeFillRectangle: MethodHandle = getLocalVTblAddress(
			GraphicsWindowDirect2DRenderTarget::class.java, 13
		).getDowncallVoid(
			nativeLinker,
			`void*`.withName("this"),
			PD2D1_RECT_F.withName("rect"),
			PID2D1Brush.withName("brush")
		)
		fillRectangle = { r, b ->
			nativeFillRectangle.invokeExact(ptr, r, b)
		}
		nativeFillRectangle.invokeExact(ptr, r, b)
	}

	private var drawText: (MemorySegment, Int, MemorySegment, MemorySegment, MemorySegment, Int, Int) -> Unit =
		{ s, sL, tF, lR, dFB, o, mM ->
			val nativeFillRectangle: MethodHandle = getLocalVTblAddress(
				GraphicsWindowDirect2DRenderTarget::class.java, 23
			).getDowncallVoid(
				nativeLinker,
				`void*`.withName("this"),
				PWCHAR.withName("string"),
				UINT32.withName("stringLength"),
				PIDWriteTextFormat.withName("textFormat"),
				PD2D1_RECT_F.withName("layoutRect"),
				PID2D1Brush.withName("defaultFillBrush"),
				D2D1_DRAW_TEXT_OPTIONS.withName("options"),
				DWRITE_MEASURING_MODE.withName("measuringMode")
			)
			drawText = { s, sL, tF, lR, dFB, o, mM ->
				nativeFillRectangle.invokeExact(ptr, s, sL, tF, lR, dFB, o, mM)
			}
			nativeFillRectangle.invokeExact(ptr, s, sL, tF, lR, dFB, o, mM)
		}

	var setTransform: (MemorySegment) -> Unit = {
		val nativeSetTransform: MethodHandle = getLocalVTblAddress(
			GraphicsWindowDirect2DRenderTarget::class.java, 26
		).getDowncallVoid(
			nativeLinker,
			`void*`.withName("this"),
			PD2D1_MATRIX_3X2_F.withName("transform")
		)
		setTransform = {
			nativeSetTransform.invokeExact(ptr, it)
		}
		nativeSetTransform.invokeExact(ptr, it)
	}

	var clear: (MemorySegment) -> Unit = {
		val nativeBeginDraw: MethodHandle = getLocalVTblAddress(
			GraphicsWindowDirect2DRenderTarget::class.java, 43
		).getDowncallVoid(
			nativeLinker,
			`void*`.withName("this"),
			PD2D1_COLOR_F.withName("clearColor")
		)
		clear = {
			nativeBeginDraw.invokeExact(ptr, it)
		}
		nativeBeginDraw.invokeExact(ptr, it)
	}

	var beginDraw: () -> Unit = {
		val nativeBeginDraw: MethodHandle = getLocalVTblAddress(
			GraphicsWindowDirect2DRenderTarget::class.java, 44
		).getDowncallVoid(
			nativeLinker,
			`void*`.withName("this")
		)
		beginDraw = {
			nativeBeginDraw.invokeExact(ptr)
		}
		nativeBeginDraw.invokeExact(ptr)
	}

	private var endDraw: (MemorySegment, MemorySegment) -> Int = { t1, t2 ->
		val nativeEndDraw: MethodHandle = getLocalVTblAddress(
			GraphicsWindowDirect2DRenderTarget::class.java, 45
		).getDowncall(
			nativeLinker,
			HRESULT,
			`void*`.withName("this"),
			PD2D1_TAG.withName("tag1"),
			PD2D1_TAG.withName("tag2")
		)
		endDraw = { t1, t2 ->
			nativeEndDraw.invokeExact(ptr, t1, t2) as Int
		}
		nativeEndDraw.invokeExact(ptr, t1, t2) as Int
	}

	var getSize: () -> DeviceIndependent2DSize = {
		val nativeGetSize: MethodHandle = getLocalVTblAddress(
			GraphicsWindowDirect2DRenderTarget::class.java, 49
		).getDowncallVoid(
			nativeLinker,
			`void*`.withName("this"),
			PD2D1_SIZE_F.withName("return value")
		)
		val sizeContainer = autoArena.allocate(D2D1_SIZE_F)
		val readSize = {
			nativeGetSize.invokeExact(ptr, sizeContainer)
			DeviceIndependent2DSize(
				D2D1_SIZE_F_width.get(sizeContainer, 0) as Float,
				D2D1_SIZE_F_height.get(sizeContainer, 0) as Float
			)
		}
		getSize = readSize
		readSize()
	}

	val t1V: MemorySegment = globalArena.allocate(D2D1_TAG)
	val t2V: MemorySegment = globalArena.allocate(D2D1_TAG)
	fun endDraw() = this.endDraw(t1V, t2V)

	fun createSolidColorBrush(
		color: MemorySegment,
		properties: MemorySegment = MemorySegment.NULL
	): GraphicsWindowDirect2DSolidColorBrush {
		createSolidColorBrush(
			color,
			properties,
			threadLocalPTR
		)
		return GraphicsWindowDirect2DSolidColorBrush(threadLocalPTR.get(`void*`, 0))
	}

	fun drawLine(
		p0: Direct2DPoint2Float,
		p1: Direct2DPoint2Float,
		brush: GraphicsWindowDirect2DSolidColorBrush,
		strokeWidth: Float = 1f,
		strokeStyle: MemorySegment = MemorySegment.NULL
	) {
		drawLine(
			p0.ptr,
			p1.ptr,
			brush.ptr,
			strokeWidth,
			strokeStyle
		)
	}

	fun fillRectangle(
		rect: Direct2DRectangleFloat,
		brush: GraphicsWindowDirect2DSolidColorBrush
	) {
		fillRectangle(
			rect.ptr,
			brush.ptr
		)
	}

	fun drawRectangle(
		rect: Direct2DRectangleFloat,
		brush: GraphicsWindowDirect2DSolidColorBrush,
		strokeWidth: Float = 1f,
		strokeStyle: MemorySegment = MemorySegment.NULL
	) {
		drawRectangle(
			rect.ptr,
			brush.ptr,
			strokeWidth,
			strokeStyle
		)
	}

	fun drawText(
		text: String,
		format: DirectWriteTextFormat,
		rect: Direct2DRectangleFloat,
		brush: GraphicsWindowDirect2DSolidColorBrush,
		options: FlagSet<D2D1DrawTextOptions> = D2D1_DRAW_TEXT_OPTIONS_NONE,
		measuringMode: DWriteMeasuringMode = DWriteMeasuringMode.DWRITE_MEASURING_MODE_NATURAL
	) = Arena.ofConfined().use { tA ->
		drawText(
			tA.allocateFrom(text, Charsets.UTF_16LE),
			text.length,
			format.ptr,
			rect.ptr,
			brush.ptr,
			options.maskI,
			measuringMode.id
		)
	}
}