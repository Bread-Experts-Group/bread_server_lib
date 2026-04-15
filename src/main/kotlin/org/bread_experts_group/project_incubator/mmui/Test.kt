package org.bread_experts_group.project_incubator.mmui

import org.bread_experts_group.api.graphics.GraphicsFeatures
import org.bread_experts_group.api.graphics.GraphicsProvider
import org.bread_experts_group.api.graphics.feature.direct2d.Direct2DPoint2Float
import org.bread_experts_group.api.graphics.feature.direct2d.Direct2DRectangleFloat
import org.bread_experts_group.api.graphics.feature.direct2d.brush.GraphicsWindowDirect2DSolidColorBrush
import org.bread_experts_group.api.graphics.feature.direct2d.factory.GraphicsWindowDirect2DFactory
import org.bread_experts_group.api.graphics.feature.direct2d.factory.StandardGraphicsWindowDirect2DFactoryCreationType
import org.bread_experts_group.api.graphics.feature.direct2d.rendertarget.GraphicsWindowDirect2DHwndRenderTarget
import org.bread_experts_group.api.graphics.feature.directwrite.factory.GraphicsWindowDirectWriteFactory
import org.bread_experts_group.api.graphics.feature.directwrite.factory.StandardGraphicsWindowDirectWriteFactoryCreationType
import org.bread_experts_group.api.graphics.feature.window.GraphicsWindow
import org.bread_experts_group.api.graphics.feature.window.feature.GraphicsWindowFeatures
import org.bread_experts_group.api.graphics.feature.window.feature.client_area_coordinates.GraphicsWindowRectangleI
import org.bread_experts_group.api.graphics.feature.window.feature.event_loop.GraphicsWindowEventSubscriber
import org.bread_experts_group.api.graphics.feature.window.feature.event_loop.StandardGraphicsWindowEventLoopEventResults
import org.bread_experts_group.api.graphics.feature.window.feature.event_loop.StandardGraphicsWindowEventLoopEventTypes
import org.bread_experts_group.api.graphics.feature.window.feature.event_loop.keyboard.GraphicsWindowEventLoopKeyboardCharacter
import org.bread_experts_group.api.graphics.feature.window.feature.event_loop.keyboard.GraphicsWindowEventLoopKeyboardVirtualKey
import org.bread_experts_group.api.graphics.feature.window.feature.event_loop.keyboard.up.GraphicsWindowEventLoopKeyboardKeyUpEventParameter
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
import org.bread_experts_group.api.system.SystemFeatures
import org.bread_experts_group.api.system.SystemProvider
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.ffi.windows.direct2d.*
import org.bread_experts_group.ffi.windows.directwrite.DWriteFontStretch
import org.bread_experts_group.ffi.windows.directwrite.DWriteFontStyle
import org.bread_experts_group.ffi.windows.directwrite.DWriteFontWeight
import org.bread_experts_group.generic.numeric.geometry.point.Point2
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.max
import kotlin.math.min

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

	val systemFontCollection = dwFactory.getSystemFontCollection(false)
	val fontFamily = systemFontCollection[systemFontCollection.findFamilyName("Verdana")!!]
	val fontNormal = fontFamily.getFirstMatchingFont(
		DWriteFontWeight.DWRITE_FONT_WEIGHT_NORMAL,
		DWriteFontStretch.DWRITE_FONT_STRETCH_NORMAL,
		DWriteFontStyle.DWRITE_FONT_STYLE_NORMAL
	)
	val fontNormalMetrics = fontNormal.getMetrics()
	val fontFormatNormal = fontNormal.createTextFormat(dwFactory, 15f)
	val fontNormalSpace = ((fontNormalMetrics.ascent + fontNormalMetrics.descent).toFloat() /
			fontNormalMetrics.designUnitsPerEm.toInt()) * fontFormatNormal.getFontSize()
	val commandLabelFont = fontFamily.getFirstMatchingFont(
		DWriteFontWeight.DWRITE_FONT_WEIGHT_BOLD,
		DWriteFontStretch.DWRITE_FONT_STRETCH_NORMAL,
		DWriteFontStyle.DWRITE_FONT_STYLE_NORMAL
	)
	val commandLabelMetrics = commandLabelFont.getMetrics()
	val commandLabelTextFormat = commandLabelFont.createTextFormat(dwFactory, 12.5f)
	val commandLabelSpace = ((commandLabelMetrics.ascent + commandLabelMetrics.descent).toFloat() /
			commandLabelMetrics.designUnitsPerEm.toInt()) * commandLabelTextFormat.getFontSize()

	var renderTarget: GraphicsWindowDirect2DHwndRenderTarget? = null
	var whiteBrush: GraphicsWindowDirect2DSolidColorBrush? = null
	var lightGrayBrush: GraphicsWindowDirect2DSolidColorBrush? = null
	var darkGrayBrush: GraphicsWindowDirect2DSolidColorBrush? = null
	var blackBrush: GraphicsWindowDirect2DSolidColorBrush? = null
	var darkBlueBrush: GraphicsWindowDirect2DSolidColorBrush? = null
	fun createDeviceResources(): Boolean {
		if (renderTarget == null) {
			val coords = window.get(GraphicsWindowFeatures.WINDOW_CLIENT_AREA_COORDINATES).get() as
					GraphicsWindowRectangleI
			renderTarget = d2dFactory.createWindowRenderTarget(
				Point2(
					coords.lowerRight.x - coords.upperLeft.x,
					coords.lowerRight.y - coords.upperLeft.y
				),
				window
			).firstNotNullOf {
				it as? GraphicsWindowDirect2DHwndRenderTarget
			}
			whiteBrush = renderTarget.createSolidColorBrush(D2D1_WHITE)
			lightGrayBrush = renderTarget.createSolidColorBrush(D2D1_LIGHT_GRAY)
			darkGrayBrush = renderTarget.createSolidColorBrush(D2D1_DARK_GRAY)
			blackBrush = renderTarget.createSolidColorBrush(D2D1_BLACK)
			darkBlueBrush = renderTarget.createSolidColorBrush(D2D1_DARK_BLUE)
		}

		return true
	}

	val point0 = Direct2DPoint2Float()
	val point1 = Direct2DPoint2Float()
	val rectangle = Direct2DRectangleFloat()

	val getCWD = SystemProvider.get(SystemFeatures.GET_CURRENT_WORKING_PATH_DEVICE)
	val setCWD = SystemProvider.get(SystemFeatures.SET_CURRENT_WORKING_PATH_DEVICE)

	var mX = 0
	var mY = 0
	var lDown = false
	val keys = ArrayDeque<GraphicsWindowEventLoopKeyboardKeyUpEventParameter>()
	var keyInput = ""

	var statusText = ""
	var cursorPosition = 0
	var lastCursorBlink = System.currentTimeMillis()
	var blinkStatus = true

	val files = mutableListOf<FileEntry>()
	var filesPage = 0
	var filesPageCount = 0
	var filesPerPage = 0

	fun updatePages(height: Float) {
		filesPage = 0
		filesPerPage = ((height - ((fontNormalSpace * 3) + (commandLabelSpace * 2))) / fontNormalSpace).toInt()
		filesPageCount = if (filesPerPage <= 0) 0
		else (files.size / filesPerPage) + (if (files.size % filesPerPage > 0) 1 else 0)
	}

	fun updateFiles() {
		val thisCWD = getCWD.device
		files.clear()
		for (child in thisCWD.get(SystemDeviceFeatures.PATH_CHILDREN)) {
			files.add(
				FileEntry(
					child.get(SystemDeviceFeatures.PATH_ELEMENT_LAST).element,
					child.get(SystemDeviceFeatures.PATH_ELEMENT_SHELL_DISPLAY_NAME).element,
					child.get(SystemDeviceFeatures.PATH_ELEMENT_SHELL_TYPE_DISPLAY_NAME).element
				)
			)
		}
		if (renderTarget != null) updatePages(renderTarget.getSize().h)
	}
	updateFiles()

	fun updateCWD(to: SystemDevice) {
		setCWD.set(to)
		updateFiles()
	}

	fun onResize(w: Int, h: Int) {
		renderTarget?.resize(w, h)
		updatePages(h.toFloat())
	}

	fun onRender(): Int {
		createDeviceResources()
		if (renderTarget == null) return 0
		whiteBrush!!
		lightGrayBrush!!
		darkGrayBrush!!
		blackBrush!!
		darkBlueBrush!!
		val size = renderTarget.getSize()
		renderTarget.beginDraw()
		renderTarget.setTransform(d2d1Matrix3x2FIdentity)
		renderTarget.clear(D2D1_BLACK)

		var preparedInput: String? = null
		val savedCursorPosition = cursorPosition
		while (keys.isNotEmpty()) when (val nextInput = keys.removeFirst()) {
			is GraphicsWindowEventLoopKeyboardCharacter -> when (val nextChar = nextInput.char) {
				'\r' -> {
					preparedInput = keyInput
					keyInput = ""
					cursorPosition = 0
					break
				}

				'\b' if (cursorPosition > 0) -> {
					cursorPosition = max(cursorPosition - 1, 0)
					keyInput = StringBuilder(keyInput).deleteCharAt(cursorPosition).toString()
				}

				else -> keyInput = StringBuilder(keyInput).insert(cursorPosition++, nextChar).toString()
			}

			is GraphicsWindowEventLoopKeyboardVirtualKey -> when (nextInput) {
				GraphicsWindowEventLoopKeyboardVirtualKey.END -> cursorPosition = keyInput.length
				GraphicsWindowEventLoopKeyboardVirtualKey.HOME -> cursorPosition = 0
				GraphicsWindowEventLoopKeyboardVirtualKey.LEFT_ARROW -> cursorPosition = max(cursorPosition - 1, 0)
				GraphicsWindowEventLoopKeyboardVirtualKey.RIGHT_ARROW -> cursorPosition = min(
					cursorPosition + 1, keyInput.length
				)

				GraphicsWindowEventLoopKeyboardVirtualKey.DELETE if (cursorPosition < keyInput.length) -> {
					keyInput = StringBuilder(keyInput).deleteCharAt(cursorPosition).toString()
				}

				else -> {}
			}
		}

		val now = System.currentTimeMillis()
		if (cursorPosition != savedCursorPosition) {
			lastCursorBlink = now
			blinkStatus = true
		}

		val paths = ArrayDeque<SystemDevice>()
		fun fillPaths() {
			var device: SystemDevice? = getCWD.device
			while (device != null) {
				paths.addFirst(device)
				device = device.get(SystemDeviceFeatures.PATH_PARENT).parent
			}
		}

		if (preparedInput != null) {
			val input = ArrayDeque<Pair<Boolean, String>>()
			var token = ""
			var escaping = false
			var lastContinueOnFailState = true
			for (char in preparedInput) {
				if (!escaping) when (char) {
					'\\' -> escaping = true
					';', ',' -> {
						input.add(lastContinueOnFailState to token)
						token = ""
						lastContinueOnFailState = char == ';'
					}

					else -> token += char
				} else {
					token += char
					escaping = false
				}
			}
			input.add(lastContinueOnFailState to token)

			var failed = false
			for (command in input) {
				val (continueChainOnFail, command) = command
				if (!continueChainOnFail && failed) continue
				failed = false
				when (command) {
					"R", "SF", "M", "O",
					"SP", "GP",
					"RS", "RSI", "S", "SI" -> statusText = command

					"D" -> {
						val cwd = getCWD.device.get(SystemDeviceFeatures.PATH_APPEND)
						for (entry in files) {
							if (!entry.selected) continue
							cwd.append(entry.directName).get(SystemDeviceFeatures.PATH_DELETE).delete()
						}
						updateFiles()
					}

					"PN" -> if (++filesPage >= filesPageCount) filesPage = 0
					"PL" -> if (--filesPage < 0) filesPage = filesPageCount - 1

					else -> {
						if (command.isNotEmpty()) when (command[0]) {
							'P' -> {
								val pathIndex = command.substring(1).toIntOrNull()
								if (pathIndex != null) {
									paths.clear()
									fillPaths()
									val navigate = paths.getOrNull(pathIndex)
									if (navigate != null) {
										updateCWD(navigate)
										statusText = ""
										continue
									}
								}
								statusText = "Bad path selector"
								failed = true
							}

							'F' -> {
								val fileIndices = command.substring(1)
								val rangeDelimiter = fileIndices.indexOf('-', 1)
								val index0 = fileIndices.let {
									if (rangeDelimiter == -1) it else it.take(rangeDelimiter)
								}.toIntOrNull()
								val range: IntRange? = if (index0 == null) null
								else if (rangeDelimiter != -1) {
									val index1 = fileIndices.substring(rangeDelimiter + 1).toIntOrNull()
									if (index1 == null) null else {
										if (index1 > index0) index0..index1
										else index1..index0
									}
								} else index0..index0
								if (range != null) {
									range.forEach { index ->
										val absoluteIndex = ((filesPage * filesPerPage) + index).mod(files.size)
										if (files.indices.contains(absoluteIndex)) {
											val entry = files[absoluteIndex]
											entry.selected = !entry.selected
											statusText = if (index + 1 > filesPerPage || index < 0)
												"Selected file entry $absoluteIndex" else ""
										}
									}
									continue
								}
								statusText = "Bad file selector"
								failed = true
							}

							else -> {
								statusText = "Invalid entry"
								failed = true
							}
						}
					}
				}
			}
		}

		rectangle.left = 0f
		rectangle.right = size.w
		rectangle.top = 0f
		// Path
		rectangle.bottom = rectangle.top + (fontNormalSpace + commandLabelSpace)
		if (paths.isEmpty()) fillPaths()
		renderTarget.fillRectangle(rectangle, darkBlueBrush)
		renderTarget.drawRectangle(rectangle, blackBrush)
		var lX = 0f
		point0.y = 0f
		val separatorLayout = dwFactory.createTextLayout(
			"\\", fontFormatNormal,
			Float.MAX_VALUE, fontFormatNormal.getFontSize()
		)
		val (separatorLast, _) = separatorLayout.hitTestTextPosition(1, true)
		var k = 0
		while (paths.isNotEmpty()) {
			val nextDevice = paths.removeFirst()
			val name = nextDevice.get(SystemDeviceFeatures.PATH_ELEMENT_LAST).element
			val layout = dwFactory.createTextLayout(
				name, fontFormatNormal,
				size.w, fontFormatNormal.getFontSize()
			)
			val (last, _) = layout.hitTestTextPosition(name.length, true)
			point0.x = lX
			renderTarget.drawTextLayout(point0, layout, whiteBrush)
			rectangle.top = fontNormalSpace
			rectangle.left = point0.x
			renderTarget.drawText("P${k++}", commandLabelTextFormat, rectangle, whiteBrush)
			rectangle.top = 0f
			rectangle.left = 0f
			lX += last.x
			point0.x = lX
			renderTarget.drawTextLayout(point0, separatorLayout, whiteBrush)
			lX += separatorLast.x
		}
		// Files
		rectangle.top = rectangle.bottom
		var fileIndex = filesPage * filesPerPage
		if (filesPageCount > 0) {
			val localPageCount = min(filesPerPage, files.size - fileIndex)
			var largestSelectorX = 0f
			val selectorLayouts = Array(localPageCount) {
				val layoutText = "F$it"
				val layout = dwFactory.createTextLayout(
					layoutText, commandLabelTextFormat,
					Float.POSITIVE_INFINITY, commandLabelSpace
				)
				val x = layout.hitTestTextPosition(layoutText.length, true).first.x
				if (largestSelectorX < x) largestSelectorX = x
				layout
			}
			var largestNameX = 0f
			val nameLayouts = Array(localPageCount) {
				val layoutText = files[fileIndex + it].shellName
				val layout = dwFactory.createTextLayout(
					layoutText, fontFormatNormal,
					Float.POSITIVE_INFINITY, fontNormalSpace
				)
				val x = layout.hitTestTextPosition(layoutText.length, true).first.x
				if (largestNameX < x) largestNameX = x
				layout
			}
			var localFileIndex = 0
			while (fileIndex < files.size) {
				if (rectangle.top >= (size.h - ((fontNormalSpace * 3) + commandLabelSpace))) break
				val entry = files[fileIndex]
				rectangle.bottom += fontNormalSpace
				val rectangleScheme: GraphicsWindowDirect2DSolidColorBrush
				val textScheme: GraphicsWindowDirect2DSolidColorBrush
				if (entry.selected) {
					rectangleScheme = darkBlueBrush
					textScheme = whiteBrush
				} else {
					rectangleScheme = if (fileIndex % 2 == 0) darkGrayBrush else lightGrayBrush
					textScheme = blackBrush
				}
				renderTarget.fillRectangle(rectangle, rectangleScheme)
				point0.x = rectangle.left
				point0.y = rectangle.top + (fontNormalSpace / 2) - (commandLabelSpace / 2)
				renderTarget.drawTextLayout(point0, selectorLayouts[localFileIndex], textScheme)
				point0.x = largestSelectorX + 4f
				point0.y = rectangle.top
				renderTarget.drawTextLayout(point0, nameLayouts[localFileIndex], textScheme)
				rectangle.left = largestSelectorX + largestNameX + 8f
				renderTarget.drawText(entry.shellType, fontFormatNormal, rectangle, textScheme)
				rectangle.left = 0f
				rectangle.top = rectangle.bottom
				fileIndex++
				localFileIndex++
			}
			point0.x = largestSelectorX + 2f
			point0.y = fontNormalSpace + commandLabelSpace
			point1.x = point0.x
			point1.y = rectangle.bottom
			renderTarget.drawLine(point0, point1, darkBlueBrush)
			point0.x = largestSelectorX + largestNameX + 6f
			point1.x = point0.x
			renderTarget.drawLine(point0, point1, darkBlueBrush)
		}
		// Page Indicator
		rectangle.top = size.h - ((fontNormalSpace * 2) + commandLabelSpace)
		rectangle.bottom = rectangle.top + commandLabelSpace
		renderTarget.fillRectangle(rectangle, darkBlueBrush)
		renderTarget.drawText(
			if (filesPageCount == 0) "Viewport too small"
			else "Page ${filesPage + 1} of $filesPageCount • [P]age [[L]ast, g#, [N]ext]",
			commandLabelTextFormat, rectangle, whiteBrush
		)
		// Status
		rectangle.bottom = size.h
		rectangle.top = rectangle.bottom - fontNormalSpace
		renderTarget.fillRectangle(rectangle, darkBlueBrush)
		renderTarget.drawText(statusText, fontFormatNormal, rectangle, whiteBrush)
		// Command
		rectangle.bottom = rectangle.top
		rectangle.top = rectangle.bottom - fontNormalSpace
		renderTarget.fillRectangle(rectangle, whiteBrush)
		val layout = dwFactory.createTextLayout(
			keyInput, fontFormatNormal,
			rectangle.right - rectangle.left,
			rectangle.bottom - rectangle.top
		)
		point0.x = rectangle.left
		point0.y = rectangle.top
		renderTarget.drawTextLayout(point0, layout, blackBrush)
		val (cursorPoint, _) = layout.hitTestTextPosition(cursorPosition, false)
		if (now - lastCursorBlink < 500) {
			if (blinkStatus) {
				rectangle.left = cursorPoint.x
				rectangle.right = rectangle.left + 1f
				rectangle.top += cursorPoint.y
				rectangle.bottom = rectangle.top + fontNormalSpace
				renderTarget.fillRectangle(rectangle, blackBrush)
			}
		} else {
			lastCursorBlink = now
			blinkStatus = !blinkStatus
		}

		val status = renderTarget.endDraw()
		// if (hr == D2DERR_RECREATE_TARGET)
		// {
		//     status = S_OK;
		//     DiscardDeviceResources();
		// }
		lDown = false
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

	window.get(GraphicsWindowFeatures.WINDOW_EVENT).add(
		GraphicsWindowEventSubscriber(
			StandardGraphicsWindowEventLoopEventTypes.MOUSE_LEFT_2D_UP
		) {
			lDown = true
			val p = it[0] as GraphicsWindowEventLoopMouseMove2D32
			mX = p.x
			mY = p.y
			WindowsGraphicsWindowEventLoopEventResult(0)
		}
	)

	window.get(GraphicsWindowFeatures.WINDOW_EVENT).add(
		GraphicsWindowEventSubscriber(
			StandardGraphicsWindowEventLoopEventTypes.KEYBOARD_KEY_DOWN
		) {
			when (val parameter = it[0]) {
				is GraphicsWindowEventLoopKeyboardCharacter, is GraphicsWindowEventLoopKeyboardVirtualKey -> {
					keys.add(parameter)
					WindowsGraphicsWindowEventLoopEventResult(0)
				}

				else -> StandardGraphicsWindowEventLoopEventResults.PASS
			}
		}
	)

	// TODO: WM_DISPLAYCHANGE InvalidateRect
	// TODO: WM_DESTROY PostQuitMessage
}