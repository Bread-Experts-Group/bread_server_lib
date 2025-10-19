package org.bread_experts_group.api.compile.pe

import org.bread_experts_group.Flaggable.Companion.raw
import org.bread_experts_group.MappedEnumeration
import org.bread_experts_group.api.compile.mzdos.MZDOSFile
import org.bread_experts_group.normalize
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.SeekableByteChannel
import java.time.ZonedDateTime
import java.util.*

class PEFile private constructor(private val structure: FileStructure) {
	companion object {
		fun of(builder: FileStructure.() -> Unit): PEFile {
			val pe = FileStructure()
			builder(pe)
			return PEFile(pe)
		}
	}

	class FileStructure internal constructor() {
		var mz: MZDOSFile? = null
		var machineType: MappedEnumeration<UShort, PEMachineTypes> = MappedEnumeration(
			PEMachineTypes.IMAGE_FILE_MACHINE_UNKNOWN
		)
		var sections: List<PESection> = emptyList()
		var createdTime: ZonedDateTime = ZonedDateTime.now()
		var characteristics: EnumSet<PECharacteristics> = EnumSet.noneOf(PECharacteristics::class.java)
		var optionalHeader: PE32OptionalHeader? = null
	}

	fun build(into: SeekableByteChannel) {
		var buffer = ByteBuffer.allocate(8)
		buffer.order(ByteOrder.LITTLE_ENDIAN)
		structure.mz?.let {
			it.build(into)
			buffer.putInt((into.position() + 4).toUInt().toInt())
			buffer.put('P'.code.toByte())
			buffer.put('E'.code.toByte())
			buffer.putShort(0)
			into.write(buffer.clear())
		}
		val savedPosition = into.position()
		buffer = ByteBuffer.allocate(20)
		buffer.order(ByteOrder.LITTLE_ENDIAN)
		buffer.putShort(structure.machineType.raw.toShort())
		buffer.putShort(structure.sections.size.toShort())
		buffer.putInt(structure.createdTime.toEpochSecond().toUInt().toInt())
		buffer.putInt(0)
		buffer.putInt(0)
		val optionalHeaderOffset = buffer.position()
		buffer.putShort(0)
		buffer.putShort(structure.characteristics.raw().toShort())
		into.position(savedPosition + 20)
		structure.optionalHeader?.build(into)
		val afterOHWrite = into.position()
		buffer.position(optionalHeaderOffset)
		buffer.putShort((afterOHWrite - (savedPosition + 20)).toShort())
		into.position(savedPosition)
		into.write(buffer.clear())
		into.position(afterOHWrite)
		structure.sections.forEach {
			it.build(into)
		}
		val headerEnd = into.position()
		structure.optionalHeader?.let { optionalHeader ->
			structure.optionalHeader?.windowsOptionalHeader?.let { windowsHeader ->
				buffer = ByteBuffer.allocate(8)
				buffer.order(ByteOrder.LITTLE_ENDIAN)
				var sizeOfCode = 0
				var sizeOfInitData = 0
				var sizeOfUnInitData = 0
				structure.sections.forEach { section ->
					val rawData = section.rawData ?: return@forEach
					val dataPosition = normalize(into.position().toInt(), windowsHeader.fileAlignment.toInt())
					into.position(dataPosition.toLong())
					into.write(ByteBuffer.wrap(rawData))
					val afterWrite = into.position()
					into.position(section.sizeOfRawDataPosition)
					val sizeOfRawData = normalize(rawData.size, windowsHeader.fileAlignment.toInt())
					buffer.putInt(sizeOfRawData)
					buffer.putInt(dataPosition)
					into.write(buffer.clear())
					buffer.clear()
					if (rawData.size < sizeOfRawData) {
						into.position((dataPosition.toLong() + sizeOfRawData) - 1)
						into.write(ByteBuffer.allocate(1))
					}
					into.position(afterWrite)
					if (section.characteristics.contains(PESectionCharacteristics.IMAGE_SCN_CNT_CODE))
						sizeOfCode += normalize(rawData.size, windowsHeader.fileAlignment.toInt())
					if (section.characteristics.contains(PESectionCharacteristics.IMAGE_SCN_CNT_INITIALIZED_DATA))
						sizeOfInitData += normalize(rawData.size, windowsHeader.fileAlignment.toInt())
					if (section.characteristics.contains(PESectionCharacteristics.IMAGE_SCN_CNT_UNINITIALIZED_DATA))
						sizeOfUnInitData += normalize(rawData.size, windowsHeader.fileAlignment.toInt())
				}
				buffer.clear()
				into.position(windowsHeader.sizeOfImagePosition)
				buffer.putInt(normalize(0x10000, windowsHeader.sectionAlignment.toInt()))
				buffer.putInt(normalize(headerEnd.toInt(), windowsHeader.fileAlignment.toInt()))
				into.write(buffer.clear())
				into.position(optionalHeader.codeSizePosition)
				buffer = ByteBuffer.allocate(12)
				buffer.order(ByteOrder.LITTLE_ENDIAN)
				buffer.putInt(sizeOfCode)
				buffer.putInt(sizeOfInitData)
				buffer.putInt(sizeOfUnInitData)
				into.write(buffer.clear())
			}
		}
	}
}