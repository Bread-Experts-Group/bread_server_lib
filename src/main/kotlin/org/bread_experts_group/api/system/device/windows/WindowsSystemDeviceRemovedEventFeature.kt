package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.MappedEnumeration
import org.bread_experts_group.api.ImplementationSource
import org.bread_experts_group.api.system.EventListener
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.device.SystemDeviceRemovedEventFeature
import org.bread_experts_group.ffi.windows.cfgmgr.WindowsCMNotifyAction
import org.bread_experts_group.ffi.windows.cfgmgr.WindowsCMNotifyEventData
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

class WindowsSystemDeviceRemovedEventFeature : SystemDeviceRemovedEventFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = true // TODO supported

	private val events = mutableSetOf<(SystemDevice) -> Unit>()
	private var init = false
	override fun listen(with: (SystemDevice) -> Unit): EventListener {
		if (!init) {
			init = true
			WindowsSystemDeviceManager.actions.add { _: MemorySegment,
													 _: MemorySegment,
													 action: MappedEnumeration<UInt, WindowsCMNotifyAction>,
													 eventData: WindowsCMNotifyEventData ->
				if (action.enum == WindowsCMNotifyAction.DEVICE_INTERFACE_REMOVAL) {
					val device = Arena.ofConfined().use { decodeDevice(eventData, it) }
					events.forEach { it(device) }
				}
			}
		}
		if (events.add(with)) WindowsSystemDeviceManager.listen()
		return object : EventListener {
			override fun teardown() {
				if (events.remove(with)) WindowsSystemDeviceManager.unlisten()
			}
		}
	}
}