package org.bread_experts_group.stream

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream

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
		val output = ByteArrayOutputStream()
		output.write16(0xCAFE)
		assertEquals(0xCAFE.toShort(), output.toByteArray().inputStream().read16())
	}

	@Test
	fun write24() {
		val output = ByteArrayOutputStream()
		output.write24(0xBACAFE)
		assertEquals(0xBACAFE, output.toByteArray().inputStream().read24())
	}

	@Test
	fun write32() {
		val output = ByteArrayOutputStream()
		output.write32(0xBABECAFE)
		assertEquals(0xBABECAFE.toInt(), output.toByteArray().inputStream().read32())
	}

	@Test
	fun write64() {
		val output = ByteArrayOutputStream()
		output.write64(0x4400FF00BABECAFE)
		assertEquals(0x4400FF00BABECAFE, output.toByteArray().inputStream().read64())
	}

	@Test
	fun write64u() {
		val output = ByteArrayOutputStream()
		output.write64u(0xFF00FF00BABECAFEu)
		assertEquals(0xFF00FF00BABECAFEu, output.toByteArray().inputStream().read64u())
	}
}