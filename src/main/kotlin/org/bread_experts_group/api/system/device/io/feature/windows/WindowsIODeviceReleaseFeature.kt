package org.bread_experts_group.api.system.device.io.feature.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.io.feature.IODeviceReleaseFeature
import java.lang.ref.Cleaner

class WindowsIODeviceReleaseFeature(private val release: Cleaner.Cleanable) : IODeviceReleaseFeature() {
	override fun supported(): Boolean = true
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun close() = release.clean()
}