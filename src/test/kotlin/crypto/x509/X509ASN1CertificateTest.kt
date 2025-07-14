package org.bread_experts_group.crypto.x509

import org.bread_experts_group.coder.format.parse.asn1.ASN1Parser
import org.bread_experts_group.dumpLog
import org.bread_experts_group.logging.ColoredHandler
import org.bread_experts_group.testBase
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.security.KeyPairGenerator
import java.time.Duration
import kotlin.io.path.writeBytes

class X509ASN1CertificateTest {
	val logger = ColoredHandler.newLogger("tmp x509")

	@Test
	fun getX509() = assertDoesNotThrow {
		val testPair = KeyPairGenerator.getInstance("EC").genKeyPair()
		val x509 = X509ASN1Certificate(
			testPair,
			Duration.ofDays(5L)
		).x509
		ByteArrayOutputStream().use {
			x509.write(it)
			val data = it.toByteArray()
			testBase.resolve("x509.crt").writeBytes(data)
			ASN1Parser().setInput(data.inputStream()).throwOnUnknown().dumpLog(logger)
		}
	}
}