package org.bread_experts_group.org.bread_experts_group.api.compile.pe

import org.bread_experts_group.MappedEnumeration
import org.bread_experts_group.api.compile.mzdos.MZDOSFile
import org.bread_experts_group.api.compile.pe.*
import org.bread_experts_group.org.bread_experts_group.testBase
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.util.*
import kotlin.test.Test

class PEFileTest {
	@Test
	fun test() {
		Files.newByteChannel(
			testBase.resolve("test.pe.exe"),
			StandardOpenOption.CREATE,
			StandardOpenOption.WRITE,
			StandardOpenOption.TRUNCATE_EXISTING
		).use {
			PEFile.of {
				mz = MZDOSFile.of {}
				machineType = MappedEnumeration(PEMachineTypes.IMAGE_FILE_MACHINE_I386)
				characteristics = EnumSet.of(
					PECharacteristics.IMAGE_FILE_EXECUTABLE_IMAGE,
					PECharacteristics.IMAGE_FILE_32BIT_MACHINE
				)
				optionalHeader = PE32OptionalHeader.of {
					windowsSpecific = PE32WindowsOptionalHeader.of {
						sectionAlignment = 4096u
						sizeOfHeapCommit = 0x00001000u
						sizeOfHeapReserve = 0x00100000u
						sizeOfStackCommit = 0x00001000u
						sizeOfStackReserve = 0x00100000u
						subsystem = MappedEnumeration(PEWindowsSubsystem.IMAGE_SUBSYSTEM_WINDOWS_GUI)
						majorOperatingSystemVersion = 4u
						majorSubsystemVersion = 6u
					}
					entryPoint = 0x1000u
					codeBase = 0x1000u
				}
				sections = listOf(
					PESection.of {
						setName(".text")
						virtualSize = 0x00001000u
						virtualAddress = 0x1000u
						characteristics = EnumSet.of(
							PESectionCharacteristics.IMAGE_SCN_CNT_CODE,
							PESectionCharacteristics.IMAGE_SCN_MEM_READ,
							PESectionCharacteristics.IMAGE_SCN_MEM_EXECUTE
						)
						rawData = byteArrayOf(
							0xA1.toByte(),
							0, 0x20, 0x40, 0,
							0xC3.toByte()
						)
					},
					PESection.of {
						setName(".init")
						virtualSize = 0x1000u
						virtualAddress = 0x2000u
						characteristics = EnumSet.of(
							PESectionCharacteristics.IMAGE_SCN_MEM_READ,
							PESectionCharacteristics.IMAGE_SCN_CNT_INITIALIZED_DATA
						)
						rawData = byteArrayOf(0xFF.toByte(), 0, 0, 0)
					}
				)
			}.build(it)
		}
	}
}