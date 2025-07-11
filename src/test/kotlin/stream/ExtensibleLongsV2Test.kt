package org.bread_experts_group.stream

import org.bread_experts_group.logging.ColoredHandler
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.util.logging.Logger
import kotlin.math.abs
import kotlin.math.max
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

class ExtensibleLongsV2Test {
	// TODO update the langs
	val logger: Logger = ColoredHandler.newLoggerResourced("tests.private_socket_util")
	val sampleSize: Int = 10000

	var longWriteCalls: Int = 0
	fun OutputStream.writeExtensibleLongCounted(value: Long) {
		this.writeExtensibleLongV2L(value)
		longWriteCalls++
	}

	private fun boundedWriteTest(range: LongRange) {
		longWriteCalls = 0
		val written = mutableListOf<Long>()
		val randomStream = ByteArrayOutputStream()
		var writeTime = Duration.ZERO
		repeat(sampleSize) {
			val toWrite = Random.nextLong(range.start, range.last)
			written.add(toWrite)
			writeTime += measureTime { randomStream.writeExtensibleLongCounted(toWrite) }
		}
		val randomStreamInput = randomStream.toByteArray().inputStream()
		var readTime = Duration.ZERO
		written.forEach {
			val t = measureTimedValue { randomStreamInput.readExtensibleLongV2L() }
			assertEquals(it, t.value)
			readTime += t.duration
		}
		var worstCase = 1
		var remainder = max(abs(range.start), abs(range.last)) ushr 6
		while (remainder != 0L) {
			remainder = remainder ushr 7
			worstCase++
		}
		logger.info {
			"Random tests [$range]: ${randomStream.size()} bytes | " +
					"Unpacked: ${longWriteCalls * Long.SIZE_BYTES} bytes | " +
					"Worst case: ${longWriteCalls * worstCase} bytes | " +
					"Read Time [$readTime] | " +
					"Write Time [$writeTime]"
		}
	}

	@Test
	fun extensibleLong() {
//		logger.info { "Long Algorithm worst case: ${(ceil((Long.SIZE_BITS - 6) / 7.0) + 1).toLong()} bytes" }
		val stream = ByteArrayOutputStream()
		assertDoesNotThrow {
			stream.writeExtensibleLongCounted(Long.MIN_VALUE)
			stream.writeExtensibleLongCounted(-1000000000000000)
			stream.writeExtensibleLongCounted(-1000000000)
			stream.writeExtensibleLongCounted(-1000)
			stream.writeExtensibleLongCounted(-1)
			stream.writeExtensibleLongCounted(0)
			stream.writeExtensibleLongCounted(1)
			stream.writeExtensibleLongCounted(1000)
			stream.writeExtensibleLongCounted(1000000000)
			stream.writeExtensibleLongCounted(1000000000000000)
			stream.writeExtensibleLongCounted(Long.MAX_VALUE)
		}
		logger.info {
			"Fixed tests: ${stream.size()} bytes, unpacked: ${longWriteCalls * Long.SIZE_BYTES} bytes"
		}
		val input = stream.toByteArray().inputStream()
		assertEquals(Long.MIN_VALUE, input.readExtensibleLongV2L())
		assertEquals(-1000000000000000, input.readExtensibleLongV2L())
		assertEquals(-1000000000, input.readExtensibleLongV2L())
		assertEquals(-1000, input.readExtensibleLongV2L())
		assertEquals(-1, input.readExtensibleLongV2L())
		assertEquals(0, input.readExtensibleLongV2L())
		assertEquals(1, input.readExtensibleLongV2L())
		assertEquals(1000, input.readExtensibleLongV2L())
		assertEquals(1000000000, input.readExtensibleLongV2L())
		assertEquals(1000000000000000, input.readExtensibleLongV2L())
		assertEquals(Long.MAX_VALUE, input.readExtensibleLongV2L())
		assertEquals(0, input.available())
		boundedWriteTest(Long.MIN_VALUE..Long.MAX_VALUE)
		boundedWriteTest(-1000000000000000000..1000000000000000000)
		boundedWriteTest(-1000000000000000..1000000000000000)
		boundedWriteTest(-1000000000L..1000000000)
		boundedWriteTest(-1000L..1000)
		boundedWriteTest(-1L..1)
	}
}