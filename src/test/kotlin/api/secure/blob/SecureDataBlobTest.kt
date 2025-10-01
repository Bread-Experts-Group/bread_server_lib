package org.bread_experts_group.api.secure.blob

import org.bread_experts_group.logging.ColoredHandler
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class SecureDataBlobTest {
	val logger = ColoredHandler.newLogger("TMP logger")

	@Test
	fun test() {
		val sdb = SecureDataBlobProvider.open()
		val lpe = sdb.get(SecureDataBlobFeatures.LOCAL_PROCESS_ENCRYPTED, false)
		assertThrows<IllegalStateException> { sdb[0] }
		lpe.initialize(200)
		logger.info(sdb.toString())
		assertDoesNotThrow { sdb[0L..<200L] = 0 }
		assert(sdb[0L..<200L].around.all { it == 0.toByte() }) { "Memory was not initialized to 0" }
		assertDoesNotThrow { sdb[0] = 3 }
		assertEquals(3.toByte(), sdb[0])
		assertThrows<IndexOutOfBoundsException> { sdb[200] }
		assertThrows<IndexOutOfBoundsException> { sdb[200] = 1 }
	}
}