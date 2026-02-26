package org.bread_experts_group.project_incubator.mmui

import org.bread_experts_group.api.graphics.GraphicsFeatures
import org.bread_experts_group.api.graphics.GraphicsProvider
import org.bread_experts_group.api.graphics.feature.direct2d.Direct2DRectangleFloat
import org.bread_experts_group.api.graphics.feature.direct2d.brush.GraphicsWindowDirect2DSolidColorBrush
import org.bread_experts_group.api.graphics.feature.direct2d.factory.GraphicsWindowDirect2DFactory
import org.bread_experts_group.api.graphics.feature.direct2d.factory.StandardGraphicsWindowDirect2DFactoryCreationType
import org.bread_experts_group.api.graphics.feature.direct2d.rendertarget.GraphicsWindowDirect2DHwndRenderTarget
import org.bread_experts_group.api.graphics.feature.directwrite.factory.GraphicsWindowDirectWriteFactory
import org.bread_experts_group.api.graphics.feature.directwrite.factory.StandardGraphicsWindowDirectWriteFactoryCreationType
import org.bread_experts_group.api.graphics.feature.directwrite.textformat.DirectWriteNonCollectionTextFormatName
import org.bread_experts_group.api.graphics.feature.window.GraphicsWindow
import org.bread_experts_group.api.graphics.feature.window.feature.GraphicsWindowFeatures
import org.bread_experts_group.api.graphics.feature.window.feature.event_loop.GraphicsWindowEventSubscriber
import org.bread_experts_group.api.graphics.feature.window.feature.event_loop.StandardGraphicsWindowEventLoopEventResults
import org.bread_experts_group.api.graphics.feature.window.feature.event_loop.StandardGraphicsWindowEventLoopEventTypes
import org.bread_experts_group.api.graphics.feature.window.feature.event_loop.redraw.GraphicsWindowEventLoopMouseMove2D32
import org.bread_experts_group.api.graphics.feature.window.feature.event_loop.resize_2d.GraphicsWindowEventLoopResize2D32
import org.bread_experts_group.api.graphics.feature.window.feature.event_loop.windows.WindowsGraphicsWindowEventLoopEventResult
import org.bread_experts_group.api.graphics.feature.window.feature.status.StandardGraphicsWindowStatus
import org.bread_experts_group.api.graphics.feature.window.icon.Image2D
import org.bread_experts_group.api.graphics.feature.window.icon.ImagePlaneType
import org.bread_experts_group.api.graphics.feature.window.icon.IntImagePlane
import org.bread_experts_group.api.graphics.feature.window.open.GraphicsWindowIcon
import org.bread_experts_group.api.graphics.feature.window.open.StandardGraphicsWindowOpenFeatures
import org.bread_experts_group.api.graphics.feature.window.open.windows.WindowsGraphicsWindowOpenFeatures
import org.bread_experts_group.ffi.windows.direct2d.D2D1_BLACK
import org.bread_experts_group.ffi.windows.direct2d.D2D1_WHITE
import org.bread_experts_group.ffi.windows.direct2d.d2d1Matrix3x2FIdentity
import org.bread_experts_group.ffi.windows.directwrite.DWriteFontStretch
import org.bread_experts_group.ffi.windows.directwrite.DWriteFontStyle
import org.bread_experts_group.ffi.windows.directwrite.DWriteFontWeight
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.roundToLong
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun main() {
	val windowing = GraphicsProvider.get(GraphicsFeatures.GUI_WINDOW)
	val img = ImageIO.read(
		File("C:\\Users\\Adenosine3Phosphate\\Downloads\\photo_2025-08-11_17-27-07.jpg")
	)
	val w = img.width
	val h = img.height
	val r = IntArray(((w * h) / 4) + 1)
	val g = IntArray(((w * h) / 4) + 1)
	val b = IntArray(((w * h) / 4) + 1)
	val a = IntArray(((w * h) / 4) + 1)
	var y = h
	var p = 0
	while (--y > 0) {
		var x = 0
		while (x < w) {
			val argb = img.getRGB(x++, y)
			a[p ushr 2] = a[p ushr 2] or (((argb ushr 24) and 0xFF) shl ((p and 0b11) shl 3))
			r[p ushr 2] = r[p ushr 2] or (((argb ushr 16) and 0xFF) shl ((p and 0b11) shl 3))
			g[p ushr 2] = g[p ushr 2] or (((argb ushr 8) and 0xFF) shl ((p and 0b11) shl 3))
			b[p ushr 2] = b[p ushr 2] or ((argb and 0xFF) shl ((p and 0b11) shl 3))
			p++
		}
	}
	val window = windowing.open(
		StandardGraphicsWindowStatus.SHOWN,
		StandardGraphicsWindowOpenFeatures.SIZING_BORDER,
		StandardGraphicsWindowOpenFeatures.SYSTEM_MAXIMIZE_BUTTON,
		StandardGraphicsWindowOpenFeatures.SYSTEM_MINIMIZE_BUTTON,
		StandardGraphicsWindowOpenFeatures.SYSTEM_CLOSE_BUTTON,
		WindowsGraphicsWindowOpenFeatures.REDRAW_HEIGHT_CHANGE,
		WindowsGraphicsWindowOpenFeatures.REDRAW_WIDTH_CHANGE,
		GraphicsWindowIcon(
			Image2D(
				arrayOf(
					IntImagePlane(ImagePlaneType.RED_8, r),
					IntImagePlane(ImagePlaneType.GREEN_8, g),
					IntImagePlane(ImagePlaneType.BLUE_8, b),
					IntImagePlane(ImagePlaneType.ALPHA_8, a)
				),
				w.toUInt(), h.toUInt()
			)
		)
	).firstNotNullOf { it as? GraphicsWindow }
	window.get(GraphicsWindowFeatures.WINDOW_STATUS).set(
		StandardGraphicsWindowStatus.MINIMIZED
	)

	val d2d = GraphicsProvider.get(GraphicsFeatures.DIRECT2D)
	val d2dFactory = d2d.factory(StandardGraphicsWindowDirect2DFactoryCreationType.SINGLE_THREADED)
		.firstNotNullOf { it as? GraphicsWindowDirect2DFactory }

	val dw = GraphicsProvider.get(GraphicsFeatures.DIRECTWRITE)
	val dwFactory = dw.factory(StandardGraphicsWindowDirectWriteFactoryCreationType.SHARED)
		.firstNotNullOf { it as? GraphicsWindowDirectWriteFactory }

	val verdana = dwFactory.createTextFormat(
		DirectWriteNonCollectionTextFormatName("Segoe UI Emoji"),
		DWriteFontWeight.DWRITE_FONT_WEIGHT_NORMAL,
		DWriteFontStyle.DWRITE_FONT_STYLE_NORMAL,
		DWriteFontStretch.DWRITE_FONT_STRETCH_NORMAL,
		12.5f
	)

	var renderTarget: GraphicsWindowDirect2DHwndRenderTarget? = null
	var whiteBrush: GraphicsWindowDirect2DSolidColorBrush? = null
	fun createDeviceResources(): Boolean {
		if (renderTarget == null) {
			//        RECT rc;
			//        GetClientRect(m_hwnd, &rc);
			//
			//        D2D1_SIZE_U size = D2D1::SizeU(
			//            rc.right - rc.left,
			//            rc.bottom - rc.top
			//            );
			//
			//        // Create a Direct2D render target.
			// TODO use size
			renderTarget = d2dFactory.createWindowRenderTarget(window).firstNotNullOf {
				it as? GraphicsWindowDirect2DHwndRenderTarget
			}
			whiteBrush = renderTarget.createSolidColorBrush(D2D1_WHITE)
		}

		return true
	}

	fun discardDeviceResources() {
		//     SafeRelease(&m_pRenderTarget);
		//    SafeRelease(&m_pLightSlateGrayBrush);
		//    SafeRelease(&m_pCornflowerBlueBrush);
	}

	fun onResize(w: Int, h: Int) {
		renderTarget?.resize(w, h)
	}

	val rectangle = Direct2DRectangleFloat()
	var mX = 0
	var mY = 0

	var lastNano = System.nanoTime()
	val lastTimes = Array(60) { 0L }
	var timeIndex = 0

	fun onRender(): Int {
		createDeviceResources()
		if (renderTarget == null) return 0
		renderTarget.beginDraw()
		val size = renderTarget.getSize()
		renderTarget.setTransform(d2d1Matrix3x2FIdentity)
		renderTarget.clear(D2D1_BLACK)

		rectangle.left = mX.toFloat()
		rectangle.right = rectangle.left + 5f
		rectangle.top = mY.toFloat()
		rectangle.bottom = rectangle.top + 5f
		renderTarget.fillRectangle(rectangle, whiteBrush!!)

		rectangle.left = 0f
		rectangle.top = 0f
		rectangle.right = size.w
		rectangle.bottom = size.h
		val thisNano = System.nanoTime()
		val frameTime = thisNano - lastNano
		lastNano = thisNano
		timeIndex = (timeIndex + 1) % lastTimes.size
		lastTimes[timeIndex] = frameTime
		val averageTime = lastTimes.average().roundToLong()
		var frameText = "Frame Time (avg/${lastTimes.size}): ${averageTime.toDuration(DurationUnit.NANOSECONDS)}"
		frameText += "\nFrames/s: ${1_000_000_000 / averageTime}"
		renderTarget.drawText(frameText, verdana, rectangle, whiteBrush!!)
		val status = renderTarget.endDraw()
		// if (hr == D2DERR_RECREATE_TARGET)
		// {
		//     status = S_OK;
		//     DiscardDeviceResources();
		// }
		return status
	}

	window.get(GraphicsWindowFeatures.WINDOW_EVENT).add(
		GraphicsWindowEventSubscriber(
			StandardGraphicsWindowEventLoopEventTypes.RESIZE_2D
		) {
			val s = it[0] as GraphicsWindowEventLoopResize2D32
			onResize(s.w, s.h)
			StandardGraphicsWindowEventLoopEventResults.PASS
		}
	)

	window.get(GraphicsWindowFeatures.WINDOW_EVENT).add(
		GraphicsWindowEventSubscriber(
			StandardGraphicsWindowEventLoopEventTypes.REDRAW
		) {
//			val hwnd = (window as WindowsGraphicsWindow).hWnd
			onRender()
//			nativeValidateRect!!.invokeExact(hwnd, MemorySegment.NULL) as Int
			WindowsGraphicsWindowEventLoopEventResult(0)
		}
	)

	window.get(GraphicsWindowFeatures.WINDOW_EVENT).add(
		GraphicsWindowEventSubscriber(
			StandardGraphicsWindowEventLoopEventTypes.MOUSE_MOVE_2D
		) {
			val p = it[0] as GraphicsWindowEventLoopMouseMove2D32
			mX = p.x
			mY = p.y
			WindowsGraphicsWindowEventLoopEventResult(0)
		}
	)
	// TODO: WM_DISPLAYCHANGE InvalidateRect
	// TODO: WM_DESTROY PostQuitMessage
}