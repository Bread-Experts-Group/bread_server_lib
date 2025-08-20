package org.bread_experts_group.computer

import org.bread_experts_group.computer.BinaryUtil.readBinary
import org.bread_experts_group.computer.disc.ISO9660BootRecord
import org.bread_experts_group.computer.disc.ISO9660Disc
import org.bread_experts_group.computer.disc.ISO9660PrimaryVolume
import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.io.BreadModPollingVirtualKeyboard
import org.bread_experts_group.computer.io.IODevice
import org.bread_experts_group.computer.io.ProgrammableInterruptTimer
import org.bread_experts_group.hex
import org.bread_experts_group.io.reader.ReadingByteBuffer
import java.io.InputStream
import java.net.URL
import java.nio.ByteBuffer
import java.nio.channels.Channels

/**
 * A Bread Mod computer.
 * @since 1.0.0
 * @see MemoryModule
 * @see Processor
 * @author Miko Elbrecht
 */
class Computer(
	val memory: List<MemoryModule>,
	val processor: Processor,
	val bios: BIOSProvider
) : SimulationSteppable {
	val keyboard: BreadModPollingVirtualKeyboard = BreadModPollingVirtualKeyboard()
	val ioMap: MutableMap<UInt, IODevice> = mutableMapOf(
		0x40u to ProgrammableInterruptTimer()
	)

	fun getIODevice(n: UInt): IODevice = this.ioMap[n] ?: run {
		println("Access to unknown I/O device ${hex(n)}")
		object : IODevice {
			override fun read(computer: Computer): UByte = 0u
			override fun write(computer: Computer, d: UByte) {}
		}
	}

	fun requestMemoryAt(address: ULong): UByte {
		this.memory.firstOrNull {
			val memoryAddress = (it.effectiveAddress ?: 0u)
			(memoryAddress..<(memoryAddress + it.capacity.toULong())).contains(address)
		}?.let {
			return it[(address - (it.effectiveAddress ?: 0u)).toInt()]
		}
		throw IndexOutOfBoundsException("Memory address out of bounds, $address")
	}

	fun setMemoryAt(address: ULong, data: UByte) {
		this.memory.firstOrNull {
			val memoryAddress = (it.effectiveAddress ?: 0u)
			(memoryAddress..<(memoryAddress + it.capacity.toULong())).contains(address)
		}?.let {
			(processor as IA32Processor).logger.info("SET ${hex(address)} TO ${hex(data)}")
			it[(address - (it.effectiveAddress ?: 0u)).toInt()] = data
			return
		}
		throw IndexOutOfBoundsException("Memory address out of bounds, $address while setting $data")
	}

	fun setMemoryAt32(address: ULong, value: UInt) {
		this.setMemoryAt(address, (value and 0xFFu).toUByte())
		this.setMemoryAt(address + 1u, ((value shr 8) and 0xFFu).toUByte())
		this.setMemoryAt(address + 2u, ((value shr 16) and 0xFFu).toUByte())
		this.setMemoryAt(address + 3u, ((value shr 24) and 0xFFu).toUByte())
	}

	fun setMemoryAt16(address: ULong, value: UShort) {
		val v = value.toUInt()
		this.setMemoryAt(address, (v and 0xFFu).toUByte())
		this.setMemoryAt(address + 1u, ((v shr 8) and 0xFFu).toUByte())
	}

	fun requestMemoryAt16(address: ULong): UShort {
		var offset = 0u
		return readBinary(2, { this.requestMemoryAt(address + offset).also { offset++ } }).toUShort()
	}

	fun requestMemoryAt32(address: ULong): UInt {
		var offset = 0u
		return readBinary(4, { this.requestMemoryAt(address + offset).also { offset++ } }).toUInt()
	}

	fun requestMemoryAt64(address: ULong): ULong {
		var offset = 0u
		return readBinary(8, { this.requestMemoryAt(address + offset).also { offset++ } }).toULong()
	}

	var discPrimaryVolume: ISO9660PrimaryVolume? = null
		private set
	var discBoot: ISO9660BootRecord? = null
		private set
	var disc: ISO9660Disc? = null
		private set
	var discURL: URL? = null
	val discStream: InputStream
		get() {
			if (this.discURL == null) throw UninitializedPropertyAccessException("Disc URL is not set")
			return discURL!!.openStream()
		}

	fun insertDisc(at: URL) {
		Channels.newChannel(at.openStream()).use {
			val reading = ReadingByteBuffer(
				it,
				ByteBuffer.allocate(32768),
				null
			)
			val newDisc = ISO9660Disc.layout.read(reading)
			val primary = newDisc.volumeDescriptors.firstNotNullOfOrNull { v -> v as? ISO9660PrimaryVolume }
				?: throw IllegalArgumentException("Disc is invalid: no primary volume")
			val bootRecord = newDisc.volumeDescriptors.firstNotNullOfOrNull { v -> v as? ISO9660BootRecord }
			this.disc = newDisc
			this.discPrimaryVolume = primary
			this.discBoot = bootRecord
		}
		this.discURL = at
	}

	val floppyURLs: Array<URL?> = arrayOfNulls(4)

	override fun reset() {
		this.memory.forEach { it.erase() }
		this.bios.initialize(this)
		this.processor.reset()
	}

	override fun step() {
		this.processor.step()
	}
}