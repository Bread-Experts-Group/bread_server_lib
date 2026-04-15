package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.api.system.device.feature.SystemDevicePathElementFeature
import org.bread_experts_group.api.system.device.feature.SystemDeviceSerialPortNameFeature
import org.bread_experts_group.api.system.device.type.UnknownDeviceType
import org.bread_experts_group.api.system.device.type.WindowsDeviceTypes
import org.bread_experts_group.api.system.device.type.WindowsFileDeviceTypes
import org.bread_experts_group.api.system.io.status.StandardIOStatus
import org.bread_experts_group.api.system.io.windows.*
import org.bread_experts_group.ffi.GUID
import org.bread_experts_group.ffi.autoArena
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.*
import org.bread_experts_group.ffi.windows.cfgmgr.*
import org.bread_experts_group.ffi.windows.ioctl.*
import org.bread_experts_group.ffi.windows.setup.*
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle

val deviceCache = mutableMapOf<String, SystemDevice>()
fun decodeDevice(guid: GUID, link: MemorySegment, arena: Arena): SystemDevice {
	threadLocalDWORD1.set(DWORD, 0, 0)
	var status = nativeCM_Get_Device_Interface_PropertyWide!!.invokeExact(
		link,
		DEVPKEY_Device_InstanceId,
		threadLocalDWORD0,
		MemorySegment.NULL,
		threadLocalDWORD1,
		0
	) as Int
	if (status != 0x1A) TODO("CM Errors a: $status")
	val instanceID = arena.allocate(threadLocalDWORD1.get(DWORD, 0).toLong())
	status = nativeCM_Get_Device_Interface_PropertyWide.invokeExact(
		link,
		DEVPKEY_Device_InstanceId,
		threadLocalDWORD0,
		instanceID,
		threadLocalDWORD1,
		0
	) as Int
	val instanceIDString = instanceID.getString(0, winCharsetWide)
	if (status != 0) TODO("CM Errors b: $status")
	return deviceCache[instanceIDString] ?: SystemDevice(
		when (guid) {
			GUID_DEVINTERFACE_DISK -> WindowsDeviceTypes.DISK
			GUID_DEVINTERFACE_CDROM -> WindowsDeviceTypes.CDROM
			GUID_DEVINTERFACE_PARTITION -> WindowsDeviceTypes.PARTITION
			GUID_DEVINTERFACE_TAPE -> WindowsDeviceTypes.TAPE
			GUID_DEVINTERFACE_WRITEONCEDISK -> WindowsDeviceTypes.WRITE_ONCE_DISK
			GUID_DEVINTERFACE_VOLUME -> WindowsDeviceTypes.VOLUME
			GUID_DEVINTERFACE_MEDIUMCHANGER -> WindowsDeviceTypes.MEDIUM_CHANGER
			GUID_DEVINTERFACE_FLOPPY -> WindowsDeviceTypes.FLOPPY
			GUID_DEVINTERFACE_CDCHANGER -> WindowsDeviceTypes.CD_CHANGER
//			"{4D1E55B2-F16F-11CF-88CB-001111000030}" -> SystemDeviceType.HUMAN_INTERFACE
//			"{86E0D1E0-8089-11D0-9CE4-08003E301F73}" -> SystemDeviceType.SERIAL
//			"{4AFA3D53-74A7-11D0-BE5E-00A0C9062857}" -> SystemDeviceType.ACPI_POWER_BUTTON
//			"{884B96C3-56EF-11D1-BC8C-00A0C91405DD}" -> SystemDeviceType.KEYBOARD
//			"{378DE44C-56EF-11D1-BC8C-00A0C91405DD}" -> SystemDeviceType.MOUSE
//			"{A5DCBF10-6530-11D2-901F-00C04FB951ED}" -> SystemDeviceType.USB
//			"{CAC88484-7515-4C03-82E6-71A87ABAC361}" -> SystemDeviceType.NETWORKED
//			"{0850302A-B344-4FDA-9BE9-90576B8D46F0}" -> SystemDeviceType.BLUETOOTH_RADIO
			else -> UnknownDeviceType
		}
	).also {
		deviceCache[instanceIDString] = it
		it.features.add(WindowsSystemDeviceIODeviceFeature3(link))
		it.features.add(WindowsSystemDeviceIODeviceFeature2(link))
		// TODO guid ?
		val identityBytes = link.toArray(ValueLayout.JAVA_BYTE)
		val symLinkString = String(identityBytes, winCharsetWide)
		it.features.add(
			SystemDevicePathElementFeature.Fixed(
				SystemDeviceFeatures.PATH,
				ImplementationSource.SYSTEM_NATIVE,
				symLinkString
			)
		)
		val classGuid = guid.allocate(arena)
		val deviceInfoList = nativeSetupDiGetClassDevs!!(
			SetupDiGetClassDevsParameters(
				classGuid,
				instanceIDString,
				MemorySegment.NULL,
				0x00000010 // TODO DIGCF_DEVICEINTERFACE
			)
		)
		if (deviceInfoList == INVALID_HANDLE_VALUE) throwLastError()
		val devInfoData = arena.allocate(SP_DEVINFO_DATA)
		SP_DEVINFO_DATA_cbSize.set(devInfoData, 0, devInfoData.byteSize().toInt())
		nativeSetupDiOpenDeviceInfo!!(
			SetupDiOpenDeviceInfoParameters(
				deviceInfoList,
				instanceIDString,
				MemorySegment.NULL,
				0,
				devInfoData
			)
		)
		status = nativeSetupDiGetDevicePropertyWide!!.invokeExact(
			capturedStateSegment,
			deviceInfoList,
			devInfoData,
			DEVPKEY_Device_FriendlyName,
			threadLocalDWORD0,
			MemorySegment.NULL,
			0,
			threadLocalDWORD1,
			0
		) as Int
		if (status == 0 && win32LastError != WindowsLastError.ERROR_INSUFFICIENT_BUFFER.id.toInt()) throwLastError()
		val friendlyNameArea = arena.allocate(threadLocalDWORD1.get(DWORD, 0).toLong())
		status = nativeSetupDiGetDevicePropertyWide.invokeExact(
			capturedStateSegment,
			deviceInfoList,
			devInfoData,
			DEVPKEY_Device_FriendlyName,
			threadLocalDWORD0,
			friendlyNameArea,
			friendlyNameArea.byteSize().toInt(),
			MemorySegment.NULL,
			0
		) as Int
		if (status == 0) {
			if (win32LastError != WindowsLastError.ERROR_NOT_FOUND.id.toInt()) throwLastError()
		} else {
			val friendlyNameBytes = ByteArray(friendlyNameArea.byteSize().toInt())
			MemorySegment.copy(
				friendlyNameArea, ValueLayout.JAVA_BYTE, 0,
				friendlyNameBytes, 0, friendlyNameBytes.size
			)
			it.features.add(
				SystemDevicePathElementFeature.Fixed(
					SystemDeviceFeatures.PATH_ELEMENT_SHELL_DISPLAY_NAME,
					ImplementationSource.SYSTEM_NATIVE,
					String(friendlyNameBytes, winCharsetWide)
				)
			)
		}
		threadLocalDWORD1.set(DWORD, 0, 0)
		status = nativeCM_Get_Device_Interface_PropertyWide.invokeExact(
			link,
			DEVPKEY_DeviceInterface_Serial_PortName,
			threadLocalDWORD0,
			MemorySegment.NULL,
			threadLocalDWORD1,
			0
		) as Int
		if (status != 0x25) try { // TODO CM Errors
			if (status != 0x1A) TODO("CM Errors c: $status")
			val serialNameArea = arena.allocate(threadLocalDWORD1.get(DWORD, 0).toLong())
			status = nativeCM_Get_Device_Interface_PropertyWide.invokeExact(
				link,
				DEVPKEY_DeviceInterface_Serial_PortName,
				threadLocalDWORD0,
				serialNameArea,
				threadLocalDWORD1,
				0
			) as Int
			if (status != 0) TODO("CM Errors d: $status")
			it.features.add(
				SystemDeviceSerialPortNameFeature(
					serialNameArea.getString(0, winCharsetWide),
					ImplementationSource.SYSTEM_NATIVE
				)
			)
		} catch (e: WindowsLastErrorException) {
			if (e.error.enum != WindowsLastError.ERROR_NOT_FOUND) throw e
		}
	}
}

