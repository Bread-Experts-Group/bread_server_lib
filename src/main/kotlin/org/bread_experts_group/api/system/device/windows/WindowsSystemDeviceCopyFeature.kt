@file:Suppress("LongLine")

package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.FeatureProvider
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.api.system.device.copy.CopySystemDeviceFeatureIdentifier
import org.bread_experts_group.api.system.device.copy.WindowsCopySystemDeviceFeatures
import org.bread_experts_group.api.system.device.feature.SystemDeviceCopyFeature
import org.bread_experts_group.api.system.device.feature.copy.SystemDeviceCopyFeatureImplementation
import org.bread_experts_group.api.system.device.feature.copy.SystemDeviceCopyHandle
import org.bread_experts_group.api.system.device.feature.copy.feature.routine.CopyProgressRoutineFeatureImplementation
import org.bread_experts_group.api.system.device.feature.copy.feature.routine.CopyProgressRoutineFeatures
import org.bread_experts_group.api.system.device.feature.copy.feature.routine.WindowsCopyProgressRoutineReturns
import org.bread_experts_group.api.system.device.feature.copy.feature.routine.feature.CopyProgressRoutineNumericBytesFeature
import org.bread_experts_group.api.system.device.feature.copy.feature.routine.feature.CopyProgressRoutineSystemIdentifierFeature
import org.bread_experts_group.api.system.device.feature.copy.feature.windows.WindowsSystemDeviceCopyProgressRoutineFeature
import org.bread_experts_group.ffi.nativeLinker
import org.bread_experts_group.ffi.windows.*
import java.lang.foreign.Arena
import java.lang.foreign.FunctionDescriptor
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.util.logging.Logger

