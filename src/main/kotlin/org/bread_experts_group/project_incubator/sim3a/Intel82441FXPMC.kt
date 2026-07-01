package org.bread_experts_group.project_incubator.sim3a

import java.io.File
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.nio.ByteBuffer
import java.nio.ByteOrder

@Suppress("PropertyName")
class Intel82441FXPMC(
	val busses: Map<Int, Map<Int, Map<Int, PCIFunction>>>,
	val memory: MemorySegment
) {
	companion object {
		/**
		 * CONFIGURATION ADDRESS REGISTER
		 *
		 * Accessible as a DWORD (32)
		 */
		const val CONFADD = 0x0CF8

		/**
		 * CONFIGURATION DATA REGISTER
		 *
		 * Accessible as a BYTE (8), WORD (16), or DWORD (32)
		 */
		const val CONFDATA = 0x0CFC

		const val VID_DEFAULT: Short = 0x8086.toShort()
		const val DID_DEFAULT: Short = 0x1237
		const val CLASSC_DEFAULT = 0x060000
	}

	var CONE = false
	var BUSNUM = 0 // Bus number 0 refers to either the PMC itself or the bus attached to the PMC.
	var DEVNUM = 0 // The PMC is always Device Number 0.
	var FUNCNUM = 0 // The PMC does not have a Function Number other than 0, access will cause master abort
	var REGNUM = 0

	val pmcConfigurationSpace: ByteBuffer = ByteBuffer.allocate(0xFF).also {
		it.order(ByteOrder.LITTLE_ENDIAN)
		it.putShort(VID_DEFAULT) // RO
		it.putShort(DID_DEFAULT) // RO
		it.putShort(0x0006) // RW
		it.putShort(0x0280) // RO RWC
		it.put(0x00) // RO
		it.putInt(CLASSC_DEFAULT) // RO
		it.position(0x0D)
		it.put(0x00) // RW
		it.put(0x00) // RO
		it.put(0x00) // RW
		it.position(0x50)
		it.putShort(0x00) // RW RO
		it.put(0x00) // RW
		it.put(0x80.toByte()) // RW
		it.put(0x00) // RW
		it.putShort(0x0000) // RW
		it.put(0x01) // RW
		it.put(0x10) // RW
		it.putInt(0x00000000) // RW
		it.putInt(0x00000000) // RW
		it.position(0x60)
		it.putInt(0x0001) // RW
		it.putInt(0x0001) // RW
		it.position(0x68)
		it.put(0x00) // RW
		it.position(0x70)
		it.put(0x00) // RW
		it.put(0x10) // RW
		it.put(0x02) // RW
		it.position(0x90)
		it.put(0x00) // RW
		it.put(0x00) // RO RWC
		it.position(0x93)
		it.put(0x00) // RW
	}

	val configuration = mutableMapOf<PCIFunction, ByteBuffer>()

	init {
		busses.forEach { (_, bus) ->
			bus.forEach { (_, device) ->
				device.forEach { (_, function) ->
					val configuration = ByteBuffer.allocate(0xFF)
					configuration.order(ByteOrder.LITTLE_ENDIAN)
					configuration.putShort(0x1234) // RO
					configuration.putShort(0x1111) // RO
					configuration.putShort(0x0006) // RW
					configuration.putShort(0x0280) // RO RWC
					configuration.put(0x00) // RO
					configuration.putInt(0x030000) // RO
					configuration.position(0x3C)
					configuration.put(0x01)
					configuration.put(0x1)
					this.configuration[function] = configuration
				}
			}
		}
	}

	fun read(port: Int): Int = when (port) {
		in CONFDATA..CONFDATA + 3 -> {
			val offset = REGNUM + when (port) {
				CONFDATA -> 0
				CONFDATA + 1 -> 1
				CONFDATA + 2 -> 2
				CONFDATA + 3 -> 3
				else -> throw IllegalStateException()
			}
			if (BUSNUM == 0) {
				if (DEVNUM > 20) {
					pmcConfigurationSpace.putInt(0x06, pmcConfigurationSpace.getInt(0x06) or (1 shl 13))
					return -1
				}
				if (DEVNUM == 0) {
					if (FUNCNUM != 0) TODO("Master abort")
					return pmcConfigurationSpace.getInt(offset)
				}
				val device = busses[0]!![DEVNUM]
				if (device == null) {
					pmcConfigurationSpace.putInt(0x06, pmcConfigurationSpace.getInt(0x06) or (1 shl 13))
					return -1
				}
				val function = device[FUNCNUM] ?: TODO("Master abort")
				println("*BSL* PCI read from $BUSNUM/$DEVNUM/$FUNCNUM@${offset.toHexString()}")
				configuration[function]!!.getInt(offset)
			} else {
				val bus = busses[BUSNUM] ?: TODO("Master abort")
				val device = bus[DEVNUM] ?: TODO("Master abort")
				val function = device[FUNCNUM] ?: TODO("Master abort")
				println(configuration[function])
				TODO("OK")
			}
		}

		0xCF8 -> (if (CONE) 1 shl 31 else 0) or (BUSNUM shl 16) or (DEVNUM shl 11) or (FUNCNUM shl 8) or REGNUM

		else -> TODO(port.toHexString())
	}

	fun write(port: Int, value: Int) {
		when (port) {
			in CONFDATA..CONFDATA + 3 -> {
				if (BUSNUM == 0) {
					if (DEVNUM > 20) pmcConfigurationSpace.putInt(
						0x06,
						pmcConfigurationSpace.getInt(0x06) or (1 shl 13)
					)
					if (DEVNUM == 0 && FUNCNUM != 0) TODO("Master abort")
				}
				val bus = busses[BUSNUM] ?: TODO("Master abort")
				val device = bus[DEVNUM]
				if (device == null) {
					pmcConfigurationSpace.putInt(
						0x06,
						pmcConfigurationSpace.getInt(0x06) or (1 shl 13)
					)
					return
				}
				val function = device[FUNCNUM] ?: TODO("Master abort")
				val offset = REGNUM + when (port) {
					CONFDATA -> 0
					CONFDATA + 1 -> 1
					CONFDATA + 2 -> 2
					CONFDATA + 3 -> 3
					else -> throw IllegalStateException()
				}
				println("*BSL* PCI write to $BUSNUM/$DEVNUM/$FUNCNUM@${offset.toHexString()} (${value.toHexString()})")
				if (offset == 0x30) {
					var pos = 31
					var mask = 0
					var remainder = vgabios.byteSize()
					while (remainder != 0L) {
						remainder = remainder ushr 1
						mask = mask or (1 shl (pos--))
					}
					val newERRValue = (value and (mask or 1))
					if (newERRValue and 1 == 1) TODO("Load")
					configuration[function]!!.putInt(0x30, newERRValue)
					return
				}
				if (offset == 0x3C) {
					configuration[function]!!.putInt(0x3C, value)
					return
				}
			}

			CONFADD -> {
				CONE = value ushr 31 == 1
				BUSNUM = ((value ushr 16) and 0b11111111)
				DEVNUM = ((value ushr 11) and 0b11111)
				FUNCNUM = ((value ushr 8) and 0b111)
				REGNUM = (value and 0b11111100)
			}

			else -> TODO(port.toHexString())
		}
	}

	val vgabios: MemorySegment = File("C:\\Users\\Adenosine3Phosphate\\Downloads\\vgabios-stdvga.bin").readBytes().let {
		val memory = Arena.global().allocate(it.size.toLong())
		MemorySegment.copy(
			it, 0,
			memory, ValueLayout.JAVA_BYTE, 0,
			it.size
		)
		memory
	}

	// TODO: bios.bin-1.17.0
	// TODO: BIOS-bochs-latest
	val bios: MemorySegment = File("C:\\Users\\Adenosine3Phosphate\\Downloads\\bios.bin-1.17.0").readBytes().let {
		val memory = Arena.global().allocate(0x100000, 4096)
		MemorySegment.copy(
			it, 0,
			memory, ValueLayout.JAVA_BYTE, memory.byteSize() - it.size,
			it.size
		)
		memory
	}
}