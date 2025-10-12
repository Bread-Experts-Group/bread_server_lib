package org.bread_experts_group.api.compile.pe

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.SeekableByteChannel

class PE32OptionalHeader private constructor(private val structure: Structure) {
	companion object {
		fun of(builder: Structure.() -> Unit): PE32OptionalHeader {
			val pe32 = Structure()
			builder(pe32)
			return PE32OptionalHeader(pe32)
		}
	}

	class Structure internal constructor() {
		var linkerVersionMajor: UByte = 0u
		var linkerVersionMinor: UByte = 0u
		var entryPoint: UInt = 0u
		var codeBase: UInt = 0u
		var dataBase: UInt = 0u
		var windowsSpecific: PE32WindowsOptionalHeader? = null
	}

	internal var codeSizePosition: Long = 0
	val windowsOptionalHeader: PE32WindowsOptionalHeader?
		get() = structure.windowsSpecific

	fun build(into: SeekableByteChannel) {
		val buffer = ByteBuffer.allocate(28)
		buffer.order(ByteOrder.LITTLE_ENDIAN)
		buffer.putShort(PE32Magic.PE_32.id.toShort())
		buffer.put(structure.linkerVersionMajor.toByte())
		buffer.put(structure.linkerVersionMinor.toByte())
		codeSizePosition = into.position() + buffer.position()
		buffer.putInt(0)
		buffer.putInt(0)
		buffer.putInt(0)
		buffer.putInt(structure.entryPoint.toInt())
		buffer.putInt(structure.codeBase.toInt())
		buffer.putInt(structure.dataBase.toInt())
		into.write(buffer.clear())
		structure.windowsSpecific?.build(into)
	}
}