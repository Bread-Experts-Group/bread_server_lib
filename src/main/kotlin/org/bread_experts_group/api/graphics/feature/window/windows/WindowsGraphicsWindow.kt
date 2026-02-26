package org.bread_experts_group.api.graphics.feature.window.windows

import org.bread_experts_group.api.graphics.feature.window.GraphicsWindow
import org.bread_experts_group.api.graphics.feature.window.feature.display_affinity.windows.WindowsGraphicsWindowDisplayAffinityFeature
import org.bread_experts_group.api.graphics.feature.window.feature.event_loop.*
import org.bread_experts_group.api.graphics.feature.window.feature.event_loop.mouse_move_2d.GraphicsWindowEventLoopMouseMove2DEventParameter
import org.bread_experts_group.api.graphics.feature.window.feature.event_loop.redraw.GraphicsWindowEventLoopMouseMove2D32
import org.bread_experts_group.api.graphics.feature.window.feature.event_loop.redraw.GraphicsWindowEventLoopRedrawEventParameter
import org.bread_experts_group.api.graphics.feature.window.feature.event_loop.resize_2d.GraphicsWindowEventLoopResize2D32
import org.bread_experts_group.api.graphics.feature.window.feature.event_loop.resize_2d.GraphicsWindowEventLoopResize2DEventParameter
import org.bread_experts_group.api.graphics.feature.window.feature.event_loop.windows.WindowsGraphicsWindowEventLoopEventResult
import org.bread_experts_group.api.graphics.feature.window.feature.event_loop.windows.WindowsGraphicsWindowEventLoopFeature
import org.bread_experts_group.api.graphics.feature.window.feature.event_loop.windows.WindowsGraphicsWindowEventLoopSystemEvent
import org.bread_experts_group.api.graphics.feature.window.feature.name.windows.WindowsGraphicsWindowNameFeature
import org.bread_experts_group.api.graphics.feature.window.feature.status.StandardGraphicsWindowStatus
import org.bread_experts_group.api.graphics.feature.window.feature.status.windows.WindowsGraphicsWindowStatusFeature
import org.bread_experts_group.api.graphics.feature.window.icon.Image2D
import org.bread_experts_group.api.graphics.feature.window.icon.ImagePlaneType
import org.bread_experts_group.api.graphics.feature.window.icon.IntImagePlane
import org.bread_experts_group.api.graphics.feature.window.icon.StandardGraphicsIcons
import org.bread_experts_group.api.graphics.feature.window.icon.windows.WindowsIDIGraphicsIcons
import org.bread_experts_group.api.graphics.feature.window.icon.windows.WindowsStockGraphicsIcons
import org.bread_experts_group.api.graphics.feature.window.open.*
import org.bread_experts_group.api.graphics.feature.window.open.windows.WindowsGraphicsWindowOpenFeatures
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.nativeLinker
import org.bread_experts_group.ffi.threadLocalPTR
import org.bread_experts_group.ffi.windows.*
import org.bread_experts_group.generic.FlagSetConvertible.Companion.bitI
import org.bread_experts_group.generic.Mappable.Companion.id
import java.lang.foreign.Arena
import java.lang.foreign.FunctionDescriptor
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.util.concurrent.CountDownLatch

