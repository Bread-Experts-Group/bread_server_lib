package org.bread_experts_group.coder.format.asn1

import org.bread_experts_group.coder.format.Parser
import org.bread_experts_group.coder.format.asn1.element.*
import java.io.InputStream
import java.math.BigInteger

class ASN1Parser(
	from: InputStream
) : Parser<Int, ASN1Element, InputStream>("Abstract Syntax Notation One", from) {
	override fun responsibleStream(of: ASN1Element): InputStream = of.data.inputStream()

	override fun readBase(): ASN1Element = ASN1Element(
		fqIn.read(),
		fqIn.readNBytes(fqIn.read())
	)

	init {
		addParser(1) { stream, _ -> ASN1Boolean(stream.read() == 0xFF) }
		addParser(2) { stream, _ -> ASN1Integer(BigInteger(stream.readAllBytes())) }
		addParser(3) { stream, _ -> ASN1BitString(stream.readAllBytes()) }
		addParser(4) { stream, _ -> ASN1OctetString(stream.readAllBytes()) }
		addParser(5) { stream, _ -> ASN1Null() }
		addParser(6) { stream, _ ->
			ASN1ObjectIdentifier(
				buildList {
					val firstByte = stream.read()
					add(firstByte / 40)
					add(firstByte % 40)

					var value = 0
					while (true) {
						val b = stream.read()
						if (b == -1) break
						value = (value shl 7) or (b and 0x7F)
						if ((b and 0x80) == 0) {
							add(value)
							value = 0
						}
					}
				}.toTypedArray()
			)
		}
		addParser(16) { stream, _ -> ASN1Set(ASN1Parser(stream).readAllParsed()) }
		addParser(17) { stream, _ -> ASN1Sequence(ASN1Parser(stream).readAllParsed()) }
		addParser(48) { stream, _ -> ASN1Sequence(ASN1Parser(stream).readAllParsed()) }
	}
}