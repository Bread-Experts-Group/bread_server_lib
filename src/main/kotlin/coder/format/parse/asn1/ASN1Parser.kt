package org.bread_experts_group.coder.format.parse.asn1

import org.bread_experts_group.coder.Mappable.Companion.id
import org.bread_experts_group.coder.format.parse.CodingCompoundThrowable
import org.bread_experts_group.coder.format.parse.Parser
import org.bread_experts_group.coder.format.parse.asn1.element.*
import org.bread_experts_group.stream.read16ui
import java.io.InputStream
import java.math.BigInteger

class ASN1Parser : Parser<ASN1ElementIdentifier, ASN1Element, InputStream>(
	"Abstract Syntax Notation One",
	InputStream::class
) {
	override fun responsibleStream(of: ASN1Element): InputStream = of.data.inputStream()

	override fun readBase(compound: CodingCompoundThrowable): ASN1Element = ASN1Element(
		fqIn.read().let {
			ASN1ElementIdentifier(
				ASN1ElementClass.entries.id(it shr 6).enum,
				ASN1ElementConstruction.entries.id((it shr 5) and 0b1).enum,
				ASN1Tag.entries.id(it and 0b11111).enum
			)
		},
		fqIn.readNBytes(
			fqIn.read().let {
				if (it < 0x80) it
				else when (val bytes = it and (0x80).inv()) {
					1 -> fqIn.read()
					2 -> fqIn.read16ui()
					else -> throw IllegalArgumentException(bytes.toString())
				}
			}
		)
	)

	init {
		addPredicateParser({ it.tag.tag == ASN1Tag.BOOLEAN }) { stream, _, _ -> ASN1Boolean(stream.read() == 0xFF) }
		addPredicateParser({ it.tag.tag == ASN1Tag.INTEGER }) { _, element, _ -> ASN1Integer(BigInteger(element.data)) }
		addPredicateParser({ it.tag.tag == ASN1Tag.UTF8_STRING }) { stream, element, _ ->
			ASN1String(element.tag, stream.readAllBytes().toString(Charsets.UTF_8))
		}
		addPredicateParser({ it.tag.tag == ASN1Tag.PRINTABLE_STRING }) { stream, element, _ ->
			ASN1String(element.tag, stream.readAllBytes().toString(Charsets.ISO_8859_1))
		}
		addPredicateParser({ it.tag.tag == ASN1Tag.IA5_STRING }) { stream, element, _ ->
			ASN1String(element.tag, stream.readAllBytes().toString(Charsets.US_ASCII))
		}
		addPredicateParser({ it.tag.tag == ASN1Tag.SEQUENCE }) { stream, element, _ ->
			ASN1Sequence(element.tag, element.data)
		}
		addPredicateParser({ it.tag.tag == ASN1Tag.SET }) { stream, element, _ ->
			ASN1Set(element.tag, element.data)
		}
		addPredicateParser({ it.tag.tag == ASN1Tag.UTC_TIME }) { stream, element, _ ->
			ASN1UTCTime(element.tag, element.data.toString(Charsets.US_ASCII))
		}
	}

	init {
//		addParser(6) { stream, _ ->
//			ASN1ObjectIdentifier(
//				buildList {
//					val firstByte = stream.read()
//					add(firstByte / 40)
//					add(firstByte % 40)
//
//					var value = 0
//					while (true) {
//						val b = stream.read()
//						if (b == -1) break
//						value = (value shl 7) or (b and 0x7F)
//						if ((b and 0x80) == 0) {
//							add(value)
//							value = 0
//						}
//					}
//				}.toTypedArray()
//			)
//		}
	}
}