package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.api.system.device.feature.SystemDeviceGetTimeFeature
import org.bread_experts_group.api.system.device.metadata.MetadataInstant
import org.bread_experts_group.api.system.device.metadata.MetadataSystemDeviceDataIdentifier
import org.bread_experts_group.api.system.device.metadata.MetadataSystemDeviceFeatureIdentifier
import org.bread_experts_group.api.system.io.open.OpenIODeviceDataIdentifier
import org.bread_experts_group.api.system.io.open.OpenIODeviceFeatureIdentifier
import org.bread_experts_group.ffi.windows.*
import java.lang.foreign.MemorySegment
import java.time.Instant

class WindowsSystemDeviceGetLastAccessTime(
	private val pathSegment: MemorySegment
) : SystemDeviceGetTimeFeature(
	ImplementationSource.SYSTEM_NATIVE,
	SystemDeviceFeatures.PATH_GET_LAST_ACCESS_TIME
) {
	override fun supported(): Boolean = nativeCreateFile3 != null && nativeCloseHandle != null &&
			nativeGetFileInformationByHandleEx != null

	override fun getTime(
		vararg features: MetadataSystemDeviceFeatureIdentifier
	): List<MetadataSystemDeviceDataIdentifier> {
		val data = mutableListOf<MetadataSystemDeviceDataIdentifier>()

		@Suppress("UNCHECKED_CAST", "KotlinConstantConditions")
		val time = readFileInfo(
			pathSegment,
			features as Array<OpenIODeviceFeatureIdentifier>,
			data as MutableList<OpenIODeviceDataIdentifier>
		) {
			Instant.ofEpochMilli(
				FILETIMEToUnixMs(FILE_BASIC_INFO_LastAccessTime.get(it, 0L) as Long)
			)
		}
		data.add(MetadataInstant(time))
		return data
	}
}