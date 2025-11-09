package org.bread_experts_group.project_incubator.console

import org.bread_experts_group.api.coding.CodingFormats
import org.bread_experts_group.api.coding.CodingFormatsProvider
import org.bread_experts_group.api.graphics.GraphicsFeatures
import org.bread_experts_group.api.graphics.GraphicsProvider
import org.bread_experts_group.api.graphics.feature.console.feature.device.GraphicsConsoleFeatures
import org.bread_experts_group.api.graphics.feature.console.feature.device.feature.GraphicsConsoleIOEvent
import org.bread_experts_group.api.graphics.feature.console.feature.device.feature.GraphicsConsoleIOFeatures
import org.bread_experts_group.api.graphics.feature.console.feature.device.feature.GraphicsConsoleModes
import org.bread_experts_group.api.io.feature.device.IODeviceFeatures
import org.bread_experts_group.api.system.EventListener
import org.bread_experts_group.api.system.SystemFeatures
import org.bread_experts_group.api.system.SystemProvider
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.api.system.device.SystemDeviceType
import org.bread_experts_group.api.system.user.SystemUserFeatures
import org.bread_experts_group.protocol.vt100d.*
import java.io.BufferedReader
import java.nio.file.Files
import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import kotlin.io.path.Path
import kotlin.math.max

class Console {
	val clockFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ssZ")
	fun Duration.format(): String {
		val seconds = this.seconds
		val minutes = seconds / 60
		val hours = minutes / 60
		val days = hours / 24
		return String.format("%02d:%02d:%02d:%02d", days, hours % 24, minutes % 60, seconds % 60)
	}

	val user = SystemProvider.get(SystemFeatures.THREAD_LOCAL_USER).user
	val userName = user.get(SystemUserFeatures.NAME_GET).name
	val logonTime = user.get(SystemUserFeatures.LOGON_TIME_GET).logonTime

	val controlSpace = "${SET_ATTRIBUTE(VT100DGraphicsAttributes.FG_MAGENTA)}␠" +
			SET_ATTRIBUTE(VT100DGraphicsAttributes.FG_DEFAULT)
	val controlTab = "${SET_ATTRIBUTE(VT100DGraphicsAttributes.FG_MAGENTA)}␉␣␣␣" +
			SET_ATTRIBUTE(VT100DGraphicsAttributes.FG_DEFAULT)
	val controlCr = "${SET_ATTRIBUTE(VT100DGraphicsAttributes.FG_MAGENTA)}␍" +
			SET_ATTRIBUTE(VT100DGraphicsAttributes.FG_DEFAULT)
	val controlLf = "${SET_ATTRIBUTE(VT100DGraphicsAttributes.FG_MAGENTA)}␊" +
			SET_ATTRIBUTE(VT100DGraphicsAttributes.FG_DEFAULT)
	val controlEtx = "${SET_ATTRIBUTE(VT100DGraphicsAttributes.FG_RED)}␃" +
			SET_ATTRIBUTE(VT100DGraphicsAttributes.FG_DEFAULT)

	val cui = GraphicsProvider.get(GraphicsFeatures.CUI_CONSOLE)
	val cOut = cui.get(GraphicsConsoleFeatures.STANDARD_OUTPUT)
	val cOutDevice = cOut.get(GraphicsConsoleIOFeatures.DEVICE_GET).device
	val cOutWrite = cOutDevice.get(IODeviceFeatures.WRITE)
	val cIn = cui.get(GraphicsConsoleFeatures.STANDARD_INPUT)
	val cInEvents = cIn.get(GraphicsConsoleIOFeatures.EVENT_GET)

	val lines = mutableListOf<String>()
	var storage = ""

	var panelLineStart = 0

