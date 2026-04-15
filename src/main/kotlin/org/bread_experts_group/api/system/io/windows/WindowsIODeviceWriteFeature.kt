package org.bread_experts_group.api.system.io.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.windows.WindowsHandleSupplier
import org.bread_experts_group.api.system.device.windows.getIOStatusForError
import org.bread_experts_group.api.system.io.feature.IODeviceWriteFeature
import org.bread_experts_group.api.system.io.rxtx.WindowsIOBackupFeatures
import org.bread_experts_group.api.system.io.send.IOSendDataIdentifier
import org.bread_experts_group.api.system.io.send.IOSendFeatureIdentifier
import org.bread_experts_group.api.system.io.send.SendSizeData
import org.bread_experts_group.api.system.socket.DeferredOperation
import org.bread_experts_group.ffi.autoArena
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.*
import java.lang.foreign.MemorySegment
import java.lang.ref.Cleaner

class WindowsIODeviceWriteFeature internal constructor(
	private val device: WindowsHandleSupplier
) : IODeviceWriteFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE

	private var backupSupported = false
	private var writeSupported = false
	override fun supported(): Boolean {
		if (nativeGetFileInformationByHandleEx == null) {
			backupSupported = nativeBackupWrite != null
			writeSupported = nativeWriteFile != null
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
				backupSupported = nativeBackupWrite != null
				writeSupported = false
			} else {
				backupSupported = nativeBackupWrite != null
				writeSupported = nativeWriteFile != null
			}
		}
		return backupSupported || writeSupported
	}

	private companion object {
		val cleaner: Cleaner = Cleaner.create()

		class SegmentHolder(
			val device: WindowsHandleSupplier
		) : Runnable {
			val handle = autoArena.allocate(`void*`)
			private var clean = false
			override fun run() {
				if (clean) return
				clean = true
				val status = nativeBackupWrite!!.invokeExact(
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
	private var cleanable: Cleaner.Cleanable = cleaner.register(this, this.holder)
	val handle: MemorySegment
		get() = holder.handle

	fun refreshHolder() {
		this.cleanable.clean()
		this.holder = SegmentHolder(device)
		this.cleanable = cleaner.register(this, this.holder)
	}

	// TODO: Asynchronous I/O
	override fun scatterSegments(
		data: Collection<MemorySegment>,
		vararg features: IOSendFeatureIdentifier
	): DeferredOperation<IOSendDataIdentifier> {
		val backup = features.contains(WindowsIOBackupFeatures.BACKUP)
		val acl = features.contains(WindowsIOBackupFeatures.ACL)
		if (backup) {
			if (!backupSupported) return DeferredOperation.Immediate(listOf())
			var size = 0L
			data.forEach { segment ->
				threadLocalDWORD0.set(DWORD, 0, 0)
				val status = nativeBackupWrite!!.invokeExact(
					capturedStateSegment,
					device.handle,
					segment,
					segment.byteSize().coerceAtMost(Int.MAX_VALUE.toLong()).toInt(),
					threadLocalDWORD0,
					0,
					if (acl) 1 else 0,
					this.handle
				) as Int
				if (status == 0) {
					refreshHolder()
					return DeferredOperation.Immediate(listOf(getIOStatusForError()))
				}
				val delta = threadLocalDWORD0.get(DWORD, 0)
				size += delta
				if (delta == 0) refreshHolder()
			}

			return DeferredOperation.Immediate(listOf(SendSizeData(size)))
		} else {
			if (!writeSupported) return DeferredOperation.Immediate(listOf())
			var size = 0L
			data.forEach { segment ->
				threadLocalDWORD0.set(DWORD, 0, 0)
				val status = nativeWriteFile!!.invokeExact(
					capturedStateSegment,
					device.handle,
					segment,
					segment.byteSize().coerceAtMost(Int.MAX_VALUE.toLong()).toInt(),
					threadLocalDWORD0,
					MemorySegment.NULL
				) as Int
				if (status == 0) return DeferredOperation.Immediate(listOf(getIOStatusForError()))
				size += threadLocalDWORD0.get(DWORD, 0)
			}
			return DeferredOperation.Immediate(listOf(SendSizeData(size)))
		}
	}
}