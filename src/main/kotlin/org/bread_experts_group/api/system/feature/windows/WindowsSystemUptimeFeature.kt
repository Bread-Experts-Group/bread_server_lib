package org.bread_experts_group.api.system.feature.windows

import org.bread_experts_group.api.ImplementationSource
import org.bread_experts_group.api.system.feature.SystemUptimeFeature
import org.bread_experts_group.ffi.windows.nativeGetTickCount64

class WindowsSystemUptimeFeature : SystemUptimeFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeGetTickCount64 != null
	override val uptime: ULong
		get() = (nativeGetTickCount64!!.invokeExact() as Long).toULong()
}