	init {
		val reader: BufferedReader =
			Files.newBufferedReader(Path("C:\\Users\\Adenosine3Phosphate\\Desktop\\Projects\\bread_server_lib\\src\\main\\kotlin\\org\\bread_experts_group\\api\\io\\IOFeatureImplementation.kt"))
		while (true) {
			val raw = reader.read()
			if (raw == -1) {
				if (storage.isNotEmpty()) lines.add(storage)
				break
			}
			storage += Char(raw)
			if (raw == '\n'.code) {
				lines.add(storage)
				storage = ""
			}
		}

		cOut.get(GraphicsConsoleIOFeatures.CODING_SET).setCoding(
			CodingFormatsProvider.get(CodingFormats.UTF_8).coding
		)
		cOut.get(GraphicsConsoleIOFeatures.MODE_SET).setMode(
			EnumSet.of(
				GraphicsConsoleModes.OUTPUT_SYSTEM_PROCESSED,
				GraphicsConsoleModes.OUTPUT_CONTROL_SEQUENCES
			)
		)
		cIn.get(GraphicsConsoleIOFeatures.CODING_SET).setCoding(
			CodingFormatsProvider.get(CodingFormats.UTF_8).coding
		)
		cIn.get(GraphicsConsoleIOFeatures.MODE_SET).setMode(
			EnumSet.of(
				GraphicsConsoleModes.INPUT_CONTROL_SEQUENCES,
				GraphicsConsoleModes.INPUT_WINDOW_EVENTS,
				GraphicsConsoleModes.INPUT_MOUSE_EVENTS
			)
		)
		cOutWrite.write(OPEN_ALT_BUFFER, Charsets.UTF_8)
	}