class WindowsGraphicsWindow(
	data: MutableList<GraphicsWindowOpenDataIdentifier>,
	vararg features: GraphicsWindowOpenFeatureIdentifier
) : GraphicsWindow() {
	lateinit var hWnd: MemorySegment
	val events: MutableList<GraphicsWindowEventSubscriber<*, *>> = mutableListOf()

	init {
		// CLASS
		val windowAllocator = Arena.ofShared()
		val iconAllocator = Arena.ofConfined()
		var sysControls = features.contains(WindowsGraphicsWindowOpenFeatures.SYSTEM_CONTROLS)
		val wndClassNameSeg = windowAllocator.allocateFrom(
			"bslClassAtom_${System.currentTimeMillis()}",
			Charsets.UTF_16LE
		)
		val localHandle = nativeGetModuleHandleWide!!.invokeExact(MemorySegment.NULL) as MemorySegment

		Arena.ofConfined().use { classAllocator ->
			val wndClassExW = classAllocator.allocate(WNDCLASSEXW)
			WNDCLASSEXW_cbSize.set(wndClassExW, 0, wndClassExW.byteSize().toInt())
			WNDCLASSEXW_hInstance.set(wndClassExW, 0, localHandle)
			WNDCLASSEXW_lpszClassName.set(wndClassExW, 0, wndClassNameSeg)
			val methodHandles = MethodHandles.lookup()
			val wndProcUpCall = nativeLinker.upcallStub(
				methodHandles.findSpecial(
					this::class.java, "wndProc",
					MethodType.methodType(
						Long::class.java,
						MemorySegment::class.java, Int::class.java, Long::class.java, Long::class.java
					), this::class.java
				).bindTo(this),
				FunctionDescriptor.of(
					ValueLayout.JAVA_LONG,
					ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_LONG, ValueLayout.JAVA_LONG
				),
				windowAllocator
			)
			WNDCLASSEXW_lpfnWndProc.set(wndClassExW, 0, wndProcUpCall)

			var classStyleFlags = 0
			if (features.contains(StandardGraphicsWindowOpenFeatures.SYSTEM_CLOSE_BUTTON)) {
				sysControls = true
				data.add(StandardGraphicsWindowOpenFeatures.SYSTEM_CLOSE_BUTTON)
			} else {
				classStyleFlags = classStyleFlags or WindowsClassStyles.CS_NOCLOSE.bitI
			}
			if (features.contains(WindowsGraphicsWindowOpenFeatures.REDRAW_WIDTH_CHANGE)) {
				data.add(WindowsGraphicsWindowOpenFeatures.REDRAW_WIDTH_CHANGE)
				classStyleFlags = classStyleFlags or WindowsClassStyles.CS_HREDRAW.bitI
			}
			if (features.contains(WindowsGraphicsWindowOpenFeatures.REDRAW_HEIGHT_CHANGE)) {
				data.add(WindowsGraphicsWindowOpenFeatures.REDRAW_HEIGHT_CHANGE)
				classStyleFlags = classStyleFlags or WindowsClassStyles.CS_VREDRAW.bitI
			}
			WNDCLASSEXW_style.set(wndClassExW, 0, classStyleFlags)

			val icon = features.firstNotNullOfOrNull { it as? GraphicsWindowIcon }
			if (icon != null) when (val iconRef = icon.icon) {
				is WindowsIDIGraphicsIcons -> {
					val iconSeg = nativeLoadIconWide!!.invokeExact(
						capturedStateSegment,
						MemorySegment.NULL, MemorySegment.ofAddress(iconRef.id.toLong() and 0xFFFFFFFF)
					) as MemorySegment
					if (iconSeg == MemorySegment.NULL) TODO("!")
					WNDCLASSEXW_hIcon.set(wndClassExW, 0, iconSeg)
					data.add(icon)
				}

				is WindowsStockGraphicsIcons, is StandardGraphicsIcons -> {
					val id = (if (iconRef is StandardGraphicsIcons) when (iconRef) {
						StandardGraphicsIcons.INFORMATION -> WindowsStockGraphicsIcons.SIID_INFO
						StandardGraphicsIcons.WARNING -> WindowsStockGraphicsIcons.SIID_WARNING
						StandardGraphicsIcons.ERROR -> WindowsStockGraphicsIcons.SIID_ERROR
					} else (iconRef as WindowsStockGraphicsIcons)).id
					Arena.ofConfined().use { stockAllocator ->
						val iconInfo = stockAllocator.allocate(SHSTOCKICONINFO)
						SHSTOCKICONINFO_cbSize.set(iconInfo, 0, iconInfo.byteSize().toInt())
						val status = nativeSHGetStockIconInfo!!.invokeExact(
							id,
							0x000000100,
							iconInfo
						) as Int
						tryThrowWin32Error(status)
						data.add(icon)
						val iconSeg = SHSTOCKICONINFO_hIcon.get(iconInfo, 0) as MemorySegment
						WNDCLASSEXW_hIcon.set(
							wndClassExW, 0,
							iconSeg.reinterpret(iconAllocator) {
								val status = nativeDestroyIcon!!.invokeExact(it) as Int
								if (status == 0) throwLastError()
							}
						)
					}
				}

				is Image2D -> Arena.ofConfined().use { image2DAllocator ->
					val hdc = nativeGetDC!!.invokeExact(MemorySegment.NULL) as MemorySegment
					val ddBitmap = Arena.ofConfined().use { bitmapHeaderAllocator ->
						val bitmapInfo = bitmapHeaderAllocator.allocate(BITMAPV5HEADER)
						BITMAPV5HEADER_bV5Size.set(bitmapInfo, 0, bitmapInfo.byteSize().toInt())
						BITMAPV5HEADER_bV5Width.set(bitmapInfo, 0, iconRef.width.toInt())
						BITMAPV5HEADER_bV5Height.set(bitmapInfo, 0, iconRef.height.toInt())
						BITMAPV5HEADER_bV5Planes.set(bitmapInfo, 0, 1.toShort())
						BITMAPV5HEADER_bV5BitCount.set(bitmapInfo, 0, 32.toShort())
						BITMAPV5HEADER_bV5Compression.set(bitmapInfo, 0, BI_BITFIELDS)

						BITMAPV5HEADER_bV5RedMask.set(bitmapInfo, 0, 0x00FF0000)
						BITMAPV5HEADER_bV5GreenMask.set(bitmapInfo, 0, 0x0000FF00)
						BITMAPV5HEADER_bV5BlueMask.set(bitmapInfo, 0, 0x000000FF)
						BITMAPV5HEADER_bV5AlphaMask.set(bitmapInfo, 0, 0xFF000000.toInt())
						(nativeCreateDIBSection!!.invokeExact(
							hdc,
							bitmapInfo,
							0,
							threadLocalPTR,
							MemorySegment.NULL,
							0
						) as MemorySegment).let {
							if (it == MemorySegment.NULL) TODO("err")
							it.reinterpret(image2DAllocator) { i ->
								val status = nativeDeleteObject!!.invokeExact(i) as Int
								if (status == 0) TODO("Err")
							}
						}
					}
					if (ddBitmap == MemorySegment.NULL) throwLastError()
					nativeReleaseDC!!.invokeExact(MemorySegment.NULL, hdc) as Int
					val iconData = threadLocalPTR.get(`void*`, 0).reinterpret(Long.MAX_VALUE)
					var planesOffset = 0
					val planesMax = (iconRef.width * iconRef.height).toInt()
					while (planesOffset < planesMax) {
						var argb = 0
						for (plane in iconRef.planes) when (plane.type) {
							ImagePlaneType.RED_1, ImagePlaneType.GREEN_1, ImagePlaneType.BLUE_1,
							ImagePlaneType.ALPHA_1 -> when (plane) {
								is IntImagePlane -> {
									val sampleChunk = plane.data[planesOffset ushr 5]
									argb = argb or when (plane.type) {
										ImagePlaneType.RED_1 -> 0xFF0000
										ImagePlaneType.GREEN_1 -> 0x00FF00
										ImagePlaneType.BLUE_1 -> 0x0000FF
										ImagePlaneType.ALPHA_1 -> 0xFF000000.toInt()
										else -> throw IllegalStateException()
									} * ((sampleChunk ushr (planesOffset and 0b11111)) and 1)
								}
							}

							ImagePlaneType.RED_2, ImagePlaneType.GREEN_2, ImagePlaneType.BLUE_2,
							ImagePlaneType.ALPHA_2 -> when (plane) {
								is IntImagePlane -> {
									val sampleChunk = plane.data[planesOffset ushr 4]
									argb = argb or when (plane.type) {
										ImagePlaneType.RED_2 -> 0x550000
										ImagePlaneType.GREEN_2 -> 0x005500
										ImagePlaneType.BLUE_2 -> 0x000055
										ImagePlaneType.ALPHA_2 -> 0x55000000
										else -> throw IllegalStateException()
									} * ((sampleChunk ushr ((planesOffset and 0b1111) shl 1)) and 0b11)
								}
							}

							ImagePlaneType.RED_4, ImagePlaneType.GREEN_4, ImagePlaneType.BLUE_4,
							ImagePlaneType.ALPHA_4 -> when (plane) {
								is IntImagePlane -> {
									val sampleChunk = plane.data[planesOffset ushr 3]
									argb = argb or when (plane.type) {
										ImagePlaneType.RED_4 -> 0x110000
										ImagePlaneType.GREEN_4 -> 0x001100
										ImagePlaneType.BLUE_4 -> 0x000011
										ImagePlaneType.ALPHA_4 -> 0x11000000
										else -> throw IllegalStateException()
									} * ((sampleChunk ushr ((planesOffset and 0b111) shl 2)) and 0xF)
								}
							}

							ImagePlaneType.RED_8, ImagePlaneType.GREEN_8, ImagePlaneType.BLUE_8,
							ImagePlaneType.ALPHA_8 -> when (plane) {
								is IntImagePlane -> {
									val sampleChunk = plane.data[planesOffset ushr 2]
									argb = argb or when (plane.type) {
										ImagePlaneType.RED_8 -> 0x010000
										ImagePlaneType.GREEN_8 -> 0x000100
										ImagePlaneType.BLUE_8 -> 0x000001
										ImagePlaneType.ALPHA_8 -> 0x01000000
										else -> throw IllegalStateException()
									} * ((sampleChunk ushr ((planesOffset and 0b11) shl 3)) and 0xFF)
								}
							}
						}
						iconData.set(ValueLayout.JAVA_INT, planesOffset.toLong() shl 2, argb)
						planesOffset++
					}

					val maskBitmap = (nativeCreateBitmap!!.invokeExact(
						iconRef.width.toInt(), iconRef.height.toInt(),
						1, 1, MemorySegment.NULL
					) as MemorySegment).let {
						if (it == MemorySegment.NULL) TODO("err")
						it.reinterpret(image2DAllocator) { i ->
							val status = nativeDeleteObject!!.invokeExact(i) as Int
							if (status == 0) TODO("Err")
						}
					}

					val iconSeg = Arena.ofConfined().use { infoAllocator ->
						val iconInfo = infoAllocator.allocate(ICONINFO)
						ICONINFO_fIcon.set(iconInfo, 0, 1)
						ICONINFO_hbmMask.set(iconInfo, 0, maskBitmap)
						ICONINFO_hbmColor.set(iconInfo, 0, ddBitmap)
						(nativeCreateIconIndirect!!.invokeExact(
							capturedStateSegment, iconInfo
						) as MemorySegment).let {
							if (it == MemorySegment.NULL) throwLastError()
							it.reinterpret(iconAllocator) { i ->
								val status = nativeDestroyIcon!!.invokeExact(i) as Int
								if (status == 0) TODO("Err")
							}
						}
					}
					data.add(icon)
					WNDCLASSEXW_hIcon.set(wndClassExW, 0, iconSeg)
				}
			}

			val classAtom = nativeRegisterClassExWide!!.invokeExact(capturedStateSegment, wndClassExW) as Short
			if (classAtom == 0.toShort()) throwLastError()
		}
		// WINDOW
		val hWndLock = CountDownLatch(1)
		var creationThrowable: Throwable? = null
		val windowName = features.firstNotNullOfOrNull { it as? GraphicsWindowName }
		val windowNameString = if (windowName != null) {
			data.add(windowName)
			windowName.name
		} else "BSL Window"
		var extendedStyleFlags = 0
		if (features.contains(StandardGraphicsWindowOpenFeatures.ALLOW_FILE_DRAG_AND_DROP)) {
			data.add(StandardGraphicsWindowOpenFeatures.ALLOW_FILE_DRAG_AND_DROP)
			extendedStyleFlags = extendedStyleFlags or WindowsWindowExtendedStyles.WS_EX_ACCEPTFILES.bitI
		}
		if (features.contains(StandardGraphicsWindowOpenFeatures.TOP_MOST)) {
			data.add(StandardGraphicsWindowOpenFeatures.TOP_MOST)
			extendedStyleFlags = extendedStyleFlags or WindowsWindowExtendedStyles.WS_EX_TOPMOST.bitI
		}
		if (features.contains(WindowsGraphicsWindowOpenFeatures.FORCE_TASKBAR_APPEARANCE)) {
			data.add(WindowsGraphicsWindowOpenFeatures.FORCE_TASKBAR_APPEARANCE)
			extendedStyleFlags = extendedStyleFlags or WindowsWindowExtendedStyles.WS_EX_APPWINDOW.bitI
		}
		if (features.contains(WindowsGraphicsWindowOpenFeatures.FORCE_TASKBAR_APPEARANCE)) {
			data.add(WindowsGraphicsWindowOpenFeatures.FORCE_TASKBAR_APPEARANCE)
			extendedStyleFlags = extendedStyleFlags or WindowsWindowExtendedStyles.WS_EX_APPWINDOW.bitI
		}
		if (features.contains(WindowsGraphicsWindowOpenFeatures.FOREGROUND_ONLY_BY_PROGRAM)) {
			data.add(WindowsGraphicsWindowOpenFeatures.FOREGROUND_ONLY_BY_PROGRAM)
			extendedStyleFlags = extendedStyleFlags or WindowsWindowExtendedStyles.WS_EX_NOACTIVATE.bitI
		}
		var helpSet = false
		if (features.contains(WindowsGraphicsWindowOpenFeatures.SYSTEM_HELP_BUTTON)) {
			data.add(WindowsGraphicsWindowOpenFeatures.SYSTEM_HELP_BUTTON)
			extendedStyleFlags = extendedStyleFlags or WindowsWindowExtendedStyles.WS_EX_CONTEXTHELP.bitI
			helpSet = true
		}
		var styleFlags = 0
		if (features.contains(StandardGraphicsWindowStatus.SHOWN)) {
			data.add(StandardGraphicsWindowStatus.SHOWN)
			styleFlags = styleFlags or WindowsWindowStyles.WS_VISIBLE.bitI
		}
		if (features.contains(StandardGraphicsWindowStatus.MAXIMIZED)) {
			data.add(StandardGraphicsWindowStatus.MAXIMIZED)
			styleFlags = styleFlags or WindowsWindowStyles.WS_MAXIMIZE.bitI
		}
		if (features.contains(StandardGraphicsWindowStatus.MINIMIZED)) {
			data.add(StandardGraphicsWindowStatus.MINIMIZED)
			styleFlags = styleFlags or WindowsWindowStyles.WS_MINIMIZE.bitI
		}
		if (features.contains(StandardGraphicsWindowOpenFeatures.SIZING_BORDER)) {
			data.add(StandardGraphicsWindowOpenFeatures.SIZING_BORDER)
			styleFlags = styleFlags or WindowsWindowStyles.WS_THICKFRAME.bitI
		}
		if (!helpSet && features.contains(StandardGraphicsWindowOpenFeatures.SYSTEM_MINIMIZE_BUTTON)) {
			sysControls = true
			data.add(StandardGraphicsWindowOpenFeatures.SYSTEM_MINIMIZE_BUTTON)
			styleFlags = styleFlags or WS_MINIMIZEBOX.bitI
		}
		if (!helpSet && features.contains(StandardGraphicsWindowOpenFeatures.SYSTEM_MAXIMIZE_BUTTON)) {
			sysControls = true
			data.add(StandardGraphicsWindowOpenFeatures.SYSTEM_MAXIMIZE_BUTTON)
			styleFlags = styleFlags or WS_MAXIMIZEBOX.bitI
		}
		var sysTitleBar = features.contains(WindowsGraphicsWindowOpenFeatures.SYSTEM_TITLE_BAR)
		if (sysControls) {
			sysTitleBar = true
			data.add(WindowsGraphicsWindowOpenFeatures.SYSTEM_CONTROLS)
			styleFlags = styleFlags or WindowsWindowStyles.WS_SYSMENU.bitI
		}
		if (sysTitleBar) {
			data.add(WindowsGraphicsWindowOpenFeatures.SYSTEM_TITLE_BAR)
			styleFlags = styleFlags or WS_CAPTION.maskI
		}
		Thread.ofPlatform().start {
			val windowNameSeg = windowAllocator.allocateFrom(windowNameString, Charsets.UTF_16LE)
			hWnd = nativeCreateWindowExWide!!.invokeExact(
				capturedStateSegment,
				extendedStyleFlags,
				wndClassNameSeg,
				windowNameSeg,
				styleFlags,

				CW_USEDEFAULT, CW_USEDEFAULT,
				CW_USEDEFAULT, CW_USEDEFAULT,

				MemorySegment.NULL,
				MemorySegment.NULL,
				localHandle,
				MemorySegment.NULL
			) as MemorySegment
			if (hWnd == MemorySegment.NULL) {
				creationThrowable = getWin32Error(win32LastError) ?: IllegalStateException()
				hWndLock.countDown()
				return@start
			}
			hWndLock.countDown()
			val msg = windowAllocator.allocate(MSG)
			while (true) {
				val status = nativeGetMessageWide!!.invokeExact(
					capturedStateSegment,
					msg, MemorySegment.NULL, 0, 0
				) as Int
				when (status) {
					-1 -> TODO("123")
					0 -> break
					else -> {
						nativeTranslateMessage!!.invokeExact(msg) as Int
						nativeDispatchMessageWide!!.invokeExact(msg) as Long
					}
				}
			}
		}
		hWndLock.await()
		if (creationThrowable != null) throw creationThrowable
		this.features.add(WindowsGraphicsWindowNameFeature(this))
		this.features.add(WindowsGraphicsWindowStatusFeature(this.hWnd))
		this.features.add(WindowsGraphicsWindowDisplayAffinityFeature(this.hWnd))
		this.features.add(WindowsGraphicsWindowEventLoopFeature(this.events))
	}

	fun sendMessage(msg: Int, wParam: Long, lParam: Long): Long = nativeSendMessageWide!!.invokeExact(
		this.hWnd,
		msg, wParam, lParam
	) as Long

	fun wndProc(hWnd: MemorySegment, message: Int, wParam: Long, lParam: Long): Long {
		val msgMEnum = WindowsWindowMessages.entries.id(message)
		for (event in this.events) {
			val result = when (event.receptiveTo) {
				StandardGraphicsWindowEventLoopEventTypes.EVERYTHING -> {
					@Suppress("UNCHECKED_CAST")
					(event.lambda as
								(Array<GraphicsWindowEventLoopEventParameter>) ->
					GraphicsWindowEventLoopEventResult)(
						arrayOf(WindowsGraphicsWindowEventLoopSystemEvent(hWnd, msgMEnum, wParam, lParam))
					)
				}

				StandardGraphicsWindowEventLoopEventTypes.RESIZE_2D
					if msgMEnum.enum == WindowsWindowMessages.WM_SIZE -> {
					@Suppress("UNCHECKED_CAST")
					(event.lambda as
								(Array<GraphicsWindowEventLoopResize2DEventParameter>) ->
					GraphicsWindowEventLoopEventResult)(
						arrayOf(
							GraphicsWindowEventLoopResize2D32(
								(lParam and 0xFFFF).toInt(),
								((lParam ushr 16) and 0xFFFF).toInt()
							)
						)
					)
				}

				StandardGraphicsWindowEventLoopEventTypes.MOUSE_MOVE_2D
					if msgMEnum.enum == WindowsWindowMessages.WM_MOUSEMOVE -> {
					@Suppress("UNCHECKED_CAST")
					(event.lambda as
								(Array<GraphicsWindowEventLoopMouseMove2DEventParameter>) ->
					GraphicsWindowEventLoopEventResult)(
						arrayOf(
							GraphicsWindowEventLoopMouseMove2D32(
								(lParam and 0xFFFF).toShort().toInt(),
								((lParam ushr 16) and 0xFFFF).toShort().toInt()
							)
						)
					)
				}

				StandardGraphicsWindowEventLoopEventTypes.REDRAW
					if msgMEnum.enum == WindowsWindowMessages.WM_PAINT -> {
					@Suppress("UNCHECKED_CAST")
					val lambda = (event.lambda as (Array<GraphicsWindowEventLoopRedrawEventParameter>) ->
					GraphicsWindowEventLoopEventResult)
					lambda(emptyArray<GraphicsWindowEventLoopRedrawEventParameter>())
				}

				else -> continue
			}
			when (result) {
				StandardGraphicsWindowEventLoopEventResults.PASS -> continue
				is WindowsGraphicsWindowEventLoopEventResult -> return result.result
			}
		}
		return when (msgMEnum.enum) {
			WindowsWindowMessages.WM_DESTROY -> {
				nativePostQuitMessage!!.invokeExact(0)
				0
			}

			else -> (nativeDefWindowProcWide!!.invokeExact(hWnd, message, wParam, lParam) as Long)
		}
	}
}