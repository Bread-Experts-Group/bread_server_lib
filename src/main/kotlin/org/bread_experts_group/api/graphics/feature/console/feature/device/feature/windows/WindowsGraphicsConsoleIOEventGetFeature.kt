package org.bread_experts_group.api.graphics.feature.console.feature.device.feature.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.graphics.feature.console.feature.device.feature.GraphicsConsoleIOEvent
import org.bread_experts_group.api.graphics.feature.console.feature.device.feature.GraphicsConsoleIOEventGetFeature
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.*
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.util.*

class WindowsGraphicsConsoleIOEventGetFeature(
	private val handle: MemorySegment
) : GraphicsConsoleIOEventGetFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean {
		if (
			nativeGetNumberOfConsoleInputEvents == null ||
			nativeReadConsoleInputExW == null
		) return false
		try {
			getEventCount()
		} catch (_: WindowsLastErrorException) {
			return false
		}
		return true
	}

	private val arena = Arena.ofShared()
	private val record = arena.allocate(INPUT_RECORD)
	private val modifierMap = mapOf(
		0x0001u to GraphicsConsoleIOEvent.KeyModifiers.RIGHT_ALT,
		0x0002u to GraphicsConsoleIOEvent.KeyModifiers.LEFT_ALT,
		0x0004u to GraphicsConsoleIOEvent.KeyModifiers.RIGHT_CTRL,
		0x0008u to GraphicsConsoleIOEvent.KeyModifiers.LEFT_CTRL,
		0x0010u to GraphicsConsoleIOEvent.KeyModifiers.SHIFT,
		0x0020u to GraphicsConsoleIOEvent.KeyModifiers.NUM_LOCK,
		0x0040u to GraphicsConsoleIOEvent.KeyModifiers.SCROLL_LOCK,
		0x0080u to GraphicsConsoleIOEvent.KeyModifiers.CAPS_LOCK
	)

	fun decodeRecord(
		record: MemorySegment
	): GraphicsConsoleIOEvent = when (INPUT_RECORD_EventType.get(record, 0L) as Int) {
		1 -> {
			val key = INPUT_RECORD_KeyEvent.invokeExact(record, 0L) as MemorySegment
			val modifiersRaw = KEY_EVENT_RECORD_dwControlKeyState.get(key, 0) as Int
			val modifiers = modifierMap.entries.fold(
				EnumSet.noneOf(GraphicsConsoleIOEvent.KeyModifiers::class.java)
			) { a, r -> if (modifiersRaw and r.key.toInt() > 0) a.add(r.value); a }
			GraphicsConsoleIOEvent.Key(
				KEY_EVENT_RECORD_bKeyDown.get(key, 0L) as Int == 1,
				(KEY_EVENT_RECORD_wRepeatCount.get(key, 0L) as Short).toInt(),
				(KEY_EVENT_RECORD_wVirtualKeyCode.get(key, 0L) as Short).toInt(),
				(KEY_EVENT_RECORD_wVirtualScanCode.get(key, 0L) as Short).toInt(),
				Char((KEY_EVENT_RECORD_uChar_UnicodeChar.get(key, 0L) as Short).toUShort()),
				modifiers
			)
		}

		4 -> {
			val bufferSize = INPUT_RECORD_WindowBufferSizeEvent.invokeExact(record, 0L) as MemorySegment
			val coord = WINDOW_BUFFER_SIZE_RECORD_COORD.invokeExact(bufferSize, 0L) as MemorySegment
			GraphicsConsoleIOEvent.WindowSize(
				COORD_X.get(coord, 0L) as Int,
				COORD_Y.get(coord, 0L) as Int,
			)
		}

		else -> GraphicsConsoleIOEvent.OperatingSystemDependent()
	}

	override fun getEvent(): GraphicsConsoleIOEvent? {
		val status = nativeReadConsoleInputExW!!.invokeExact(
			capturedStateSegment,
			handle,
			record,
			1,
			threadLocalDWORD0,
			0x0002.toShort()
		) as Int
		if (status == 0) decodeLastError()
		return if (threadLocalDWORD0.get(DWORD, 0) > 0) decodeRecord(record)
		else null
	}

	override fun pollEvent(): GraphicsConsoleIOEvent {
		val status = nativeReadConsoleInputExW!!.invokeExact(
			capturedStateSegment,
			handle,
			record,
			1,
			threadLocalDWORD0,
			0.toShort()
		) as Int
		if (status == 0) decodeLastError()
		return decodeRecord(record)
	}

	override fun peekEvent(): GraphicsConsoleIOEvent? {
		val status = nativeReadConsoleInputExW!!.invokeExact(
			capturedStateSegment,
			handle,
			record,
			1,
			threadLocalDWORD0,
			0x0003.toShort()
		) as Int
		if (status == 0) decodeLastError()
		return if (threadLocalDWORD0.get(DWORD, 0) > 0) decodeRecord(record)
		else null
	}

	override fun peekNextEvent(): GraphicsConsoleIOEvent {
		val status = nativeReadConsoleInputExW!!.invokeExact(
			capturedStateSegment,
			handle,
			record,
			1,
			threadLocalDWORD0,
			0x0001.toShort()
		) as Int
		if (status == 0) decodeLastError()
		return decodeRecord(record)
	}

	override fun getEvents(
		returnIfNone: Boolean, maxLength: Int
	): List<GraphicsConsoleIOEvent> = Arena.ofConfined().use {
		val events = arena.allocate(INPUT_RECORD, maxLength.toLong())
		val status = nativeReadConsoleInputExW!!.invokeExact(
			capturedStateSegment,
			handle,
			events,
			maxLength,
			threadLocalDWORD0,
			(if (returnIfNone) 0x0002 else 0).toShort() // TODO: document flags
		) as Int
		if (status == 0) decodeLastError()
		val count = threadLocalDWORD0.get(DWORD, 0)
		List(count) {
			decodeRecord(events.asSlice(INPUT_RECORD.byteSize() * it))
		}
	}

	override fun peekEvents(
		returnIfNone: Boolean, maxLength: Int
	): List<GraphicsConsoleIOEvent> = Arena.ofConfined().use {
		val events = arena.allocate(INPUT_RECORD, maxLength.toLong())
		val status = nativeReadConsoleInputExW!!.invokeExact(
			capturedStateSegment,
			handle,
			events,
			maxLength,
			threadLocalDWORD0,
			(if (returnIfNone) 0x0003 else 0x0001).toShort()
		) as Int
		if (status == 0) decodeLastError()
		var i = 0
		val count = threadLocalDWORD0.get(DWORD, 0)
		buildList {
			while (i < count) add(decodeRecord(events.asSlice(INPUT_RECORD.byteSize() * i++)))
		}
	}

	override fun getEventCount(): UInt {
		val status = nativeGetNumberOfConsoleInputEvents!!.invokeExact(
			capturedStateSegment,
			handle,
			threadLocalDWORD0
		) as Int
		if (status == 0) decodeLastError()
		return threadLocalDWORD0.get(DWORD, 0).toUInt()
	}
}