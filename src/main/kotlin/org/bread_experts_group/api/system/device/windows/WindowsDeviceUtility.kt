package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.generic.Flaggable.Companion.raw
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.api.system.device.SystemDeviceType
import org.bread_experts_group.api.system.device.feature.SystemDeviceBasicIdentifierFeature
import org.bread_experts_group.api.system.device.feature.SystemDeviceFriendlyNameFeature
import org.bread_experts_group.api.system.device.feature.SystemDeviceSerialPortNameFeature
import org.bread_experts_group.api.system.device.windows.WindowsSystemDeviceIODeviceFeature.Companion.getFlags
import org.bread_experts_group.api.system.io.open.OpenIODeviceDataIdentifier
import org.bread_experts_group.api.system.io.open.OpenIODeviceFeatureIdentifier
import org.bread_experts_group.ffi.GUID
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.*
import org.bread_experts_group.ffi.windows.cfgmgr.*
import org.bread_experts_group.ffi.windows.setup.*
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.util.*

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
		when (guid.toString()) {
			"{4D1E55B2-F16F-11CF-88CB-001111000030}" -> SystemDeviceType.HUMAN_INTERFACE
			"{86E0D1E0-8089-11D0-9CE4-08003E301F73}" -> SystemDeviceType.SERIAL
			"{53F56307-B6BF-11D0-94F2-00A0C91EFB8B}" -> SystemDeviceType.STORAGE
			"{53F5630D-B6BF-11D0-94F2-00A0C91EFB8B}" -> SystemDeviceType.VOLUME_STORAGE
			"{4AFA3D53-74A7-11D0-BE5E-00A0C9062857}" -> SystemDeviceType.ACPI_POWER_BUTTON
			"{884B96C3-56EF-11D1-BC8C-00A0C91405DD}" -> SystemDeviceType.KEYBOARD
			"{378DE44C-56EF-11D1-BC8C-00A0C91405DD}" -> SystemDeviceType.MOUSE
			"{A5DCBF10-6530-11D2-901F-00C04FB951ED}" -> SystemDeviceType.USB
			"{CAC88484-7515-4C03-82E6-71A87ABAC361}" -> SystemDeviceType.NETWORKED
			"{0850302A-B344-4FDA-9BE9-90576B8D46F0}" -> SystemDeviceType.BLUETOOTH_RADIO
			else -> SystemDeviceType.OTHER
		}
	).also {
		deviceCache[instanceIDString] = it
		it.features.add(
			WindowsSystemDeviceIODeviceFeature(link)
		)
		it.features.add(
			SystemDeviceBasicIdentifierFeature(
				ImplementationSource.SYSTEM_NATIVE,
				SystemDeviceFeatures.SYSTEM_TYPE_IDENTIFIER,
				guid
			)
		)
		val identityBytes = link.toArray(ValueLayout.JAVA_BYTE)
		val symLinkString = String(identityBytes, winCharsetWide)
		it.features.add(
			SystemDeviceBasicIdentifierFeature(
				ImplementationSource.SYSTEM_NATIVE,
				SystemDeviceFeatures.SYSTEM_IDENTIFIER,
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
		try {
			nativeSetupDiGetDevicePropertyWide!!.invokeExact(
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
			if (status == 0) throwLastError()
			val friendlyNameBytes = ByteArray(friendlyNameArea.byteSize().toInt())
			MemorySegment.copy(
				friendlyNameArea, ValueLayout.JAVA_BYTE, 0,
				friendlyNameBytes, 0, friendlyNameBytes.size
			)
			it.features.add(
				SystemDeviceFriendlyNameFeature(
					String(friendlyNameBytes, winCharsetWide),
					ImplementationSource.SYSTEM_NATIVE
				)
			)
		} catch (e: WindowsLastErrorException) {
			if (e.error.enum != WindowsLastError.ERROR_NOT_FOUND) throw e
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

fun winCreatePathDevice(
	widePathSegment: MemorySegment
): SystemDevice = SystemDevice(SystemDeviceType.FILE_SYSTEM_ENTRY).also {
	val readOnlySegment = widePathSegment.asReadOnly()
	val status = nativePathCchRemoveBackslash!!.invokeExact(
		readOnlySegment,
		readOnlySegment.byteSize() / WCHAR.byteSize()
	) as Int
	if (status != 0 && status != 1) throwLastError()
	it.features.add(
		SystemDeviceBasicIdentifierFeature(
			ImplementationSource.SYSTEM_NATIVE,
			SystemDeviceFeatures.SYSTEM_IDENTIFIER,
			readOnlySegment.getString(0, winCharsetWide)
		)
	)
	val fileNameSegment = nativePathFindFileNameWide!!.invokeExact(readOnlySegment) as MemorySegment
	if (fileNameSegment != readOnlySegment) it.features.add(
		SystemDeviceFriendlyNameFeature(
			fileNameSegment.reinterpret(Long.MAX_VALUE).getString(0, winCharsetWide),
			ImplementationSource.SYSTEM_NATIVE
		)
	)
	it.features.add(WindowsSystemDeviceIODeviceFeature(readOnlySegment))
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
	it.features.add(WindowsSystemDeviceGetLastMetadataWriteTime(readOnlySegment))
}

fun <T> readFileInfo(
	pathSegment: MemorySegment,
	features: Array<out OpenIODeviceFeatureIdentifier>,
	data: MutableList<OpenIODeviceDataIdentifier>,
	transformer: (MemorySegment) -> T
): T {
	var arena = Arena.ofConfined()
	val ext3 = arena.allocate(CREATEFILE3_EXTENDED_PARAMETERS)
	CREATEFILE3_EXTENDED_PARAMETERS_dwSize.set(ext3, 0L, ext3.byteSize().toInt())
	CREATEFILE3_EXTENDED_PARAMETERS_dwFileFlags.set(ext3, 0L, getFlags(features, data))
	val handle = nativeCreateFile3!!.invokeExact(
		capturedStateSegment,
		pathSegment,
		0,
		EnumSet.of(
			WindowsFileSharingTypes.FILE_SHARE_READ,
			WindowsFileSharingTypes.FILE_SHARE_WRITE
		).raw().toInt(),
		WindowsCreationDisposition.OPEN_EXISTING.id.toInt(),
		ext3
	) as MemorySegment
	arena.close()
	if (handle == INVALID_HANDLE_VALUE) throwLastError()
	arena = Arena.ofConfined()
	val transformed = try {
		val basicInfo = arena.allocate(FILE_BASIC_INFO)
		if (
			nativeGetFileInformationByHandleEx!!.invokeExact(
				capturedStateSegment,
				handle,
				WindowsFileInfoByHandleClasses.FileBasicInfo.id.toInt(),
				basicInfo,
				basicInfo.byteSize().toInt()
			) as Int == 0
		) throwLastError()
		transformer(basicInfo)
	} catch (e: Throwable) {
		throw e
	} finally {
		arena.close()
	}
	if (nativeCloseHandle!!.invokeExact(capturedStateSegment, handle) as Int == 0) throwLastError()
	return transformed
}