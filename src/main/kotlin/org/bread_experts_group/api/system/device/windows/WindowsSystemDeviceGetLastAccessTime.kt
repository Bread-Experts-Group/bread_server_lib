package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.api.system.device.feature.SystemDeviceGetTimeFeature
import org.bread_experts_group.api.system.device.metadata.MetadataInstant
import org.bread_experts_group.api.system.device.metadata.MetadataSystemDeviceDataIdentifier
import org.bread_experts_group.api.system.device.metadata.MetadataSystemDeviceFeatureIdentifier
import org.bread_experts_group.ffi.autoArena
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.*
import java.lang.foreign.MemorySegment
import java.time.Instant

class WindowsSystemDeviceGetLastAccessTime(
	private val pathSegment: MemorySegment
) : SystemDeviceGetTimeFeature(
	ImplementationSource.SYSTEM_NATIVE,
	SystemDeviceFeatures.PATH_GET_LAST_ACCESS_TIME
) {
	override fun supported(): Boolean = nativeGetFileAttributesExW != null

	override fun getTime(
		vararg features: MetadataSystemDeviceFeatureIdentifier
	): List<MetadataSystemDeviceDataIdentifier> {
		val data = mutableListOf<MetadataSystemDeviceDataIdentifier>()
		val attributeData = autoArena.allocate(WIN32_FILE_ATTRIBUTE_DATA)
		val status = nativeGetFileAttributesExW!!.invokeExact(
			capturedStateSegment,
			pathSegment,
			WindowsGetFileExInfoLevels.GetFileExInfoStandard.id.toInt(),
			attributeData
		) as Int
		if (status == 0) {
			data.add(getIOStatusForError())
			return data
		}
		data.add(
			MetadataInstant(
				Instant.ofEpochMilli(
					FILETIMEToUnixMs(
						WIN32_FILE_ATTRIBUTE_DATA_ftLastAccessTime.get(attributeData, 0L) as Long
					)
				)
			)
		)
		return data
	}
}