package org.bread_experts_group.api.system.io.linux.x64

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.io.feature.IODeviceSeekFeature
import org.bread_experts_group.api.system.io.seek.SeekIODeviceFeatureIdentifier
import org.bread_experts_group.api.system.io.seek.StandardSeekIODeviceFeatures
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.posix.linux.x64.SEEK_CUR
import org.bread_experts_group.ffi.posix.linux.x64.SEEK_END
import org.bread_experts_group.ffi.posix.linux.x64.SEEK_SET
import org.bread_experts_group.ffi.posix.linux.x64.nativeLSeek
import org.bread_experts_group.ffi.posix.x64.throwLastErrno

class LinuxIODeviceSeekFeature(private val fd: Int) : IODeviceSeekFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeLSeek != null

	override fun seek(
		n: Long,
		vararg features: SeekIODeviceFeatureIdentifier
	): Pair<Long, List<SeekIODeviceFeatureIdentifier>> {
		val supportedFeatures = mutableListOf<SeekIODeviceFeatureIdentifier>()
		val position = nativeLSeek!!.invokeExact(
			capturedStateSegment,
			fd,
			n,
			when {
				features.contains(StandardSeekIODeviceFeatures.BEGIN) -> {
					supportedFeatures.add(StandardSeekIODeviceFeatures.BEGIN)
					SEEK_SET
				}

				features.contains(StandardSeekIODeviceFeatures.END) -> {
					supportedFeatures.add(StandardSeekIODeviceFeatures.END)
					SEEK_END
				}

				else -> {
					supportedFeatures.add(StandardSeekIODeviceFeatures.CURRENT)
					SEEK_CUR
				}
			}
		) as Long
		if (position == -1L) throwLastErrno()
		return position to supportedFeatures
	}
}