package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.device.enumerate.SystemDeviceEnumerationDataIdentifier
import org.bread_experts_group.api.system.device.enumerate.SystemDeviceEnumerationFeatureIdentifier
import org.bread_experts_group.api.system.device.enumerate.SystemDeviceIterator
import org.bread_experts_group.api.system.device.feature.SystemDeviceEnumerationFeature
import org.bread_experts_group.api.system.device.type.SystemDeviceTypeIdentifier
import org.bread_experts_group.api.system.device.type.WindowsDeviceTypes
import org.bread_experts_group.ffi.GUID
import org.bread_experts_group.ffi.autoArena
import org.bread_experts_group.ffi.windows.DWORD
import org.bread_experts_group.ffi.windows.WCHAR
import org.bread_experts_group.ffi.windows.cfgmgr.nativeCM_Get_Device_Interface_ListWide
import org.bread_experts_group.ffi.windows.cfgmgr.nativeCM_Get_Device_Interface_List_SizeWide
import org.bread_experts_group.ffi.windows.ioctl.*
import org.bread_experts_group.ffi.windows.threadLocalDWORD0
import java.lang.foreign.MemorySegment

class WindowsSystemDeviceEnumerationFeature : SystemDeviceEnumerationFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = true // TODO supported

	private fun getIterator(
		guid: GUID,
		type: SystemDeviceTypeIdentifier
	): SystemDeviceEnumerationDataIdentifier {
		val guidAllocated = guid.allocate(autoArena)
		var status = nativeCM_Get_Device_Interface_List_SizeWide!!.invokeExact(
			threadLocalDWORD0,
			guidAllocated,
			MemorySegment.NULL,
			0x00000000 // TODO CM_GET_DEVICE_INTERFACE_LIST_PRESENT
		) as Int
		if (status != 0) TODO("CM Errors $status")
		val buffer = autoArena.allocate(
			threadLocalDWORD0.get(DWORD, 0).toLong() * WCHAR.byteSize()
		)
		status = nativeCM_Get_Device_Interface_ListWide!!.invokeExact(
			guidAllocated,
			MemorySegment.NULL,
			buffer,
			buffer.byteSize().toInt(),
			0x00000000 // TODO CM_GET_DEVICE_INTERFACE_LIST_PRESENT
		) as Int
		if (status != 0) TODO("CM Errors $status")

		return object : SystemDeviceIterator(type) {
			private var nextDevice: SystemDevice? = null
			private var offset = 0L
			fun pullNext() {
				if (offset >= buffer.byteSize()) {
					nextDevice = null
					return
				}
				var size = 0L
				while ((offset + size) < buffer.byteSize()) {
					val char = buffer.get(WCHAR, offset + size)
					size += WCHAR.byteSize()
					if (char.code == 0) break
				}
				if (size == WCHAR.byteSize()) {
					nextDevice = null
					return
				}
				nextDevice = decodeDevice(
					guid,
					buffer.asSlice(offset, size),
					autoArena
				)
				offset += size
			}

			init {
				pullNext()
			}

			override fun hasNext(): Boolean = nextDevice != null
			override fun next(): SystemDevice {
				val dev = nextDevice ?: throw NoSuchElementException()
				pullNext()
				return dev
			}
		}
	}

	override fun enumerate(
		vararg features: SystemDeviceEnumerationFeatureIdentifier
	): List<SystemDeviceEnumerationDataIdentifier> {
		val data = mutableListOf<SystemDeviceEnumerationDataIdentifier>()
		val usedTypes = mutableListOf<SystemDeviceEnumerationFeatureIdentifier>()
		for (feature in features) when (feature) {
			WindowsDeviceTypes.DISK,
			WindowsDeviceTypes.CDROM,
			WindowsDeviceTypes.PARTITION,
			WindowsDeviceTypes.TAPE,
			WindowsDeviceTypes.WRITE_ONCE_DISK,
			WindowsDeviceTypes.VOLUME,
			WindowsDeviceTypes.MEDIUM_CHANGER,
			WindowsDeviceTypes.FLOPPY,
			WindowsDeviceTypes.CD_CHANGER -> usedTypes.add(feature)
		}
		for (feature in usedTypes) when (feature) {
			WindowsDeviceTypes.DISK -> data.add(getIterator(GUID_DEVINTERFACE_DISK, WindowsDeviceTypes.DISK))
			WindowsDeviceTypes.CDROM -> data.add(getIterator(GUID_DEVINTERFACE_CDROM, WindowsDeviceTypes.CDROM))
			WindowsDeviceTypes.PARTITION -> data.add(
				getIterator(GUID_DEVINTERFACE_PARTITION, WindowsDeviceTypes.PARTITION)
			)

			WindowsDeviceTypes.TAPE -> data.add(getIterator(GUID_DEVINTERFACE_TAPE, WindowsDeviceTypes.TAPE))
			WindowsDeviceTypes.WRITE_ONCE_DISK -> data.add(
				getIterator(GUID_DEVINTERFACE_WRITEONCEDISK, WindowsDeviceTypes.WRITE_ONCE_DISK)
			)

			WindowsDeviceTypes.VOLUME -> data.add(getIterator(GUID_DEVINTERFACE_VOLUME, WindowsDeviceTypes.VOLUME))
			WindowsDeviceTypes.MEDIUM_CHANGER -> data.add(
				getIterator(GUID_DEVINTERFACE_MEDIUMCHANGER, WindowsDeviceTypes.MEDIUM_CHANGER)
			)

			WindowsDeviceTypes.FLOPPY -> data.add(getIterator(GUID_DEVINTERFACE_FLOPPY, WindowsDeviceTypes.FLOPPY))
			WindowsDeviceTypes.CD_CHANGER -> data.add(
				getIterator(GUID_DEVINTERFACE_CDCHANGER, WindowsDeviceTypes.CD_CHANGER)
			)
		}
		return data
	}
}