fun decodeDevice(eventData: WindowsCMNotifyEventData, arena: Arena) = when (eventData) {
	is WindowsCMNotifyEventData.DeviceInterface -> decodeDevice(eventData.guid, eventData.symbolicLink, arena)
	else -> TODO("Filter ... ${eventData.filterType}")
}

class ShellInfoFeature(
	private val readOnlySegment: MemorySegment,
	private val flags: Int,
	private val handle: MethodHandle,
	expresses: FeatureExpression<SystemDevicePathElementFeature>
) : SystemDevicePathElementFeature(expresses) {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean {
		val temp = autoArena.allocate(SHFILEINFOW)
		val infoStatus = (nativeSHGetFileInfoWide ?: return false).invokeExact(
			readOnlySegment,
			0,
			temp,
			temp.byteSize().toInt(),
			flags
		) as Long
		return infoStatus != 0L
	}

	override val element: String
		get() {
			val temp = autoArena.allocate(SHFILEINFOW)
			val infoStatus = nativeSHGetFileInfoWide!!.invokeExact(
				readOnlySegment,
				0,
				temp,
				temp.byteSize().toInt(),
				flags
			) as Long
			if (infoStatus == 0L) TODO("err")
			return (handle.invokeExact(temp, 0L) as MemorySegment).getString(0, winCharsetWide)
		}
}

