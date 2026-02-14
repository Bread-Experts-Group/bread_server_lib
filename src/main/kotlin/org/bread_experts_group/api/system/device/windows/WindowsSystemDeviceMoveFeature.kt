@file:Suppress("LongLine")

package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.FeatureProvider
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.api.system.device.feature.SystemDeviceMoveFeature
import org.bread_experts_group.api.system.device.feature.move.SystemDeviceMoveFeatureImplementation
import org.bread_experts_group.api.system.device.feature.move.SystemDeviceMoveHandle
import org.bread_experts_group.api.system.device.feature.move.feature.routine.MoveProgressRoutineFeatureImplementation
import org.bread_experts_group.api.system.device.feature.move.feature.routine.MoveProgressRoutineFeatures
import org.bread_experts_group.api.system.device.feature.move.feature.routine.WindowsMoveProgressRoutineReturns
import org.bread_experts_group.api.system.device.feature.move.feature.routine.feature.MoveProgressRoutineNumericBytesFeature
import org.bread_experts_group.api.system.device.feature.move.feature.routine.feature.MoveProgressRoutineSystemIdentifierFeature
import org.bread_experts_group.api.system.device.feature.move.feature.windows.WindowsSystemDeviceMoveProgressRoutineFeature
import org.bread_experts_group.api.system.device.move.MoveSystemDeviceFeatureIdentifier
import org.bread_experts_group.api.system.device.move.WindowsMoveSystemDeviceFeatures
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.nativeLinker
import org.bread_experts_group.ffi.windows.*
import java.lang.foreign.Arena
import java.lang.foreign.FunctionDescriptor
import java.lang.foreign.MemorySegment
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType

class WindowsSystemDeviceMoveFeature(private val pathSegment: MemorySegment) : SystemDeviceMoveFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeMoveFileWithProgressWide != null

	override fun move(
		destination: SystemDevice,
	): SystemDeviceMoveHandle = object : SystemDeviceMoveHandle() {
		private val rtFeature = WindowsSystemDeviceMoveProgressRoutineFeature()
		override val features: MutableList<SystemDeviceMoveFeatureImplementation<*>> = mutableListOf(rtFeature)

		@Suppress("unused")
		fun progressRoutine(
			totalFileSize: Long,
			totalFileTransferred: Long,
			totalStreamSize: Long,
			totalStreamTransferred: Long,
			streamNumber: Int,
			callbackReason: Int,
			sourceFile: MemorySegment,
			destinationFile: MemorySegment,
			lpData: MemorySegment
		): Int {
			val routine = rtFeature.routine
			return if (routine != null) {
				val features = mutableListOf<MoveProgressRoutineFeatureImplementation<*>>(
					MoveProgressRoutineNumericBytesFeature(
						MoveProgressRoutineFeatures.TOTAL_SIZE_BYTES,
						ImplementationSource.SYSTEM_NATIVE,
						totalFileSize
					),
					MoveProgressRoutineNumericBytesFeature(
						MoveProgressRoutineFeatures.TOTAL_TRANSFERRED_BYTES,
						ImplementationSource.SYSTEM_NATIVE,
						totalFileTransferred
					),
					MoveProgressRoutineNumericBytesFeature(
						MoveProgressRoutineFeatures.FILE_STREAM_TOTAL_SIZE_BYTES,
						ImplementationSource.SYSTEM_NATIVE,
						totalStreamSize
					), MoveProgressRoutineNumericBytesFeature(
						MoveProgressRoutineFeatures.FILE_STREAM_TOTAL_TRANSFERRED_BYTES,
						ImplementationSource.SYSTEM_NATIVE,
						totalStreamTransferred
					),
					MoveProgressRoutineSystemIdentifierFeature(
						MoveProgressRoutineFeatures.FILE_STREAM_SYSTEM_IDENTIFIER,
						ImplementationSource.SYSTEM_NATIVE,
						streamNumber
					),
					MoveProgressRoutineSystemIdentifierFeature(
						MoveProgressRoutineFeatures.CALL_REASON_SYSTEM_IDENTIFIER,
						ImplementationSource.SYSTEM_NATIVE,
						callbackReason
					)
				)
				val actions = routine(
					object : FeatureProvider<MoveProgressRoutineFeatureImplementation<*>> {
						override val logger
							get() = TODO("Not yet implemented")
						override val supportedFeatures: MutableMap<FeatureExpression<
								out MoveProgressRoutineFeatureImplementation<*>>,
								MutableList<MoveProgressRoutineFeatureImplementation<*>>> = mutableMapOf()
						override val features: MutableList<MoveProgressRoutineFeatureImplementation<*>> = features
					}
				)
				when {
					actions.contains(WindowsMoveProgressRoutineReturns.STOP_COPY_AND_DELETE_DESTINATION) -> 1
					actions.contains(WindowsMoveProgressRoutineReturns.STOP_COPY_AND_RETAIN_DESTINATION) -> 2
					actions.contains(WindowsMoveProgressRoutineReturns.STOP_CALLING_ROUTINE) -> 3
					else -> 0
				}
			} else 0
		}

		override fun start(vararg features: MoveSystemDeviceFeatureIdentifier): List<MoveSystemDeviceFeatureIdentifier> {
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
				destination.get(SystemDeviceFeatures.SYSTEM_IDENTIFIER).identity as String,
				winCharsetWide
			)
			val status = nativeMoveFileWithProgressWide!!.invokeExact(
				capturedStateSegment,
				pathSegment,
				destinationSegment,
				nativeLinker.upcallStub(
					MethodHandles.lookup().findSpecial(
						this::class.java, "progressRoutine",
						MethodType.methodType(
							Int::class.java,
							Long::class.java, Long::class.java, Long::class.java, Long::class.java,
							Int::class.java, Int::class.java,
							MemorySegment::class.java, MemorySegment::class.java, MemorySegment::class.java
						), this::class.java
					).bindTo(this),
					FunctionDescriptor.of(
						DWORD,
						LARGE_INTEGER, LARGE_INTEGER, LARGE_INTEGER, LARGE_INTEGER,
						DWORD, DWORD, HANDLE, HANDLE, LPVOID
					),
					arena
				),
				MemorySegment.NULL,
				flags
			) as Int
			arena.close()
			if (status == 0) throwLastError()
			return supportedFeatures
		}
	}
}