package org.bread_experts_group.api.serial

import org.bread_experts_group.api.CheckedImplementation
import java.lang.foreign.MemorySegment
import java.nio.channels.ReadableByteChannel
import java.util.*

abstract class SerialInterface : CheckedImplementation, ReadableByteChannel {
	companion object {
		fun open(
			device: UByte,
			baudRate: UInt, dataBits: UByte, stopBits: UByte, parityScheme: SerialParityScheme
		): SerialInterface {
			val serial = ServiceLoader.load(SerialInterface::class.java)
				.filter { it.supported() }
				.minByOrNull { it.source }!!
			serial.configure(device, baudRate, dataBits, stopBits, parityScheme)
			return serial
		}
	}

	protected abstract fun configure(
		device: UByte,
		baudRate: UInt, dataBits: UByte, stopBits: UByte, parityScheme: SerialParityScheme
	)

	abstract fun read(b: MemorySegment, length: Long = b.byteSize()): Long
}