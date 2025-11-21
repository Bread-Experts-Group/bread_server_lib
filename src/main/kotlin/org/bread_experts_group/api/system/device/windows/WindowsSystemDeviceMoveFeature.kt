package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.api.system.device.feature.SystemDeviceMoveFeature
import org.bread_experts_group.api.system.device.move.MoveSystemDeviceFeatureIdentifier
import org.bread_experts_group.api.system.device.move.WindowsMoveSystemDeviceFeatures
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.nativeMoveFileWithProgressW
import org.bread_experts_group.ffi.windows.throwLastError
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

class WindowsSystemDeviceMoveFeature(private val pathSegment: MemorySegment) : SystemDeviceMoveFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeMoveFileWithProgressW != null

	override fun move(
		destination: SystemDevice,
		vararg features: MoveSystemDeviceFeatureIdentifier
	): Pair<SystemDeviceMoveHandle, List<MoveSystemDeviceFeatureIdentifier>> {
		val supportedFeatures = mutableListOf<MoveSystemDeviceFeatureIdentifier>()
		var flags = 0
		if (features.contains(WindowsMoveSystemDeviceFeatures.OVERWRITE)) {
			flags = flags or 0x1
			supportedFeatures.add(WindowsMoveSystemDeviceFeatures.OVERWRITE)
		}
		if (features.contains(WindowsMoveSystemDeviceFeatures.COPY_ALLOWED)) {
			flags = flags or 0x2
			supportedFeatures.add(WindowsMoveSystemDeviceFeatures.MOVE_ON_RESTART)
		}
		if (features.contains(WindowsMoveSystemDeviceFeatures.MOVE_ON_RESTART)) {
			flags = flags or 0x4
			supportedFeatures.add(WindowsMoveSystemDeviceFeatures.MOVE_ON_RESTART)
		}
		if (features.contains(WindowsMoveSystemDeviceFeatures.WRITE_THROUGH)) {
			flags = flags or 0x8
			supportedFeatures.add(WindowsMoveSystemDeviceFeatures.WRITE_THROUGH)
		}
		if (features.contains(WindowsMoveSystemDeviceFeatures.FAIL_IF_NOT_TRACKABLE)) {
			flags = flags or 0x20
			supportedFeatures.add(WindowsMoveSystemDeviceFeatures.FAIL_IF_NOT_TRACKABLE)
		}
		val arena = Arena.ofConfined()
		val destinationSegment = arena.allocateFrom(
			destination.get(SystemDeviceFeatures.SYSTEM_IDENTIFIER).identity,
			Charsets.UTF_16LE
		)
		val status = nativeMoveFileWithProgressW!!.invokeExact(
			capturedStateSegment,
			pathSegment,
			destinationSegment,
			MemorySegment.NULL, // todo routine
			MemorySegment.NULL, // todo data
			flags
		) as Int
		arena.close()
		if (status == 0) throwLastError()
		return SystemDeviceMoveHandle() to supportedFeatures
	}
}