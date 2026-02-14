package org.bread_experts_group.api.compile.pe

import org.bread_experts_group.generic.Flaggable.Companion.raw
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.SeekableByteChannel
import java.util.*

class PESection private constructor(private val structure: Structure) {
	companion object {
		fun of(builder: Structure.() -> Unit): PESection {
			val pe = Structure()
			builder(pe)
			return PESection(pe)
		}
	}

	class Structure internal constructor() {
		var name: ByteArray = ByteArray(8)
			private set

		fun setName(name: String) {
			val encoded = name.toByteArray(Charsets.UTF_8)
			if (encoded.size > 8) throw IllegalArgumentException("Name must be under 8 bytes long.")
			this.name = if (encoded.size < 8) encoded + ByteArray(8 - encoded.size) else encoded
		}

		var virtualSize: UInt = 0u
		var virtualAddress: UInt = 0u
		var rawData: ByteArray? = null
		var pointerToRelocations: UInt = 0u
		var numberOfRelocations: UShort = 0u
		var characteristics: EnumSet<PESectionCharacteristics> = EnumSet.noneOf(PESectionCharacteristics::class.java)
	}

	internal var sizeOfRawDataPosition: Long = 0
	internal val rawData: ByteArray?
		get() = structure.rawData
	internal val characteristics: EnumSet<PESectionCharacteristics>
		get() = structure.characteristics

	fun build(into: SeekableByteChannel) {
		val buffer = ByteBuffer.allocate(40)
		buffer.order(ByteOrder.LITTLE_ENDIAN)
		buffer.put(structure.name)
		buffer.putInt(structure.virtualSize.toInt())
		buffer.putInt(structure.virtualAddress.toInt())
		sizeOfRawDataPosition = into.position() + buffer.position()
		buffer.putInt(0)
		buffer.putInt(0)
		buffer.putInt(structure.pointerToRelocations.toInt())
		buffer.putInt(0)
		buffer.putShort(structure.numberOfRelocations.toShort())
		buffer.putShort(0)
		buffer.putInt(structure.characteristics.raw().toInt())
		into.write(buffer.clear())
	}
}