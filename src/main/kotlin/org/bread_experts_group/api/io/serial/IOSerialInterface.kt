package org.bread_experts_group.api.io.serial

import org.bread_experts_group.api.CheckedImplementation
import org.bread_experts_group.api.NoFeatureAvailableException
import java.util.*

abstract class IOSerialInterface : CheckedImplementation {
	companion object {
		fun open(
			device: UByte,
			baudRate: UInt, dataBits: UByte, stopBits: UByte, parityScheme: IOSerialParityScheme
		): IOSerialInterface {
			val serial = ServiceLoader.load(IOSerialInterface::class.java)
				.filter { it.supported() }
				.minByOrNull { it.source } ?: throw NoFeatureAvailableException("Serial Interfaces")
			serial.configure(device, baudRate, dataBits, stopBits, parityScheme)
			return serial
		}
	}

	protected abstract fun configure(
		device: UByte,
		baudRate: UInt, dataBits: UByte, stopBits: UByte, parityScheme: IOSerialParityScheme
	)
}