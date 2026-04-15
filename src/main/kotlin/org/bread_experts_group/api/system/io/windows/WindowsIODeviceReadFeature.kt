package org.bread_experts_group.api.system.io.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.windows.WindowsHandleSupplier
import org.bread_experts_group.api.system.io.feature.IODeviceReadFeature
import org.bread_experts_group.api.system.io.receive.IOReceiveDataIdentifier
import org.bread_experts_group.api.system.io.receive.IOReceiveFeatureIdentifier
import org.bread_experts_group.api.system.io.receive.ReceiveSizeData
import org.bread_experts_group.api.system.io.rxtx.WindowsIOBackupFeatures
import org.bread_experts_group.api.system.socket.DeferredOperation
import org.bread_experts_group.ffi.autoArena
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.*
import java.lang.foreign.MemorySegment
import java.lang.ref.Cleaner

class WindowsIODeviceReadFeature internal constructor(
	private val device: WindowsHandleSupplier
) : IODeviceReadFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE

	private var backupSupported = false
	private var readSupported = false
	override fun supported(): Boolean {
		if (nativeGetFileInformationByHandleEx == null) {
			backupSupported = nativeBackupRead != null
			readSupported = nativeReadFile != null
		} else {
			val data = autoArena.allocate(FILE_STANDARD_INFO)
			val status = nativeGetFileInformationByHandleEx.invokeExact(
				capturedStateSegment,
				device.handle,
				WindowsFileInfoByHandleClasses.FileStandardInfo.id.toInt(),
				data,
				data.byteSize().toInt()
			) as Int
			if (status == 0) throwLastError()
			if ((FILE_STANDARD_INFO_Directory.get(data, 0L) as Byte).toInt() != 0) {
				backupSupported = nativeBackupRead != null
				readSupported = false
			} else {
				backupSupported = nativeBackupRead != null
				readSupported = nativeReadFile != null
			}
		}
		return backupSupported || readSupported
	}

	private companion object {
		val cleaner: Cleaner = Cleaner.create()

		class SegmentHolder(
			val device: WindowsHandleSupplier
		) : Runnable {
			val handle = autoArena.allocate(`void*`)
			override fun run() {
				val status = nativeBackupRead!!.invokeExact(
					capturedStateSegment,
					device.handle,
					MemorySegment.NULL,
					0,
					MemorySegment.NULL,
					1,
					0,
					handle
				) as Int
				if (status == 0) throwLastError()
			}
		}
	}

	private var holder: SegmentHolder = SegmentHolder(device)
	val handle: MemorySegment
		get() = holder.handle

	init {
		cleaner.register(this, this.holder)
	}

	// TODO: Asynchronous I/O
	override fun gatherSegments(
		data: Collection<MemorySegment>,
		vararg features: IOReceiveFeatureIdentifier
	): DeferredOperation<IOReceiveDataIdentifier> {
		val backup = features.contains(WindowsIOBackupFeatures.BACKUP)
		val acl = features.contains(WindowsIOBackupFeatures.ACL)
		if (backup) {
			if (!backupSupported) return DeferredOperation.Immediate(listOf())
			var read = 0L
			data.forEach { segment ->
				threadLocalDWORD0.set(DWORD, 0, 0)
				val status = nativeBackupRead!!.invokeExact(
					capturedStateSegment,
					device.handle,
					segment,
					segment.byteSize().coerceAtMost(Int.MAX_VALUE.toLong()).toInt(),
					threadLocalDWORD0,
					0,
					if (acl) 1 else 0,
					this.handle
				) as Int
				if (status == 0) throwLastError()
				val delta = threadLocalDWORD0.get(DWORD, 0)
				read += delta
				if (delta == 0) {
					this.holder = SegmentHolder(device)
					cleaner.register(this, this.holder)
				}
			}

			return DeferredOperation.Immediate(listOf(ReceiveSizeData(read)))
		} else {
			if (!readSupported) return DeferredOperation.Immediate(listOf())
			var read = 0L
			data.forEach { segment ->
				threadLocalDWORD0.set(DWORD, 0, 0)
				val status = nativeReadFile!!.invokeExact(
					capturedStateSegment,
					device.handle,
					segment,
					segment.byteSize().coerceAtMost(Int.MAX_VALUE.toLong()).toInt(),
					threadLocalDWORD0,
					MemorySegment.NULL
				) as Int
				if (status == 0) throwLastError()
				read += threadLocalDWORD0.get(DWORD, 0)
			}

			return DeferredOperation.Immediate(listOf(ReceiveSizeData(read)))
		}
	}
}