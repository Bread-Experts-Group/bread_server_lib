package org.bread_experts_group.api.graphics.feature.console.feature.device.windows

import org.bread_experts_group.api.system.device.windows.WindowsHandleSupplier
import org.bread_experts_group.api.system.io.IODevice
import java.lang.foreign.MemorySegment

class WindowsStandardConsoleIODevice(
	override val handle: MemorySegment
) : IODevice(), WindowsHandleSupplier