fun winCreatePathDevice(
	widePathSegment: MemorySegment
): SystemDevice = SystemDevice(WindowsFileDeviceTypes.FILE).also {
	val readOnlySegment = widePathSegment.asReadOnly()
	val status = nativePathCchRemoveBackslash!!.invokeExact(
		readOnlySegment,
		readOnlySegment.byteSize() / WCHAR.byteSize()
	) as Int
	if (status != 0 && status != 1) throwLastError()
	it.features.add(
		SystemDevicePathElementFeature.Fixed(
			SystemDeviceFeatures.PATH,
			ImplementationSource.SYSTEM_NATIVE,
			readOnlySegment.getString(0, winCharsetWide)
		)
	)
	val fileNameSegment = nativePathFindFileNameWide!!.invokeExact(readOnlySegment) as MemorySegment
	val fileName = fileNameSegment.reinterpret(Long.MAX_VALUE).getString(0, winCharsetWide)
	it.features.add(
		SystemDevicePathElementFeature.Fixed(
			SystemDeviceFeatures.PATH_ELEMENT_LAST,
			ImplementationSource.SYSTEM_NATIVE,
			fileName.removeSuffix("\\")
		)
	)
	it.features.add(
		ShellInfoFeature(
			readOnlySegment,
			WindowsShellFileInfoFlags.SHGFI_DISPLAYNAME.position.toInt(),
			SHFILEINFOW_szDisplayName,
			SystemDeviceFeatures.PATH_ELEMENT_SHELL_DISPLAY_NAME
		)
	)
	it.features.add(
		ShellInfoFeature(
			readOnlySegment,
			WindowsShellFileInfoFlags.SHGFI_TYPENAME.position.toInt(),
			SHFILEINFOW_szTypeName,
			SystemDeviceFeatures.PATH_ELEMENT_SHELL_TYPE_DISPLAY_NAME
		)
	)
	it.features.add(WindowsSystemDeviceIODeviceFeature3(readOnlySegment))
	it.features.add(WindowsSystemDeviceIODeviceFeature2(readOnlySegment))
	it.features.add(WindowsSystemDevicePathAppendFeature(readOnlySegment))
	it.features.add(WindowsSystemDeviceParentFeature(readOnlySegment))
	it.features.add(WindowsSystemDeviceChildrenFeature(readOnlySegment))
	it.features.add(WindowsSystemDeviceChildrenStreamsFeature(readOnlySegment))
	it.features.add(WindowsSystemDeviceCopyFeature(readOnlySegment))
	it.features.add(WindowsSystemDeviceDeleteFeature(readOnlySegment))
	it.features.add(WindowsSystemDeviceMoveFeature(readOnlySegment))
	it.features.add(WindowsSystemDeviceReplaceFeature(readOnlySegment))
	it.features.add(WindowsSystemDeviceSoftLinkFeature(readOnlySegment))
	it.features.add(WindowsSystemDeviceHardLinkFeature(readOnlySegment))
	it.features.add(WindowsSystemDeviceQueryTransparentEncryptionFeature(readOnlySegment))
	it.features.add(WindowsSystemDeviceTransparentEncryptionEnableFeature(readOnlySegment))
	it.features.add(WindowsSystemDeviceTransparentEncryptionDisableFeature(readOnlySegment))
	it.features.add(WindowsSystemDeviceTransparentEncryptionRawIODeviceFeature(readOnlySegment))
	it.features.add(WindowsSystemDeviceGetCreationTime(readOnlySegment))
	it.features.add(WindowsSystemDeviceGetLastAccessTime(readOnlySegment))
	it.features.add(WindowsSystemDeviceGetLastWriteTime(readOnlySegment))
}

fun addFileFeatures(
	device: WindowsIODevice,
	read: Boolean,
	write: Boolean
) {
	if (read || write) device.features.add(WindowsIODeviceSeekFeature(device))
	if (read) device.features.add(WindowsIODeviceReadFeature(device))
	if (write) {
		device.features.add(WindowsIODeviceWriteFeature(device))
		device.features.add(WindowsIODeviceFlushFeature(device))
		device.features.add(WindowsIODeviceSetSizeFeature(device))
	}
	device.features.add(WindowsIOGetDeviceGeometryFeature(device))
	device.features.add(WindowsIODeviceBypassFSDriverBoundsChecksFeature(device))
	device.features.add(WindowsIODeviceGetDeviceFirmwareInfoFeature(device))
	device.features.add(WindowsIODeviceGetDevicePartitionLayoutInfoFeature(device))
	device.features.add(WindowsIODeviceReopenFeature(device))
	device.features.add(WindowsIODeviceGetSizeFeature(device))
	device.features.add(WindowsIODeviceDataRangeLockFeature(device))
}

fun getIOStatusForError(): StandardIOStatus = when (win32LastError) {
	WindowsLastError.ERROR_FILE_NOT_FOUND.id.toInt() -> StandardIOStatus.DEVICE_NOT_FOUND
	WindowsLastError.ERROR_ACCESS_DENIED.id.toInt() -> StandardIOStatus.ACCESS_DENIED
	WindowsLastError.ERROR_SHARING_VIOLATION.id.toInt() -> StandardIOStatus.DEVICE_IN_USE

	else -> throwLastError()
}