	context(window: ConsoleMessage.WindowSize)
	fun renderPanel(): String {
		var rendered = ""
		var i = panelLineStart
		while ((i + 3) <= window.y) {
			if (i >= lines.size) {
				rendered += controlEtx
				break
			}
			var line = lines[i++]
			line = if (line.length > window.x) "${line.take(window.x - 1)}${
				SET_ATTRIBUTE(
					VT100DGraphicsAttributes.FG_YELLOW
				)
			}>${SET_ATTRIBUTE(VT100DGraphicsAttributes.FG_DEFAULT)}"
			else line.take(window.x)
			rendered += line
				.replace(" ", controlSpace)
				.replace("\t", controlTab)
				.replace("\r", controlCr)
				.replace("\n", controlLf) + '\n'
		}
		return rendered
	}

	val tabs = listOf(
		ContextTab("SHELL", 'S'),
		ContextTab("EXECUTE", 'E'),
		ContextTab(
			"DEVICE", 'D',
			object : ContextTab(
				"SERIAL", 'S'
			) {
				var addEvent: EventListener? = null
				var removeEvent: EventListener? = null
				val mappings = IdentityHashMap<SystemDevice, ContextTab>()
				override fun opened() {
					fun addDevice(device: SystemDevice) {
						if (mappings.contains(device)) return
						val friendlyName = device.get(SystemDeviceFeatures.FRIENDLY_NAME).name
						val serialPortName = device.getOrNull(SystemDeviceFeatures.SERIAL_PORT_NAME)?.name
						val newTab = ContextTab(
							"$friendlyName [${serialPortName ?: "x"}]",
							null
						)
						mappings[device] = newTab
						tabs.add(newTab)
					}

					addEvent = SystemProvider.get(SystemFeatures.DEVICE_ADDED_EVENT).listen {
						if (it.type != SystemDeviceType.SERIAL) return@listen
						addDevice(it)
						renderCallback()
					}

					removeEvent = SystemProvider.get(SystemFeatures.DEVICE_REMOVED_EVENT).listen {
						if (it.type != SystemDeviceType.SERIAL) return@listen
						tabs.remove(mappings.remove(it))
						renderCallback()
					}

					SystemProvider.get(SystemFeatures.ENUMERATE_DEVICES).enumerate(SystemDeviceType.SERIAL).forEach {
						addDevice(it)
					}
				}

				override fun closed() {
					addEvent?.teardown()
					removeEvent?.teardown()
					mappings.clear()
				}
			}
		)
	)

	var openTabs = arrayOf<ContextTab>()

	context(mouse: ConsoleMessage.MouseInput)
	fun processTab(tab: ContextTab, depth: Int) {
		if (
			!mouse.captured && !mouse.down &&
			mouse.button == 0 && mouse.x in tab.x until (tab.x + (tab.w - 1)) && mouse.y == tab.y
		) {
			openTabs = if (openTabs.getOrNull(depth) != tab) {
				val newOpenTabs = openTabs.copyOf(depth + 1)
				newOpenTabs[depth] = tab
				tab.opened()
				tab.populate(tab.tabs)
				@Suppress("UNCHECKED_CAST")
				newOpenTabs as Array<ContextTab>
			} else {
				for (i in depth until openTabs.size) {
					val tab = openTabs[i]
					tab.closed()
					tab.tabs.clear()
				}
				@Suppress("UNCHECKED_CAST")
				openTabs.copyOf(depth) as Array<ContextTab>
			}
			mouse.captured = true
		}
	}

	fun renderTab(tab: ContextTab, depth: Int): String {
		val tabName = tab.name.padEnd(tab.w).let {
			if (tab.selectChar != null) it.replace(
				tab.selectChar.toString(),
				SET_ATTRIBUTE(VT100DGraphicsAttributes.UNDERLINE) +
						tab.selectChar +
						SET_ATTRIBUTE(VT100DGraphicsAttributes.NO_UNDERLINE)
			) else it
		}
		val label = CURSOR_XY_POS(tab.x.toUShort(), tab.y.toUShort()) + tabName
		if (openTabs.getOrNull(depth) != tab) return label
		return SET_ATTRIBUTE(VT100DGraphicsAttributes.INTENSE_BG_BLACK) +
				label +
				SET_ATTRIBUTE(VT100DGraphicsAttributes.BG_WHITE)
	}

	context(window: ConsoleMessage.WindowSize, mouse: ConsoleMessage.MouseInput)
	fun processTopLevel(x: Int, y: Int) {
		var tabsX = x
		tabs.forEach {
			it.x = tabsX
			it.y = y
			it.w = it.name.length
			it.renderCallback = { context(window, mouse) { render() } }
			processTab(it, 0)
			tabsX += it.name.length + 1
		}
		var openTabsI = 0
		while (true) {
			if (!openTabs.indices.contains(openTabsI)) break
			val processTab = openTabs[openTabsI]
			val w = processTab.tabs.maxOfOrNull { it.name.length } ?: 0
			processTab.tabs.forEachIndexed { iS, subTab ->
				subTab.x = processTab.x + (if (openTabsI == 0) 0 else processTab.name.length)
				subTab.y = (if (openTabsI == 0) 2 else processTab.y) + iS
				subTab.w = w
				subTab.renderCallback = { context(window, mouse) { render() } }
				processTab(subTab, openTabsI + 1)
			}
			openTabsI++
		}
	}

	fun renderTopLevel(): String {
		var built = ""
		tabs.forEach {
			built += renderTab(it, 0)
		}
		openTabs.forEachIndexed { i, processTab ->
			processTab.tabs.forEachIndexed { _, subTab ->
				built += renderTab(subTab, i + 1)
			}
		}
		return built + CURSOR_XY_POS(tabs.last().x.toUShort(), tabs.last().y.toUShort())
	}

	context(window: ConsoleMessage.WindowSize, mouse: ConsoleMessage.MouseInput)
	fun render() {
		if (!mouse.wheel) processTopLevel(2, 1)
		else if (!mouse.captured) {
			mouse.captured = true
			panelLineStart = max(if (mouse.button == 0) panelLineStart - 1 else panelLineStart + 1, 0)
		}
		val timeNow = OffsetDateTime.now()
		val uptime = Duration.of(
			SystemProvider.get(SystemFeatures.UPTIME_MS).uptime.toLong(),
			ChronoUnit.MILLIS
		)
		val timeStats = "${clockFormat.format(timeNow)} ${uptime.format()}"
		val logonDuration = Duration.between(logonTime, Instant.now())
		val userStats = "$userName ${logonDuration.format()}"
		val buffer = CURSOR_HIDE +
				SET_ATTRIBUTE(VT100DGraphicsAttributes.DEFAULT) +
				ERASE_SCREEN(VT100DEraseTypes.ALL) +
				// Pseudo-console
				CURSOR_XY_POS(1u, 2u) +
				renderPanel() +
				// Status
				SET_ATTRIBUTE(VT100DGraphicsAttributes.BG_WHITE) +
				SET_ATTRIBUTE(VT100DGraphicsAttributes.FG_BLACK) +
				// Status Top
				CURSOR_XY_POS((window.x - timeStats.length).toUShort(), 1u) +
				ERASE_LINE(VT100DEraseTypes.ALL) +
				timeStats +
				// Status Bottom
				CURSOR_XY_POS(1u, window.y.toUShort()) +
				ERASE_LINE(VT100DEraseTypes.ALL) +
				" ${window.x} x ${window.y}" +
				CURSOR_X_POS((window.x - userStats.length).toUShort()) +
				userStats +
				CURSOR_XY_POS(1u, 1u) +
				renderTopLevel() +
				// Mouse draw
				SET_ATTRIBUTE(
					when (mouse.button) {
						0 -> VT100DGraphicsAttributes.BG_GREEN
						1 -> VT100DGraphicsAttributes.BG_YELLOW
						2 -> VT100DGraphicsAttributes.BG_RED
						else -> VT100DGraphicsAttributes.BG_BLUE
					}
				) +
				CURSOR_XY_POS(mouse.x.toUShort(), mouse.y.toUShort()) +
				ERASE_CHARACTERS(1u)
		cOutWrite.write(buffer, Charsets.UTF_8)
	}
}

