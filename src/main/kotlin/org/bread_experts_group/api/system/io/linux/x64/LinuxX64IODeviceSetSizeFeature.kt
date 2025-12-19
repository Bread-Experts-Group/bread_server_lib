package org.bread_experts_group.api.system.io.linux.x64

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.io.feature.IODeviceSeekFeature
import org.bread_experts_group.api.system.io.feature.IODeviceSetSizeFeature
import org.bread_experts_group.api.system.io.seek.StandardSeekIODeviceFeatures
import org.bread_experts_group.api.system.io.size.SetSizeIODeviceDataIdentifier
import org.bread_experts_group.api.system.io.size.SetSizeIODeviceFeatureIdentifier
import org.bread_experts_group.api.system.io.size.StandardSetSizeFeatures
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.posix.linux.x64.nativeFTruncate
import org.bread_experts_group.ffi.posix.x64.throwLastErrno

class LinuxX64IODeviceSetSizeFeature(
	private val fd: Int,
	private val seek: IODeviceSeekFeature
) : IODeviceSetSizeFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeFTruncate != null

	override fun set(vararg features: SetSizeIODeviceFeatureIdentifier): List<SetSizeIODeviceDataIdentifier> {
		if (features.contains(StandardSetSizeFeatures.CURRENT_POSITION)) {
			val status = nativeFTruncate!!.invokeExact(
				capturedStateSegment,
				fd,
				seek.seek(0, StandardSeekIODeviceFeatures.CURRENT).first
			) as Int
			if (status == -1) throwLastErrno()
			return listOf(StandardSetSizeFeatures.CURRENT_POSITION)
		}
		return emptyList()
	}
}