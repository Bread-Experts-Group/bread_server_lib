package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.api.system.device.copy.CopySystemDeviceFeatureIdentifier
import org.bread_experts_group.api.system.device.copy.WindowsCopySystemDeviceFeatures
import org.bread_experts_group.api.system.device.feature.SystemDeviceCopyFeature
import org.bread_experts_group.ffi.windows.*
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

class WindowsSystemDeviceCopyFeature(private val pathSegment: MemorySegment) : SystemDeviceCopyFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeCopyFile2 != null

	override fun copy(
		destination: SystemDevice,
		vararg features: CopySystemDeviceFeatureIdentifier
	): Pair<SystemDeviceCopyHandle, List<CopySystemDeviceFeatureIdentifier>> {
		val supportedFeatures = mutableListOf<CopySystemDeviceFeatureIdentifier>()
		val arena = Arena.ofConfined()
		val destinationSegment = arena.allocateFrom(
			destination.get(SystemDeviceFeatures.SYSTEM_IDENTIFIER).identity,
			Charsets.UTF_16LE
		)
		val extraParameters = if (features.isNotEmpty()) {
			val p = arena.allocate(COPYFILE2_EXTENDED_PARAMETERS)
			COPYFILE2_EXTENDED_PARAMETERS_dwSize.set(p, 0L, p.byteSize().toInt())
			var flags = 0
			if (features.contains(WindowsCopySystemDeviceFeatures.FAIL_IF_DESTINATION_EXISTS)) {
				flags = flags or 0x00000001
				supportedFeatures.add(WindowsCopySystemDeviceFeatures.FAIL_IF_DESTINATION_EXISTS)
			}
			if (features.contains(WindowsCopySystemDeviceFeatures.RESTARTABLE)) {
				flags = flags or 0x00000002
				supportedFeatures.add(WindowsCopySystemDeviceFeatures.RESTARTABLE)
			}
			if (features.contains(WindowsCopySystemDeviceFeatures.OPEN_SOURCE_FOR_WRITE)) {
				flags = flags or 0x00000004
				supportedFeatures.add(WindowsCopySystemDeviceFeatures.OPEN_SOURCE_FOR_WRITE)
			}
			if (features.contains(WindowsCopySystemDeviceFeatures.ALLOW_UNENCRYPTED_DESTINATION)) {
				flags = flags or 0x00000008
				supportedFeatures.add(WindowsCopySystemDeviceFeatures.ALLOW_UNENCRYPTED_DESTINATION)
			}
			if (features.contains(WindowsCopySystemDeviceFeatures.DIRECTORY)) {
				flags = flags or 0x00000080
				supportedFeatures.add(WindowsCopySystemDeviceFeatures.DIRECTORY)
			}
			if (features.contains(WindowsCopySystemDeviceFeatures.COPY_SYMBOLIC_LINK)) {
				flags = flags or 0x00000800
				supportedFeatures.add(WindowsCopySystemDeviceFeatures.COPY_SYMBOLIC_LINK)
			}
			if (features.contains(WindowsCopySystemDeviceFeatures.DISABLE_SYSTEM_CACHE)) {
				flags = flags or 0x00001000
				supportedFeatures.add(WindowsCopySystemDeviceFeatures.DISABLE_SYSTEM_CACHE)
			}
			if (features.contains(WindowsCopySystemDeviceFeatures.RESUME_FROM_RESTART)) {
				flags = flags or 0x00004000
				supportedFeatures.add(WindowsCopySystemDeviceFeatures.RESUME_FROM_RESTART)
			}
			if (features.contains(WindowsCopySystemDeviceFeatures.IGNORE_ALT_STREAMS)) {
				flags = flags or 0x00008000
				supportedFeatures.add(WindowsCopySystemDeviceFeatures.IGNORE_ALT_STREAMS)
			}
			if (features.contains(WindowsCopySystemDeviceFeatures.DISABLE_WINDOWS_COPY_OFFLOAD)) {
				flags = flags or 0x00040000
				supportedFeatures.add(WindowsCopySystemDeviceFeatures.DISABLE_WINDOWS_COPY_OFFLOAD)
			}
			if (features.contains(WindowsCopySystemDeviceFeatures.ALWAYS_COPY_REPARSE_POINT)) {
				flags = flags or 0x00200000
				supportedFeatures.add(WindowsCopySystemDeviceFeatures.ALWAYS_COPY_REPARSE_POINT)
			}
			if (features.contains(WindowsCopySystemDeviceFeatures.NO_BLOCK_DESTINATION_ENCRYPT)) {
				flags = flags or 0x00400000
				supportedFeatures.add(WindowsCopySystemDeviceFeatures.NO_BLOCK_DESTINATION_ENCRYPT)
			}
			if (features.contains(WindowsCopySystemDeviceFeatures.IGNORE_SOURCE_ENCRYPTION)) {
				flags = flags or 0x00800000
				supportedFeatures.add(WindowsCopySystemDeviceFeatures.IGNORE_SOURCE_ENCRYPTION)
			}
			if (features.contains(WindowsCopySystemDeviceFeatures.NO_DESTINATION_WRITE_DAC)) {
				flags = flags or 0x02000000
				supportedFeatures.add(WindowsCopySystemDeviceFeatures.NO_DESTINATION_WRITE_DAC)
			}
			if (features.contains(WindowsCopySystemDeviceFeatures.DISABLE_PRE_ALLOCATION)) {
				flags = flags or 0x04000000
				supportedFeatures.add(WindowsCopySystemDeviceFeatures.DISABLE_PRE_ALLOCATION)
			}
			if (features.contains(WindowsCopySystemDeviceFeatures.LOW_SPACE_FREE_MODE)) {
				flags = flags or 0x08000000
				supportedFeatures.add(WindowsCopySystemDeviceFeatures.LOW_SPACE_FREE_MODE)
			}
			if (features.contains(WindowsCopySystemDeviceFeatures.COMPRESS_OVER_LINK)) {
				flags = flags or 0x10000000
				supportedFeatures.add(WindowsCopySystemDeviceFeatures.COMPRESS_OVER_LINK)
			}
			if (features.contains(WindowsCopySystemDeviceFeatures.SPARSENESS)) {
				flags = flags or 0x20000000
				supportedFeatures.add(WindowsCopySystemDeviceFeatures.SPARSENESS)
			}
			COPYFILE2_EXTENDED_PARAMETERS_dwCopyFlags.set(p, 0L, flags)
			p
		} else MemorySegment.NULL
		decodeWin32Error(
			nativeCopyFile2!!.invokeExact(
				pathSegment,
				destinationSegment,
				extraParameters
			) as Int
		)
		arena.close()
		return SystemDeviceCopyHandle() to supportedFeatures
	}
}