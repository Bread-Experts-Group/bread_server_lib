package org.bread_experts_group.api.compile.pe

import org.bread_experts_group.generic.Flaggable.Companion.raw
import org.bread_experts_group.generic.MappedEnumeration
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.SeekableByteChannel
import java.util.*

class PE32WindowsOptionalHeader private constructor(private val structure: Structure) {
	companion object {
		fun of(builder: Structure.() -> Unit): PE32WindowsOptionalHeader {
			val pe32 = Structure()
			builder(pe32)
			return PE32WindowsOptionalHeader(pe32)
		}
	}

	class Structure internal constructor() {
		var imageBase: UInt = 0x00400000u
		var sectionAlignment: UInt = 512u
		var fileAlignment: UInt = 512u
		var majorOperatingSystemVersion: UShort = 0u
		var minorOperatingSystemVersion: UShort = 0u
		var majorImageVersion: UShort = 0u
		var minorImageVersion: UShort = 0u
		var majorSubsystemVersion: UShort = 0u
		var minorSubsystemVersion: UShort = 0u
		var subsystem: MappedEnumeration<UShort, PEWindowsSubsystem> = MappedEnumeration(
			PEWindowsSubsystem.IMAGE_SUBSYSTEM_UNKNOWN
		)
		var dllCharacteristics: EnumSet<PEDLLCharacteristics> = EnumSet.noneOf(PEDLLCharacteristics::class.java)
		var sizeOfStackReserve: UInt = 0u
		var sizeOfStackCommit: UInt = 0u
		var sizeOfHeapReserve: UInt = 0u
		var sizeOfHeapCommit: UInt = 0u
	}

	internal var sizeOfImagePosition: Long = 0
	internal val fileAlignment: UInt
		get() = structure.fileAlignment
	internal val sectionAlignment: UInt
		get() = structure.sectionAlignment

	fun build(into: SeekableByteChannel) {
		val buffer = ByteBuffer.allocate(196)
		buffer.order(ByteOrder.LITTLE_ENDIAN)
		buffer.putInt(structure.imageBase.toInt())
		buffer.putInt(structure.sectionAlignment.toInt())
		buffer.putInt(structure.fileAlignment.toInt())
		buffer.putShort(structure.majorOperatingSystemVersion.toShort())
		buffer.putShort(structure.minorOperatingSystemVersion.toShort())
		buffer.putShort(structure.majorImageVersion.toShort())
		buffer.putShort(structure.minorImageVersion.toShort())
		buffer.putShort(structure.majorSubsystemVersion.toShort())
		buffer.putShort(structure.minorSubsystemVersion.toShort())
		buffer.putInt(0)
		sizeOfImagePosition = into.position() + buffer.position()
		buffer.putInt(0)
		buffer.putInt(0)
		buffer.putInt(0) // TODO CheckSum
		buffer.putShort(structure.subsystem.raw.toShort())
		buffer.putShort(structure.dllCharacteristics.raw().toShort())
		buffer.putInt(structure.sizeOfStackReserve.toInt())
		buffer.putInt(structure.sizeOfStackCommit.toInt())
		buffer.putInt(structure.sizeOfHeapReserve.toInt())
		buffer.putInt(structure.sizeOfHeapCommit.toInt())
		buffer.putInt(0)
		buffer.putInt(16) // TODO: NumberOfRvaAndSizes
		repeat(16) {
			buffer.putInt(0)
			buffer.putInt(0)
		}
		into.write(buffer.clear())
	}
}