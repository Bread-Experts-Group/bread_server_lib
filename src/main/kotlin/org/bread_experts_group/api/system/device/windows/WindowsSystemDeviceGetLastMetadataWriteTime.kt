package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.api.system.device.feature.SystemDeviceGetTimeFeature
import org.bread_experts_group.api.system.io.open.OpenIODeviceFeatureIdentifier
import org.bread_experts_group.ffi.windows.*
import java.lang.foreign.MemorySegment
import java.time.Instant

class WindowsSystemDeviceGetLastMetadataWriteTime(
	private val pathSegment: MemorySegment
) : SystemDeviceGetTimeFeature(
	ImplementationSource.SYSTEM_NATIVE,
	SystemDeviceFeatures.PATH_GET_LAST_METADATA_WRITE_TIME
) {
	override fun supported(): Boolean = nativeCreateFile3 != null && nativeCloseHandle != null &&
			nativeGetFileInformationByHandleEx != null

	override fun getTime(
		vararg features: OpenIODeviceFeatureIdentifier
	): Pair<Instant, List<OpenIODeviceFeatureIdentifier>> {
		val supportedFeatures = mutableListOf<OpenIODeviceFeatureIdentifier>()
		return readFileInfo(pathSegment, features, supportedFeatures) {
			Instant.ofEpochMilli(
				FILETIMEToUnixMs(FILE_BASIC_INFO_ChangeTime.get(it, 0L) as Long)
			)
		} to supportedFeatures
	}
}