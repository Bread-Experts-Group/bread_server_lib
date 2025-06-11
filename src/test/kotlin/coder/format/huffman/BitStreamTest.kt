package coder.format.huffman

import org.bread_experts_group.coder.format.huffman.BitInputStream
import org.bread_experts_group.coder.format.huffman.BitOutputStream
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream

class BitStreamTest {
	@Test
	fun testWrite() {
		val destination = ByteArrayOutputStream()
		val output = BitOutputStream(destination)
		output.writeBit(true)
		output.writeBit(true)
		output.writeBit(true)
		output.writeBit(false)
		output.writeBit(true)
		output.writeBit(false)
		output.writeBit(true)
		output.writeBit(true)

		output.writeBit(true)
		output.writeBit(false)
		output.writeBit(true)
		output.flush()
		val input = BitInputStream(destination.toByteArray().inputStream())
		Assertions.assertEquals(true, input.nextBit())
		Assertions.assertEquals(true, input.nextBit())
		Assertions.assertEquals(true, input.nextBit())
		Assertions.assertEquals(false, input.nextBit())
		Assertions.assertEquals(true, input.nextBit())
		Assertions.assertEquals(false, input.nextBit())
		Assertions.assertEquals(true, input.nextBit())
		Assertions.assertEquals(true, input.nextBit())

		Assertions.assertEquals(true, input.nextBit())
		Assertions.assertEquals(false, input.nextBit())
		Assertions.assertEquals(true, input.nextBit())
	}
}