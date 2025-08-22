package org.bread_experts_group.computer.ia32.bios

import org.bread_experts_group.computer.BIOSProvider
import org.bread_experts_group.computer.BinaryUtil.hex
import org.bread_experts_group.computer.Computer
import org.bread_experts_group.computer.disc.el_torito.ElToritoBootCatalogDescriptor
import org.bread_experts_group.computer.disc.el_torito.ElToritoBootCatalogDescriptor.Companion.BOOT_SYSTEM_IDENTIFIER
import org.bread_experts_group.computer.disc.el_torito.ElToritoBootCatalogInitialEntry
import org.bread_experts_group.computer.disc.el_torito.ElToritoBootCatalogValidationEntry
import org.bread_experts_group.computer.disc.el_torito.ElToritoPlatform
import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.bios.h10.*
import org.bread_experts_group.computer.ia32.bios.h13.*
import org.bread_experts_group.computer.ia32.bios.h14.InitializeSerial
import org.bread_experts_group.computer.ia32.bios.h16.CheckKeystroke
import org.bread_experts_group.computer.ia32.bios.h16.GetKeyboardFunctionality
import org.bread_experts_group.computer.ia32.bios.h16.GetKeystroke
import org.bread_experts_group.computer.ia32.bios.h16.GetShiftFlags
import org.bread_experts_group.computer.ia32.bios.h17.InitializePrinter
import org.bread_experts_group.computer.ia32.bios.h17.WriteCharacterToPrinter
import org.bread_experts_group.computer.ia32.bios.h19.BootstrapLoader
import org.bread_experts_group.computer.ia32.bios.h1A.GetSystemTicks
import org.bread_experts_group.computer.ia32.instruction.impl.InterruptReturn.Companion.BIOS_RETURN
import org.bread_experts_group.io.reader.ReadingByteBuffer
import org.bread_experts_group.io.reader.ReadingByteBuffer.Companion.reading
import java.nio.ByteBuffer
import java.nio.channels.Channels

class StandardBIOS : BIOSProvider {
	val teletype: TeletypeOutput = TeletypeOutput()
	val teletypeString: TeletypeOutputString = TeletypeOutputString(this.teletype)
	val setVideoMode: SetVideoMode = SetVideoMode(this.teletype)
	val getVideoMode: GetVideoMode = GetVideoMode(this.teletype)
	val setCursor: SetCursorPosition = SetCursorPosition(this.teletype)
	val getCursor: GetCursorPosition = GetCursorPosition(this.teletype)
	val selectActiveDisplayPage: SelectActiveDisplayPage = SelectActiveDisplayPage(this.teletype)
	val scrollUp: ScrollUp = ScrollUp(this.teletype)
	val putCharacter: PutCharacter = PutCharacter(this.teletype)

