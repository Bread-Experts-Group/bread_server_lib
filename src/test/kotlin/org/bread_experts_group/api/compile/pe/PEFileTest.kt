package org.bread_experts_group.org.bread_experts_group.api.compile.pe

import org.bread_experts_group.MappedEnumeration
import org.bread_experts_group.api.compile.ebc.EBCMoveTypes
import org.bread_experts_group.api.compile.ebc.EBCProcedure
import org.bread_experts_group.api.compile.ebc.EBCProcedure.Companion.naturalIndex16
import org.bread_experts_group.api.compile.ebc.EBCRegisters
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
			testBase.resolve("test.pe.efi"),
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
						rawData = EBCProcedure()
							.MOVn(
								EBCRegisters.R1, false,
								EBCRegisters.R0, true,
								null, naturalIndex16(
									false,
									1u,
									16u
								) // SYSTEM TABLE
							)
							.MOVn(
								EBCRegisters.R4, false,
								EBCRegisters.R1, true,
								null, naturalIndex16(
									false,
									4u,
									32u
								) // CON OUT
							)
							.MOVn(
								EBCRegisters.R3, false,
								EBCRegisters.R4, true,
								null, naturalIndex16(
									false,
									1u,
									0u
								) // OUTPUT STRING
							)
							.MOVI(
								EBCRegisters.R6, false,
								null, EBCMoveTypes.QUADWORD_64,
								0x00402000u
							)
							.PUSHn(EBCRegisters.R6, false, null) // String
							.PUSHn(EBCRegisters.R4, false, null) // This
							.CALL(
								EBCRegisters.R3,
								operand1Indirect = false,
								relative = false,
								native = true,
								immediate = null
							)
							.MOVn(
								EBCRegisters.R0, false,
								EBCRegisters.R0, false,
								null, naturalIndex16(
									false,
									2u,
									0u
								)
							)
							.RET()
							.output
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
						rawData = "rere002\r\n\u0000".toByteArray(Charsets.UTF_16LE)
					}
				)
			}.build(it)
		}
	}
}