package org.bread_experts_group.api.computer.ia32.bios

import org.bread_experts_group.api.computer.BIOSProvider
import org.bread_experts_group.api.computer.Computer
import org.bread_experts_group.api.computer.disc.el_torito.ElToritoBootCatalogDescriptor
import org.bread_experts_group.api.computer.disc.el_torito.ElToritoBootCatalogDescriptor.Companion.BOOT_SYSTEM_IDENTIFIER
import org.bread_experts_group.api.computer.disc.el_torito.ElToritoBootCatalogInitialEntry
import org.bread_experts_group.api.computer.disc.el_torito.ElToritoBootCatalogValidationEntry
import org.bread_experts_group.api.computer.disc.el_torito.ElToritoPlatform
import org.bread_experts_group.api.computer.ia32.IA32Processor
import org.bread_experts_group.api.computer.ia32.instruction.impl.InterruptReturn.Companion.BIOS_RETURN
import org.bread_experts_group.hex
import org.bread_experts_group.io.reader.ReadingByteBuffer
import org.bread_experts_group.io.reader.ReadingByteBuffer.Companion.reading
import java.nio.ByteBuffer
import java.nio.channels.Channels
import java.util.*

class StandardBIOS : BIOSProvider {
	val interrupts = ServiceLoader.load(StandardBIOSInterruptProvider::class.java)
		.groupBy { it.int }
		.onEach { (_, interrupts) ->
			interrupts.forEach { it.bios = this }
		}

	val teletype = interrupts
		.flatMap { it.value }
		.firstNotNullOf { it as? TeletypeOutput }

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
		val f1A = interrupts[0x1Au]!!
		processor.setHook(0xF000u, 0xFE6Eu) {
			val operation = f1A.firstOrNull { it.matches(processor) }
				?: throw IllegalArgumentException("0x1A no matching operation found; ${hex(processor.a.th)}")
			operation.handle(processor)
		}
		// (F000:E6F2) INT 0x19 Entry Point
		processor.computer.setMemoryAt32(0x0064u, 0xF000E6F2u)
		val f19 = interrupts[0x19u]!!
		processor.setHook(0xF000u, 0xE6F2u) {
			val operation = f19.firstOrNull { it.matches(processor) }
				?: throw IllegalArgumentException("0x19 no matching operation found")
			operation.handle(processor)
		}
		// (F000:EFD0) INT 0x17 Printer Services
		processor.computer.setMemoryAt32(0x005Cu, 0xF000EFD0u)
		val f17 = interrupts[0x17u]!!
		processor.setHook(0xF000u, 0xEFD0u) {
			val operation = f17.firstOrNull { it.matches(processor) }
				?: throw IllegalArgumentException("0x17 no matching operation found")
			operation.handle(processor)
		}
		// (F000:E82E) INT 0x16 Keyboard Services
		processor.computer.setMemoryAt32(0x0058u, 0xF000E82Eu)
		val f16 = interrupts[0x16u]!!
		processor.setHook(0xF000u, 0xE82Eu) {
			val operation = f16.firstOrNull { it.matches(processor) }
				?: throw IllegalArgumentException("0x16 no matching operation found")
			operation.handle(processor)
		}
		// (F000:F859) INT 0x15 System Services
		processor.computer.setMemoryAt32(0x0054u, 0xF000F859u)
		val f15 = interrupts[0x15u]!!
		processor.setHook(0xF000u, 0xF859u) {
			val operation = f15.firstOrNull { it.matches(processor) }
				?: throw IllegalArgumentException("0x15 no matching operation found")
			operation.handle(processor)
		}
		// (F000:E739) INT 0x14 Serial
		processor.computer.setMemoryAt32(0x0050u, 0xF000E739u)
		val f14 = interrupts[0x14u]!!
		processor.setHook(0xF000u, 0xE739u) {
			val operation = f14.firstOrNull { it.matches(processor) }
				?: throw IllegalArgumentException("0x14 no matching operation found")
			operation.handle(processor)
		}
		// (F000:EC59) INT 0x13 Floppy
		processor.computer.setMemoryAt32(0x004Cu, 0xF000EC59u)
		val f13 = interrupts[0x13u]!!
		processor.setHook(0xF000u, 0xEC59u) {
			val operation = f13.firstOrNull { it.matches(processor) }
				?: throw IllegalArgumentException("0x13 no matching operation found")
			operation.handle(processor)
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
			equipment = equipment or ((computer.floppies.size - 1) shl 6).toUInt()
			processor.a.tex = equipment
		}
		// (F000:F065) INT 0x10 Video
		processor.computer.setMemoryAt32(0x0040u, 0xF000F065u)
		val f10 = interrupts[0x10u]!!
		processor.setHook(0xF000u, 0xF065u) {
			val operation = f10.firstOrNull { it.matches(processor) }
				?: throw IllegalArgumentException("0x10 no matching operation found")
			operation.handle(processor)
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
			val floppy = computer.floppies.getOrNull(0)
			if (floppy != null) {
				it.decoding.loadFloppyIntoMemory(floppy.first, 0u, 16384u, 0x7C00u)
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
	}
}