	override fun initialize(computer: Computer) {
		val processor = computer.processor as IA32Processor
		processor.computer = computer
		// See Intel® Platform Innovation Framework for UEFI, Compatibility Support Module Specification, Rev 0.98
		// Section 5.2, Table 14, "Fixed BIOS Entry Points", Page 162.
		// Write Real Mode Interrupt Vector Table
		for (offset in ULong.MIN_VALUE..255u) processor.computer.setMemoryAt32((offset * 4u), 0xF000FF50u)
		// (F000:FF50) Unknown INT Reset
		processor.setHook(0xF000u, 0xFF50u) {
			throw UnsupportedOperationException("Unsupported interrupt hit")
		}
		// (F000:FF4C) INT 0x1C Periodic Timer Interrupt No-op
		processor.computer.setMemoryAt32(0x0070u, 0xF000FF4Cu)
		processor.setHook(0xF000u, 0xFF4Cu) {
			BIOS_RETURN.handle(processor)
		}
		// (F000:FE6E) INT 0x1A System-Timer Services
		processor.computer.setMemoryAt32(0x0068u, 0xF000FE6Eu)
		processor.setHook(0xF000u, 0xFE6Eu) {
			when (processor.a.h.toUInt()) {
				0x00u -> GetSystemTicks
				else -> throw IllegalArgumentException("Unknown 0x1A ah: ${hex(processor.a.th)}")
			}.handle(processor)
		}
		// (F000:E6F2) INT 0x19 Entry Point
		processor.computer.setMemoryAt32(0x0064u, 0xF000E6F2u)
		processor.setHook(0xF000u, 0xE6F2u) {
			when (processor.a.h.toUInt()) {
				0x00u -> BootstrapLoader
				else -> throw IllegalArgumentException("Unknown 0x19 ah: ${hex(processor.a.th)}")
			}.handle(processor)
		}
		// (F000:EFD0) INT 0x17 Printer Services
		processor.computer.setMemoryAt32(0x005Cu, 0xF000EFD0u)
		processor.setHook(0xF000u, 0xEFD0u) {
			when (processor.a.h.toUInt()) {
				0x00u -> WriteCharacterToPrinter
				0x01u -> InitializePrinter
				else -> throw IllegalArgumentException("Unknown 0x17 ah: ${hex(processor.a.th)}")
			}.handle(processor)
		}
		// (F000:E82E) INT 0x16 Keyboard Services
		processor.computer.setMemoryAt32(0x0058u, 0xF000E82Eu)
		processor.setHook(0xF000u, 0xE82Eu) {
			when (processor.a.h.toUInt()) {
				0x00u -> GetKeystroke
				0x01u -> CheckKeystroke
				0x02u -> GetShiftFlags
				0x09u -> GetKeyboardFunctionality
				else -> throw IllegalArgumentException("Unknown 0x16 ah: ${hex(processor.a.th)}")
			}.handle(processor)
		}
		// (F000:E739) INT 0x14 Serial
		processor.computer.setMemoryAt32(0x0050u, 0xF000E739u)
		processor.setHook(0xF000u, 0xE739u) {
			when (processor.a.h.toUInt()) {
				0x00u -> InitializeSerial
				else -> throw IllegalArgumentException("Unknown 0x14 ah: ${hex(processor.a.th)}")
			}.handle(processor)
		}
		// (F000:EC59) INT 0x13 Floppy
		processor.computer.setMemoryAt32(0x004Cu, 0xF000EC59u)
		processor.setHook(0xF000u, 0xEC59u) {
			when (processor.a.h.toUInt()) {
				0x00u -> ResetDiskSystem
				0x02u -> Read
				0x08u -> GetDriveParameters
				0x41u -> InstallationCheck
				0x42u -> ExtendedRead
				else -> throw IllegalArgumentException("Unknown 0x13 ah: ${hex(processor.a.th)}")
			}.handle(processor)
		}
		// (F000:F841) INT 0x12 Memory Size
		processor.computer.setMemoryAt32(0x0048u, 0xF000F841u)
		processor.setHook(0xF000u, 0xF841u) {
			BIOS_RETURN.handle(processor)
			val memory = (processor.computer.memory.sumOf { it.capacity } / 1024u)
			processor.a.tx = minOf(memory, UShort.MAX_VALUE.toUInt()).toUShort()
		}
		// (F000:F84D) INT 0x11 Equipment Determination
		processor.computer.setMemoryAt32(0x0044u, 0xF000F84Du)
		processor.setHook(0xF000u, 0xF84Du) {
			// https://www.ctyme.com/intr/rb-0575.htm
			BIOS_RETURN.handle(processor)
			// parallel ports / internal modem / game port / serial ports / _ / floppies / default video mode /
			// 16K memory banks / 80x87 coprocessor / floppies installed
			var equipment = 0b00_0_0_001_0_00_10_11_1_1u
			equipment = equipment or ((computer.floppyURLs.size - 1) shl 6).toUInt()
			processor.a.tex = equipment
		}
		// (F000:F065) INT 0x10 Video
		processor.computer.setMemoryAt32(0x0040u, 0xF000F065u)
		processor.setHook(0xF000u, 0xF065u) {
			when (processor.a.h.toUInt()) {
				0x00u -> this.setVideoMode
				0x02u -> this.setCursor
				0x03u -> this.getCursor
				0x05u -> this.selectActiveDisplayPage
				0x06u -> this.scrollUp
				0x09u -> this.putCharacter
				0x0Eu -> this.teletype
				0x0Fu -> this.getVideoMode
				0x13u -> this.teletypeString
				else -> throw IllegalArgumentException("Unknown 0x10 ah: ${hex(processor.a.th)}")
			}.handle(processor)
		}
		// (F000:FEA0) INT 0x08 System Timer
		processor.computer.setMemoryAt32(0x20u, 0xF000FEA0u)
		processor.setHook(0xF000u, 0xFEA0u) {
			BIOS_RETURN.handle(processor)
			processor.initiateInterrupt(0x1Cu)
		}
		// (F000:FF53) "Dummy Interrupt Handler"
		processor.computer.setMemoryAt((0xF000u * 0x10u) + 0xFF53u, 0xCFu)
		// (F000:FFF0) "Power-On Entry Point" / Reset Vector
		processor.setHook(0xF000u, 0xFFF0u) {
			if (computer.floppyURLs[0] != null) {
				it.decoding.loadFloppyIntoMemory(0, 0u, 16384u, 0x7C00u)
				it.d.l = 0x00u
				it.cs.rx = 0u
				it.ip.rx = 0x7C00u
				return@setHook
			}
			if (computer.discURL != null) {
				val primary = computer.discPrimaryVolume ?: throw IllegalArgumentException("No disc inserted")
				val boot = computer.discBoot ?: throw IllegalArgumentException("Disc is not bootable: no boot record")
				if (boot.bootSystemIdentifier != BOOT_SYSTEM_IDENTIFIER)
					throw IllegalArgumentException(
						"Disc is not bootable: unknown system \"${boot.bootSystemIdentifier}\""
					)
				val elTorito = ElToritoBootCatalogDescriptor.layout.read(boot.bootData.reading())
				val initialEntry = computer.discStream.use { disc ->
					val reading = ReadingByteBuffer(
						Channels.newChannel(disc),
						ByteBuffer.allocate(32768),
						null
					)
					disc.skip((elTorito.bootCatalogOffset * primary.logicalBlockSize).toLong())
					val validation = ElToritoBootCatalogValidationEntry.layout.read(reading)
					if (validation.platform.enum != ElToritoPlatform.X86)
						throw IllegalArgumentException("Disc is not bootable: unknown platform ${validation.platform}")
					ElToritoBootCatalogInitialEntry.layout.read(reading)
				}
				val discStart = (initialEntry.loadRBA * primary.logicalBlockSize).toULong()
				val memoryStart = (initialEntry.loadSegment * 0x10u).toULong()
				it.decoding.loadDiscIntoMemory(
					discStart,
					discStart + (initialEntry.sectorCount * primary.logicalBlockSize).toULong(),
					memoryStart
				)
				it.d.l = 0xE0u
				it.cs.rx = 0u
				it.ip.rx = memoryStart
			}
		}
		// (F000:FF54) INT 0x05 Print Screen Entry Point
		// TODO INT 05
	}
}