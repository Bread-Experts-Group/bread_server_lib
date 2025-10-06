package org.bread_experts_group.api.secure.cryptography

import org.bread_experts_group.logging.ColoredHandler
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.Test

class CryptographySystemRandomTest {
	val system = CryptographySystemProvider.open()
	val logger = ColoredHandler.newLogger("tmp logger")

	@Test
	fun randomSystem() = assertDoesNotThrow {
		val rng = system.get(CryptographySystemFeatures.RANDOM_SYSTEM_PREFERRED)
		logger.info("System preferred: ${rng.nextBytes(256).toHexString()}")
		logger.info("System preferred: ${rng.nextByte()}")
		logger.info("System preferred: ${rng.nextShort()}")
		logger.info("System preferred: ${rng.nextInt()}")
		logger.info("System preferred: ${rng.nextLong()}")
	}

	@Test
	fun random() = assertDoesNotThrow {
		val rng = system.get(CryptographySystemFeatures.RANDOM)
		logger.info("Random: ${rng.nextBytes(256).toHexString()}")
		logger.info("Random: ${rng.nextByte()}")
		logger.info("Random: ${rng.nextShort()}")
		logger.info("Random: ${rng.nextInt()}")
		logger.info("Random: ${rng.nextLong()}")
	}

	@Test
	fun randomDSA() = assertDoesNotThrow {
		val rng = system.get(CryptographySystemFeatures.RANDOM_FIPS_186_2_DSA)
		logger.info("DSA: ${rng.nextBytes(256).toHexString()}")
		logger.info("DSA: ${rng.nextByte()}")
		logger.info("DSA: ${rng.nextShort()}")
		logger.info("DSA: ${rng.nextInt()}")
		logger.info("DSA: ${rng.nextLong()}")
	}
}