// Testing only
@OptIn(ExperimentalStdlibApi::class)
fun main() {
	val console = Console()
	val messageQueue = LinkedBlockingQueue<ConsoleMessage>()
	var commandBuffer = ""

	@Suppress("AssignedValueIsNeverRead")
	fun keyPress(char: Char) {
		if (commandBuffer.isEmpty()) {
			if (char == VT100D_ESC) commandBuffer = "$VT100D_ESC"
		} else if (commandBuffer.length == 1) commandBuffer += char
		else when (commandBuffer[1]) {
			'[' -> if (commandBuffer.length == 2) commandBuffer += char
			else when (commandBuffer[2]) {
				'<' -> if (commandBuffer.last().lowercaseChar() == 'm') {
					val parameters = commandBuffer.substring(3, commandBuffer.length - 1).split(';')
					val cb = parameters.getOrNull(0)?.toIntOrNull() ?: 0
					messageQueue.put(
						ConsoleMessage.MouseInput(
							cb and 0b11,
							parameters.getOrNull(1)?.toIntOrNull() ?: 0,
							parameters.getOrNull(2)?.toIntOrNull() ?: 0,
							(cb and 0b1000000) != 0,
							commandBuffer.last() == 'M',
							false
						)
					)
					commandBuffer = ""
				} else commandBuffer += char

				else -> commandBuffer = ""
			}

			else -> commandBuffer = ""
		}
	}

	Thread.ofPlatform().name("Input").start {
		while (true) when (val event = console.cInEvents.pollEvent()) {
			is GraphicsConsoleIOEvent.Key -> if (event.down) keyPress(event.char)
			is GraphicsConsoleIOEvent.WindowSize -> messageQueue.put(ConsoleMessage.WindowSize(event.x, event.y))
			else -> {}
		}
	}

	Thread.ofPlatform().name("Output").start {
		var windowSize = ConsoleMessage.WindowSize(80, 25)
		var mouseInput = ConsoleMessage.MouseInput(0, 0, 0, wheel = false, down = false, captured = true)
		while (true) {
			when (val message = messageQueue.poll(250, TimeUnit.MILLISECONDS)) {
				is ConsoleMessage.WindowSize -> windowSize = message
				is ConsoleMessage.MouseInput -> mouseInput = message
				null -> {}
			}
			context(windowSize, mouseInput) { console.render() }
		}
	}
}