package org.bread_experts_group.coder.format.asn1.element

import org.bread_experts_group.coder.Mappable

enum class ASN1Tag(
	override var id: Int, override val tag: String
) : Mappable<ASN1Tag, Int> {
	BOOLEAN(1, "BOOLEAN"),
	INTEGER(2, "INTEGER"),
	BIT_STRING(3, "BIT STRING"),
	OCTET_STRING(4, "OCTET STRING"),
	NULL(5, "NULL"),
	OBJECT_IDENTIFIER(6, "OBJECT IDENTIFIER"),
	OBJECT_DESCRIPTOR(7, "ObjectDescriptor"),
	EXTERNAL(8, "EXTERNAL"),
	REAL(9, "REAL"),
	ENUMERATED(10, "ENUMERATED"),
	EMBEDDED_PDV(11, "EMBEDDED PDV"),
	UTF8_STRING(12, "UTF8String"),
	RELATIVE_OID(13, "RELATIVE-OID"),
	TIME(14, "TIME"),
	SEQUENCE(16, "SEQUENCE"),
	SET(17, "SET"),
	NUMERIC_STRING(18, "NumericString"),
	PRINTABLE_STRING(19, "PrintableString"),
	T61_STRING(20, "T61String"),
	VIDEOTEX_STRING(21, "VideotexString"),
	IA5_STRING(22, "IA5String"),
	UTC_TIME(23, "UTCTime"),
	GENERALIZED_TIME(24, "GeneralizedTime"),
	GRAPHIC_STRING(25, "GraphicString"),
	VISIBLE_STRING(26, "VisibleString"),
	GENERAL_STRING(27, "GeneralString"),
	UNIVERSAL_STRING(28, "UniversalString"),
	CHARACTER_STRING(29, "CHARACTER STRING"),
	BMP_STRING(30, "BMPString"),
	DATE(31, "DATE"),
	TIME_OF_DAY(32, "TIME-OF-DAY"),
	DATE_TIME(33, "DATE-TIME"),
	DURATION(34, "DURATION"),
	UNKNOWN(-1, "Unknown");

	override fun other(): ASN1Tag? = UNKNOWN
	override fun toString(): String = stringForm()
}