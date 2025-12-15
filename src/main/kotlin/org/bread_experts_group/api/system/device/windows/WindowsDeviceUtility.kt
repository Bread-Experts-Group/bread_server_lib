package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.Flaggable.Companion.raw
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.api.system.device.SystemDeviceType
import org.bread_experts_group.api.system.device.feature.SystemDeviceBasicIdentifierFeature
import org.bread_experts_group.api.system.device.feature.SystemDeviceFriendlyNameFeature
import org.bread_experts_group.api.system.device.feature.SystemDeviceSerialPortNameFeature
import org.bread_experts_group.api.system.device.windows.WindowsSystemDeviceIODeviceFeature.Companion.getFlags
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
		val identityBytes = ByteArray(link.byteSize().toInt())
		MemorySegment.copy(
			link, ValueLayout.JAVA_BYTE, 0,
			identityBytes, 0, identityBytes.size
		)
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
	arena: Arena,
	pathSegment: MemorySegment
): SystemDevice = SystemDevice(SystemDeviceType.FILE_SYSTEM_ENTRY).also {
	val safeSegment = pathSegment.asReadOnly()
	val status = nativePathCchRemoveBackslash!!.invokeExact(
		safeSegment,
		safeSegment.byteSize() / 2
	) as Int
	if (status !in 0..1) throwLastError()
	it.registerCleaningAction { arena.close() }
	val path = safeSegment.getString(0, winCharsetWide)
	it.features.add(
		SystemDeviceBasicIdentifierFeature(
			ImplementationSource.SYSTEM_NATIVE,
			SystemDeviceFeatures.SYSTEM_IDENTIFIER,
			path
		)
	)
	val fileNameSegment = nativePathFindFileName!!(path)
	val fileName = fileNameSegment.reinterpret(Long.MAX_VALUE).getString(0, winCharsetWide)
	if (fileNameSegment != safeSegment) it.features.add(
		SystemDeviceFriendlyNameFeature(fileName, ImplementationSource.SYSTEM_NATIVE)
	)
	it.features.add(WindowsSystemDeviceIODeviceFeature(safeSegment))
	it.features.add(WindowsSystemDevicePathAppendFeature(safeSegment))
	it.features.add(WindowsSystemDeviceParentFeature(safeSegment))
	it.features.add(WindowsSystemDeviceChildrenFeature(safeSegment))
	it.features.add(WindowsSystemDeviceChildrenStreamsFeature(safeSegment))
	it.features.add(WindowsSystemDeviceCopyFeature(safeSegment))
	it.features.add(WindowsSystemDeviceDeleteFeature(safeSegment))
	it.features.add(WindowsSystemDeviceMoveFeature(safeSegment))
	it.features.add(WindowsSystemDeviceReplaceFeature(safeSegment))
	it.features.add(WindowsSystemDeviceSoftLinkFeature(safeSegment))
	it.features.add(WindowsSystemDeviceHardLinkFeature(safeSegment))
	it.features.add(WindowsSystemDeviceQueryTransparentEncryptionFeature(path))
	it.features.add(WindowsSystemDeviceTransparentEncryptionEnableFeature(path))
	it.features.add(WindowsSystemDeviceTransparentEncryptionDisableFeature(path))
	it.features.add(WindowsSystemDeviceTransparentEncryptionRawIODeviceFeature(path))
	it.features.add(WindowsSystemDeviceGetCreationTime(safeSegment))
	it.features.add(WindowsSystemDeviceGetLastAccessTime(safeSegment))
	it.features.add(WindowsSystemDeviceGetLastWriteTime(safeSegment))
	it.features.add(WindowsSystemDeviceGetLastMetadataWriteTime(safeSegment))
}

fun <T> readFileInfo(
	pathSegment: MemorySegment,
	features: Array<out OpenIODeviceFeatureIdentifier>,
	supportedFeatures: MutableList<OpenIODeviceFeatureIdentifier>,
	transformer: (MemorySegment) -> T
): T {
	var arena = Arena.ofConfined()
	val ext3 = arena.allocate(CREATEFILE3_EXTENDED_PARAMETERS)
	CREATEFILE3_EXTENDED_PARAMETERS_dwSize.set(ext3, 0L, ext3.byteSize().toInt())
	CREATEFILE3_EXTENDED_PARAMETERS_dwFileFlags.set(ext3, 0L, getFlags(features, supportedFeatures))
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