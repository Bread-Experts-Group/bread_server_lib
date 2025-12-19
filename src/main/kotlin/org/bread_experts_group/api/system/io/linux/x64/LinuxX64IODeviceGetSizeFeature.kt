package org.bread_experts_group.api.system.io.linux.x64

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.io.feature.IODeviceGetSizeFeature
import org.bread_experts_group.api.system.io.size.DataSize
import org.bread_experts_group.api.system.io.size.GetSizeIODeviceDataIdentifier
import org.bread_experts_group.api.system.io.size.GetSizeIODeviceFeatureIdentifier
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.posix.linux.x64.nativeFStat
import org.bread_experts_group.ffi.posix.linux.x64.stat
import org.bread_experts_group.ffi.posix.linux.x64.stat_st_size
import org.bread_experts_group.ffi.posix.x64.throwLastErrno
import java.lang.foreign.Arena

class LinuxX64IODeviceGetSizeFeature(
	private val fd: Int
) : IODeviceGetSizeFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeFStat != null

	override fun get(
		vararg features: GetSizeIODeviceFeatureIdentifier
	): List<GetSizeIODeviceDataIdentifier> = Arena.ofConfined().use { tempArena ->
		val statStruct = tempArena.allocate(stat)
		val status = nativeFStat!!.invokeExact(
			capturedStateSegment,
			fd,
			statStruct
		) as Int
		if (status == -1) throwLastErrno()
		return listOf(DataSize(stat_st_size.get(statStruct, 0L) as Long))
	}
}