package org.bread_experts_group.org.bread_experts_group.api.compile.pe

import org.bread_experts_group.MappedEnumeration
import org.bread_experts_group.api.compile.ebc.EBCProcedure
import org.bread_experts_group.api.compile.ebc.efi.EFIExample
import org.bread_experts_group.api.compile.mzdos.MZDOSFile
import org.bread_experts_group.api.compile.pe.*
import org.bread_experts_group.org.bread_experts_group.testBase
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.util.*
import kotlin.io.path.toPath
import kotlin.test.Test

class PEFileTest {
	@Test
	fun test() {
		Files.newByteChannel(
			testBase.resolve("D:\\test.pe.efi"),
			StandardOpenOption.CREATE,
			StandardOpenOption.WRITE,
			StandardOpenOption.TRUNCATE_EXISTING
		).use {
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
				val code = EBCProcedure.compile(
					EFIExample::class,
					EFIExample::class.java.protectionDomain.codeSource.location.toURI().toPath(),
					0x00401000u, 0x00402000u, 0x00403000u
				)
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
						rawData = code[0]
					},
					PESection.of {
						setName(".init")
						virtualSize = 0x1000u
						virtualAddress = 0x2000u
						characteristics = EnumSet.of(
							PESectionCharacteristics.IMAGE_SCN_MEM_READ,
							PESectionCharacteristics.IMAGE_SCN_CNT_INITIALIZED_DATA
						)
						// █
						rawData = code[1]
//						rawData = "rere002\r\n\u0000".toByteArray(Charsets.UTF_16LE)
					},
					PESection.of {
						setName(".uninit")
						virtualSize = 0x1000u
						virtualAddress = 0x3000u
						characteristics = EnumSet.of(
							PESectionCharacteristics.IMAGE_SCN_MEM_READ,
							PESectionCharacteristics.IMAGE_SCN_MEM_WRITE,
							PESectionCharacteristics.IMAGE_SCN_CNT_UNINITIALIZED_DATA
						)
					}
				)
			}.build(it)
		}
	}
}