class WindowsSystemDeviceCopyFeature(private val pathSegment: MemorySegment) : SystemDeviceCopyFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeCopyFile2 != null

	override fun copy(
		destination: SystemDevice
	): SystemDeviceCopyHandle = object : SystemDeviceCopyHandle() {
		private val rtFeature = WindowsSystemDeviceCopyProgressRoutineFeature()
		override val features: MutableList<SystemDeviceCopyFeatureImplementation<*>> = mutableListOf(rtFeature)

		@Suppress("unused")
		fun progressRoutine(
			copyMessage: MemorySegment,
			context: MemorySegment
		): Int {
			val routine = rtFeature.routine
			if (routine != null) {
				val features = mutableListOf<CopyProgressRoutineFeatureImplementation<*>>()
				val copyMessageSegment = copyMessage.reinterpret(COPYFILE2_MESSAGE.byteSize())
				when (COPYFILE2_MESSAGE_Type.get(copyMessageSegment, 0L)) {
					1 -> {
						val info = COPYFILE2_MESSAGE_Info.invokeExact(copyMessageSegment, 0L) as MemorySegment
						features.add(
							CopyProgressRoutineSystemIdentifierFeature(
								CopyProgressRoutineFeatures.FILE_STREAM_SYSTEM_IDENTIFIER,
								ImplementationSource.SYSTEM_NATIVE,
								COPYFILE2_MESSAGE_ChunkStarted_dwStreamNumber.get(info, 0L) as Int
							)
						)
						features.add(
							CopyProgressRoutineSystemIdentifierFeature(
								CopyProgressRoutineFeatures.CHUNK_SYSTEM_IDENTIFIER,
								ImplementationSource.SYSTEM_NATIVE,
								COPYFILE2_MESSAGE_ChunkStarted_uliChunkNumber.get(info, 0L) as Long
							)
						)
						features.add(
							CopyProgressRoutineNumericBytesFeature(
								CopyProgressRoutineFeatures.CHUNK_TOTAL_SIZE_BYTES,
								ImplementationSource.SYSTEM_NATIVE,
								COPYFILE2_MESSAGE_ChunkStarted_uliChunkSize.get(info, 0L) as Long
							)
						)
						features.add(
							CopyProgressRoutineNumericBytesFeature(
								CopyProgressRoutineFeatures.FILE_STREAM_TOTAL_SIZE_BYTES,
								ImplementationSource.SYSTEM_NATIVE,
								COPYFILE2_MESSAGE_ChunkStarted_uliStreamSize.get(info, 0L) as Long
							)
						)
						features.add(
							CopyProgressRoutineNumericBytesFeature(
								CopyProgressRoutineFeatures.TOTAL_SIZE_BYTES,
								ImplementationSource.SYSTEM_NATIVE,
								COPYFILE2_MESSAGE_ChunkStarted_uliTotalFileSize.get(info, 0L) as Long
							)
						)
					}

					2 -> {
						val info = COPYFILE2_MESSAGE_Info.invokeExact(copyMessageSegment, 0L) as MemorySegment
						features.add(
							CopyProgressRoutineSystemIdentifierFeature(
								CopyProgressRoutineFeatures.FILE_STREAM_SYSTEM_IDENTIFIER,
								ImplementationSource.SYSTEM_NATIVE,
								COPYFILE2_MESSAGE_ChunkFinished_dwStreamNumber.get(info, 0L) as Int
							)
						)
						features.add(
							CopyProgressRoutineSystemIdentifierFeature(
								CopyProgressRoutineFeatures.CHUNK_SYSTEM_IDENTIFIER,
								ImplementationSource.SYSTEM_NATIVE,
								COPYFILE2_MESSAGE_ChunkFinished_uliChunkNumber.get(info, 0L) as Long
							)
						)
						features.add(
							CopyProgressRoutineNumericBytesFeature(
								CopyProgressRoutineFeatures.CHUNK_TOTAL_SIZE_BYTES,
								ImplementationSource.SYSTEM_NATIVE,
								COPYFILE2_MESSAGE_ChunkFinished_uliChunkSize.get(info, 0L) as Long
							)
						)
						features.add(
							CopyProgressRoutineNumericBytesFeature(
								CopyProgressRoutineFeatures.FILE_STREAM_TOTAL_SIZE_BYTES,
								ImplementationSource.SYSTEM_NATIVE,
								COPYFILE2_MESSAGE_ChunkFinished_uliStreamSize.get(info, 0L) as Long
							)
						)
						features.add(
							CopyProgressRoutineNumericBytesFeature(
								CopyProgressRoutineFeatures.FILE_STREAM_TOTAL_TRANSFERRED_BYTES,
								ImplementationSource.SYSTEM_NATIVE,
								COPYFILE2_MESSAGE_ChunkFinished_uliStreamBytesTransferred.get(info, 0L) as Long
							)
						)
						features.add(
							CopyProgressRoutineNumericBytesFeature(
								CopyProgressRoutineFeatures.TOTAL_SIZE_BYTES,
								ImplementationSource.SYSTEM_NATIVE,
								COPYFILE2_MESSAGE_ChunkFinished_uliTotalFileSize.get(info, 0L) as Long
							)
						)
						features.add(
							CopyProgressRoutineNumericBytesFeature(
								CopyProgressRoutineFeatures.TOTAL_TRANSFERRED_BYTES,
								ImplementationSource.SYSTEM_NATIVE,
								COPYFILE2_MESSAGE_ChunkFinished_uliTotalBytesTransferred.get(info, 0L) as Long
							)
						)
					}

					3 -> {
						val info = COPYFILE2_MESSAGE_Info.invokeExact(copyMessageSegment, 0L) as MemorySegment
						features.add(
							CopyProgressRoutineSystemIdentifierFeature(
								CopyProgressRoutineFeatures.FILE_STREAM_SYSTEM_IDENTIFIER,
								ImplementationSource.SYSTEM_NATIVE,
								COPYFILE2_MESSAGE_StreamStarted_dwStreamNumber.get(info, 0L) as Int
							)
						)
						features.add(
							CopyProgressRoutineNumericBytesFeature(
								CopyProgressRoutineFeatures.TOTAL_SIZE_BYTES,
								ImplementationSource.SYSTEM_NATIVE,
								COPYFILE2_MESSAGE_StreamStarted_uliTotalFileSize.get(info, 0L) as Long
							)
						)
						features.add(
							CopyProgressRoutineNumericBytesFeature(
								CopyProgressRoutineFeatures.FILE_STREAM_TOTAL_SIZE_BYTES,
								ImplementationSource.SYSTEM_NATIVE,
								COPYFILE2_MESSAGE_StreamStarted_uliStreamSize.get(info, 0L) as Long
							)
						)
					}

					4 -> {
						val info = COPYFILE2_MESSAGE_Info.invokeExact(copyMessageSegment, 0L) as MemorySegment
						features.add(
							CopyProgressRoutineSystemIdentifierFeature(
								CopyProgressRoutineFeatures.FILE_STREAM_SYSTEM_IDENTIFIER,
								ImplementationSource.SYSTEM_NATIVE,
								COPYFILE2_MESSAGE_StreamFinished_dwStreamNumber.get(info, 0L) as Int
							)
						)
						features.add(
							CopyProgressRoutineNumericBytesFeature(
								CopyProgressRoutineFeatures.FILE_STREAM_TOTAL_SIZE_BYTES,
								ImplementationSource.SYSTEM_NATIVE,
								COPYFILE2_MESSAGE_StreamFinished_uliStreamSize.get(info, 0L) as Long
							)
						)
						features.add(
							CopyProgressRoutineNumericBytesFeature(
								CopyProgressRoutineFeatures.FILE_STREAM_TOTAL_TRANSFERRED_BYTES,
								ImplementationSource.SYSTEM_NATIVE,
								COPYFILE2_MESSAGE_StreamFinished_uliStreamBytesTransferred.get(info, 0L) as Long
							)
						)
						features.add(
							CopyProgressRoutineNumericBytesFeature(
								CopyProgressRoutineFeatures.TOTAL_SIZE_BYTES,
								ImplementationSource.SYSTEM_NATIVE,
								COPYFILE2_MESSAGE_StreamFinished_uliTotalFileSize.get(info, 0L) as Long
							)
						)
						features.add(
							CopyProgressRoutineNumericBytesFeature(
								CopyProgressRoutineFeatures.TOTAL_TRANSFERRED_BYTES,
								ImplementationSource.SYSTEM_NATIVE,
								COPYFILE2_MESSAGE_StreamFinished_uliTotalBytesTransferred.get(info, 0L) as Long
							)
						)
					}

					6 -> {
						val info = COPYFILE2_MESSAGE_Info.invokeExact(copyMessageSegment, 0L) as MemorySegment
						features.add(
							CopyProgressRoutineSystemIdentifierFeature(
								CopyProgressRoutineFeatures.COPY_PHASE_SYSTEM_IDENTIFIER,
								ImplementationSource.SYSTEM_NATIVE,
								COPYFILE2_MESSAGE_Error_CopyPhase.get(info, 0L) as Int
							)
						)
						features.add(
							CopyProgressRoutineSystemIdentifierFeature(
								CopyProgressRoutineFeatures.FILE_STREAM_SYSTEM_IDENTIFIER,
								ImplementationSource.SYSTEM_NATIVE,
								COPYFILE2_MESSAGE_Error_dwStreamNumber.get(info, 0L) as Int
							)
						)
						features.add(
							CopyProgressRoutineSystemIdentifierFeature(
								CopyProgressRoutineFeatures.ERROR_SYSTEM_IDENTIFIER,
								ImplementationSource.SYSTEM_NATIVE,
								COPYFILE2_MESSAGE_Error_hrFailure.get(info, 0L) as Int
							)
						)
						features.add(
							CopyProgressRoutineSystemIdentifierFeature(
								CopyProgressRoutineFeatures.CHUNK_SYSTEM_IDENTIFIER,
								ImplementationSource.SYSTEM_NATIVE,
								COPYFILE2_MESSAGE_Error_uliChunkNumber.get(info, 0L) as Long
							)
						)
						features.add(
							CopyProgressRoutineNumericBytesFeature(
								CopyProgressRoutineFeatures.FILE_STREAM_TOTAL_SIZE_BYTES,
								ImplementationSource.SYSTEM_NATIVE,
								COPYFILE2_MESSAGE_Error_uliStreamSize.get(info, 0L) as Long
							)
						)
						features.add(
							CopyProgressRoutineNumericBytesFeature(
								CopyProgressRoutineFeatures.FILE_STREAM_TOTAL_TRANSFERRED_BYTES,
								ImplementationSource.SYSTEM_NATIVE,
								COPYFILE2_MESSAGE_Error_uliStreamBytesTransferred.get(info, 0L) as Long
							)
						)
						features.add(
							CopyProgressRoutineNumericBytesFeature(
								CopyProgressRoutineFeatures.TOTAL_SIZE_BYTES,
								ImplementationSource.SYSTEM_NATIVE,
								COPYFILE2_MESSAGE_Error_uliTotalFileSize.get(info, 0L) as Long
							)
						)
						features.add(
							CopyProgressRoutineNumericBytesFeature(
								CopyProgressRoutineFeatures.TOTAL_TRANSFERRED_BYTES,
								ImplementationSource.SYSTEM_NATIVE,
								COPYFILE2_MESSAGE_Error_uliTotalBytesTransferred.get(info, 0L) as Long
							)
						)
					}

					else -> {}
				}
				val actions = routine.invoke(
					object : FeatureProvider<CopyProgressRoutineFeatureImplementation<*>> {
						override val logger: Logger
							get() = TODO("Not yet implemented")
						override val supportedFeatures: MutableMap<FeatureExpression<
								out CopyProgressRoutineFeatureImplementation<*>>,
								MutableList<CopyProgressRoutineFeatureImplementation<*>>> = mutableMapOf()
						override val features: MutableList<CopyProgressRoutineFeatureImplementation<*>> = features
					}
				)
				return when {
					actions.contains(WindowsCopyProgressRoutineReturns.STOP_COPY_AND_DELETE_DESTINATION) -> 1
					actions.contains(WindowsCopyProgressRoutineReturns.STOP_COPY_AND_RETAIN_DESTINATION) -> 2
					actions.contains(WindowsCopyProgressRoutineReturns.STOP_CALLING_ROUTINE) -> 3
					else -> 0
				}
			} else return 0
		}

		override fun start(
			vararg features: CopySystemDeviceFeatureIdentifier
		): List<CopySystemDeviceFeatureIdentifier> {
			val supportedFeatures = mutableListOf<CopySystemDeviceFeatureIdentifier>()
			val arena = Arena.ofConfined()
			val destinationSegment = arena.allocateFrom(
				destination.get(SystemDeviceFeatures.SYSTEM_IDENTIFIER).identity,
				Charsets.UTF_16LE
			)
			val extraParameters = if (features.isNotEmpty()) {
				val p = arena.allocate(COPYFILE2_EXTENDED_PARAMETERS_V2)
				COPYFILE2_EXTENDED_PARAMETERS_V2_dwSize.set(p, 0L, p.byteSize().toInt())
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
				COPYFILE2_EXTENDED_PARAMETERS_V2_dwCopyFlags.set(p, 0L, flags)
				flags = 0
				if (features.contains(WindowsCopySystemDeviceFeatures.DISABLE_COPY_JUNCTIONS)) {
					flags = flags or 0x00000001
					supportedFeatures.add(WindowsCopySystemDeviceFeatures.DISABLE_COPY_JUNCTIONS)
				}
				COPYFILE2_EXTENDED_PARAMETERS_V2_dwCopyFlagsV2.set(p, 0L, flags)
				COPYFILE2_EXTENDED_PARAMETERS_V2_pProgressRoutine.set(
					p, 0L,
					nativeLinker.upcallStub(
						MethodHandles.lookup().findSpecial(
							this::class.java, "progressRoutine",
							MethodType.methodType(
								Int::class.java,
								MemorySegment::class.java, MemorySegment::class.java
							), this::class.java
						).bindTo(this),
						FunctionDescriptor.of(
							ValueLayout.JAVA_INT,
							ValueLayout.ADDRESS, ValueLayout.ADDRESS
						),
						arena
					)
				)
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
			return supportedFeatures
		}
	}
}