package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.Mappable.Companion.id
import org.bread_experts_group.MappedEnumeration
import org.bread_experts_group.ffi.GUID
import org.bread_experts_group.ffi.nativeLinker
import org.bread_experts_group.ffi.windows.*
import org.bread_experts_group.ffi.windows.cfgmgr.WindowsCMNotifyAction
import org.bread_experts_group.ffi.windows.cfgmgr.WindowsCMNotifyEventData
import org.bread_experts_group.ffi.windows.cfgmgr.nativeCM_Register_Notification
import org.bread_experts_group.ffi.windows.cfgmgr.nativeCM_Unregister_Notification
import java.lang.foreign.Arena
import java.lang.foreign.FunctionDescriptor
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType

internal object WindowsSystemDeviceManager {
	val actions = mutableListOf<(
		hNotify: MemorySegment,
		context: MemorySegment,
		action: MappedEnumeration<UInt, WindowsCMNotifyAction>,
		eventData: WindowsCMNotifyEventData
	) -> Unit>()
	private var events = 0
	private var eventArena: Arena? = null
	private var eventsNotifier: MemorySegment = MemorySegment.NULL
	private fun startUpEvents() {
		val newArena = Arena.ofConfined()
		val callbackPtr = nativeLinker.upcallStub(
			MethodHandles.lookup().findSpecial(
				this::class.java,
				"execInternal",
				MethodType.methodType(
					Int::class.java,
					MemorySegment::class.java, MemorySegment::class.java, Int::class.java,
					MemorySegment::class.java, Int::class.java
				),
				this::class.java
			).bindTo(this),
			FunctionDescriptor.of(
				ValueLayout.JAVA_INT,
				ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS,
				ValueLayout.JAVA_INT
			),
			newArena
		)
		val filter = newArena.allocate(CM_NOTIFY_FILTER)
		CM_NOTIFY_FILTER_cbSize.set(filter, 0, filter.byteSize().toInt())
		CM_NOTIFY_FILTER_Flags.set(filter, 0, 1)
		CM_NOTIFY_FILTER_FilterType.set(filter, 0, 0)
		val status = nativeCM_Register_Notification!!.invokeExact(
			filter,
			MemorySegment.NULL,
			callbackPtr,
			threadLocalPTR
		) as Int
		if (status != 0) TODO("Ret: $status")
		eventsNotifier = threadLocalPTR.get(ValueLayout.ADDRESS, 0)
		eventArena = newArena
	}

	private fun shutDownEvents() {
		if (eventsNotifier == MemorySegment.NULL) return
		val status = nativeCM_Unregister_Notification!!.invokeExact(eventsNotifier) as Int
		if (status != 0) TODO("Ret: $status")
		eventsNotifier = MemorySegment.NULL
		eventArena?.close()
		eventArena = null
	}

	fun listen() {
		if (events++ == 0) startUpEvents()
	}

	fun unlisten() {
		if (--events == 0) shutDownEvents()
	}

	@Suppress("unused")
	private fun execInternal(
		hNotify: MemorySegment,
		context: MemorySegment,
		action: Int,
		eventData: MemorySegment,
		eventDataSize: Int
	): Int {
		val eventDataSegment = eventData.reinterpret(eventDataSize.toLong())
		val eventDataDecoded = when (val filter = CM_NOTIFY_EVENT_DATA_FilterType.get(eventDataSegment, 0) as Int) {
			0 -> WindowsCMNotifyEventData.DeviceInterface(
				GUID(
					CM_NOTIFY_EVENT_DATA_u_DeviceInterface_ClassGuid.invokeExact(eventDataSegment, 0L) as MemorySegment
				),
				(CM_NOTIFY_EVENT_DATA_u_DeviceInterface_SymbolicLink.invokeExact(eventDataSegment, 0L) as MemorySegment)
					.reinterpret(eventDataSize - ((DWORD.byteSize() * 2) + GUID.byteSize()))
			)

			else -> TODO("Filter #$filter")
		}
		actions.forEach {
			it(
				hNotify, context,
				WindowsCMNotifyAction.entries.id(action.toUInt()),
				eventDataDecoded
			)
		}
		return 0
	}
}