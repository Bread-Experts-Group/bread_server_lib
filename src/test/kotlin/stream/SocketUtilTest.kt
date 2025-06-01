package stream

import org.bread_experts_group.stream.read16
import org.bread_experts_group.stream.read24
import org.bread_experts_group.stream.read32
import org.bread_experts_group.stream.read64
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SocketUtilTest {
	@Test
	fun read16() {
		val testStream = byteArrayOf(0x01, 0x02).inputStream()
		assertEquals(0x0102, testStream.read16())
	}

	@Test
	fun read24() {
		val testStream = byteArrayOf(0x01, 0x02, 0x03).inputStream()
		assertEquals(0x010203, testStream.read24())
	}

	@Test
	fun read32() {
		val testStream = byteArrayOf(0x01, 0x02, 0x03, 0x04).inputStream()
		assertEquals(0x01020304, testStream.read32())
	}

	@OptIn(ExperimentalUnsignedTypes::class)
	@Test
	fun read64() {
		val testStream = byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08).inputStream()
		assertEquals(0x0102030405060708, testStream.read64())
		val testStreamR = ubyteArrayOf(
			0xFFu, 0xFEu, 0xFDu, 0xFCu, 0xFBu, 0xFAu, 0xF9u, 0xF8u
		).toByteArray().inputStream()
		assertEquals(
			0xFFFEFDFCFBFAF9F8u.toLong(),
			testStreamR.read64()
		)
	}

	@Test
	fun write16() {
	}

	@Test
	fun write24() {
	}

	@Test
	fun write32() {
	}

	@Test
	fun write64() {
	}
}