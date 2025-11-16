package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.Flaggable.Companion.raw
import org.bread_experts_group.api.FeatureIdentifier
import org.bread_experts_group.api.ImplementationSource
import org.bread_experts_group.api.system.device.feature.SystemDeviceIODeviceFeature
import org.bread_experts_group.api.system.device.io.IODevice
import org.bread_experts_group.api.system.device.io.windows.WindowsIODevice
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.*
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.util.*

class WindowsSystemDeviceIODeviceFeature(
	symbolicLink: MemorySegment
) : SystemDeviceIODeviceFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = true
	// TODO: IODevices created from things like serial devices may exist when support checking, but not after

	private val localArena = Arena.ofConfined()
	private val symbolicLink = localArena.allocate(symbolicLink.byteSize()).copyFrom(symbolicLink)
	override fun open(vararg features: FeatureIdentifier): Pair<IODevice, List<FeatureIdentifier>> {
		val handle = nativeCreateFile3!!.invokeExact(
			capturedStateSegment,
			symbolicLink,
			EnumSet.of(WindowsGenericAccessRights.GENERIC_READ, WindowsGenericAccessRights.GENERIC_WRITE)
				.raw()
				.toInt(),
			0,
			WindowsCreationDisposition.OPEN_EXISTING.id.toInt(),
			MemorySegment.NULL
		) as MemorySegment
		if (handle == INVALID_HANDLE_VALUE) decodeLastError()
		return WindowsIODevice(handle) to emptyList()
	}
}