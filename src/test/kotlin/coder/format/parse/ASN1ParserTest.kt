package org.bread_experts_group.coder.format.parse

import org.bread_experts_group.coder.format.parse.asn1.ASN1Parser
import org.bread_experts_group.dumpLog
import org.bread_experts_group.logging.ColoredHandler
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.io.InputStream

class ASN1ParserTest {
	val logger = ColoredHandler.Companion.newLogger("tmp asn1")
	val testFile: InputStream? = this::class.java.classLoader.getResourceAsStream(
		"coder/format/crypto/x509/32k-rsa-example-cert.der"
	)
	val testParser = ASN1Parser().setInput(testFile!!).throwOnUnknown()

	@Test
	fun getNext() = assertDoesNotThrow {
		testParser.dumpLog(logger)
	}
}