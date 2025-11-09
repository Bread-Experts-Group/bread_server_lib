package org.bread_experts_group.org.bread_experts_group.api.serial

import org.bread_experts_group.api.io.serial.IOSerialInterface
import org.bread_experts_group.api.io.serial.IOSerialParityScheme
import org.bread_experts_group.logging.ColoredHandler
import org.junit.jupiter.api.assertDoesNotThrow
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import kotlin.test.Test

class SerialInterfaceTest {
	val logger = ColoredHandler.newLogger("tmp logger")

	@Test
	fun read512() = assertDoesNotThrow {
		val serial = IOSerialInterface.open(
			10u,
			9600u, 8u, 1u, IOSerialParityScheme.NO_PARITY
		)
		val testBuffer = Arena.ofAuto().allocate(512)
//		serial.read(testBuffer, 512)
		val string = ByteArray(512)
		MemorySegment.copy(testBuffer, ValueLayout.JAVA_BYTE, 0, string, 0, 512)
		logger.info(String(string, Charsets.US_ASCII))
	}
}