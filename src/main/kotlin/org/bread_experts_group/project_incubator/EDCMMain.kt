package org.bread_experts_group.project_incubator

import org.bread_experts_group.api.compile.ebc.EBCJVMCompilation
import org.bread_experts_group.api.compile.ebc.efi.EFIExample
import org.bread_experts_group.api.compile.mzdos.MZDOSFile
import org.bread_experts_group.api.compile.pe.*
import org.bread_experts_group.api.system.SystemFeatures
import org.bread_experts_group.api.system.SystemProvider
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.api.system.io.IODevice
import org.bread_experts_group.api.system.io.IODeviceFeatures
import org.bread_experts_group.api.system.io.open.FileIOOpenFeatures
import org.bread_experts_group.api.system.io.open.FileIOReOpenFeatures
import org.bread_experts_group.api.system.io.open.StandardIOOpenFeatures
import org.bread_experts_group.api.system.io.seek.StandardSeekIODeviceFeatures
import org.bread_experts_group.generic.MappedEnumeration
import org.bread_experts_group.generic.io.reader.BSLWriter
import java.util.*
import kotlin.io.path.toPath

fun main() {
	val device = SystemProvider.get(SystemFeatures.GET_PATH_DEVICE_DIRECT).get(
		"J:\\efi\\boot\\BOOTX64.efi"
	)
	val io = device.get(SystemDeviceFeatures.IO_DEVICE).open(
		StandardIOOpenFeatures.CREATE,
		FileIOOpenFeatures.TRUNCATE,
		FileIOReOpenFeatures.WRITE,
		FileIOReOpenFeatures.SHARE_READ
	).firstNotNullOf { it as? IODevice }
	val writer = BSLWriter(io.get(IODeviceFeatures.WRITE))
	val seek = io.get(IODeviceFeatures.SEEK)

	PEFile.of {
		mz = MZDOSFile.of {}
		machineType = MappedEnumeration(PEMachineTypes.IMAGE_FILE_MACHINE_EBC)
		characteristics = EnumSet.of(
			PECharacteristics.IMAGE_FILE_EXECUTABLE_IMAGE
		)
		optionalHeader = PE32OptionalHeader.of {
			windowsSpecific = PE32WindowsOptionalHeader.of {
				sectionAlignment = 4096u
				sizeOfHeapCommit = 0x00001000u
				sizeOfHeapReserve = 0x00100000u
				sizeOfStackCommit = 0x00001000u
				sizeOfStackReserve = 0x00100000u
				subsystem = MappedEnumeration(PEWindowsSubsystem.IMAGE_SUBSYSTEM_EFI_APPLICATION)
//						majorOperatingSystemVersion = 4u
//						majorSubsystemVersion = 6u
			}
			entryPoint = 0x1000u
			codeBase = 0x1000u
		}
		val output = EBCJVMCompilation.compileClass(
			EFIExample::class,
			EFIExample::class.java.protectionDomain.codeSource.location.toURI().toPath(),
			0x00401000u, 0x00410000u, 0x00411000u
		)
		sections = listOf(
			PESection.of {
				setName(".text")
				virtualSize = 0x9000u
				virtualAddress = 0x1000u

				characteristics = EnumSet.of(
					PESectionCharacteristics.IMAGE_SCN_CNT_CODE,
					PESectionCharacteristics.IMAGE_SCN_MEM_READ,
					PESectionCharacteristics.IMAGE_SCN_MEM_EXECUTE
				)
				rawData = output.code
			},
			PESection.of {
				setName(".init")
				virtualSize = 0x1000u
				virtualAddress = 0x10000u
				characteristics = EnumSet.of(
					PESectionCharacteristics.IMAGE_SCN_MEM_READ,
					PESectionCharacteristics.IMAGE_SCN_CNT_INITIALIZED_DATA
				)
				rawData = output.initializedData
			},
			PESection.of {
				setName(".uninit")
				virtualSize = 0x1000u
				virtualAddress = 0x11000u
				characteristics = EnumSet.of(
					PESectionCharacteristics.IMAGE_SCN_MEM_READ,
					PESectionCharacteristics.IMAGE_SCN_MEM_WRITE,
					PESectionCharacteristics.IMAGE_SCN_CNT_UNINITIALIZED_DATA
				)
			}
		)
	}.build(
		writer,
		{ seek.seek(0, StandardSeekIODeviceFeatures.CURRENT).first },
		{ seek.seek(it, StandardSeekIODeviceFeatures.BEGIN) }
	)
	writer.flush()
}