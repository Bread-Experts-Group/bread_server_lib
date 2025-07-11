package org.bread_experts_group.crypto.x509

import org.bread_experts_group.coder.format.parse.asn1.element.*
import java.math.BigInteger
import java.security.KeyPair
import java.security.Signature
import java.security.interfaces.ECPublicKey
import java.time.ZonedDateTime
import java.time.temporal.TemporalAmount

/**
 * #### !!! Not designated for normal use (yet) !!!
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc5280">IETF RFC 5280</a>:
 * Internet X.509 Public Key Infrastructure Certificate and Certificate Revocation List (CRL) Profile
 */
class X509ASN1Certificate(
	keyPair: KeyPair,
	validityPeriod: TemporalAmount,
	commonName: String = "",
	vararg extensions: ASN1Sequence
) {
	companion object {
		const val OID_CURVE_PRIME_FIELD: Int = 0
		const val OID_ISO: Int = 1
		const val OID_INTERNET: Int = 1
		const val OID_PRIVATE_EXTENSION: Int = 1
		const val OID_NAMED_CURVES_PRIME_FIELDS: Int = 1
		const val OID_PUBLIC_KEY_CRYPTOGRAPHY_STANDARDS: Int = 1
		const val OID_JOINT_ISO_CCITT: Int = 2
		const val OID_MEMBER_BODY: Int = 2
		const val OID_PUBLIC_KEY_TYPES: Int = 2
		const val OID_ELLIPTIC_CURVE_DOMAIN_PARAMETERS: Int = 3
		const val OID_IDENTIFIED_ORGANIZATION: Int = 3
		const val OID_ECDSA_WITH_SHA2: Int = 3
		const val OID_SIGNATURES: Int = 4
		const val OID_SECURITY: Int = 5
		const val OID_DIRECTORY_SERVICES: Int = 5
		const val OID_MECHANISMS: Int = 5
		const val OID_DEPARTMENT_OF_DEFENSE: Int = 6
		const val OID_PKIX: Int = 7
		const val OID_PKCS_9: Int = 9
		const val OID_CERTIFICATE_EXTENSIONS: Int = 29
		const val OID_UNKNOWN_RFC_8410 = 101
		const val OID_STANDARDS_FOR_EFFICIENT_CRYPTOGRAPHY_GROUP: Int = 132
		const val OID_UNITED_STATES_ANSI: Int = 840
		const val OID_ANSI_X9_62: Int = 10045
		const val OID_RSA_DATA_SECURITY_INC: Int = 113549

		// Object Identifiers
		val ATTRIBUTE_COMMON_NAME: ASN1ObjectIdentifier = ASN1ObjectIdentifier( // id-at
			OID_JOINT_ISO_CCITT, OID_DIRECTORY_SERVICES, 4,
			3
		)
		val EXTENSION_SUBJECT_ALT_NAME: ASN1ObjectIdentifier = ASN1ObjectIdentifier( // id-ce
			OID_JOINT_ISO_CCITT, OID_DIRECTORY_SERVICES, OID_CERTIFICATE_EXTENSIONS,
			17
		)
		val EXTENSION_BASIC_CONSTRAINTS: ASN1ObjectIdentifier = ASN1ObjectIdentifier( // id-ce
			OID_JOINT_ISO_CCITT, OID_DIRECTORY_SERVICES, OID_CERTIFICATE_EXTENSIONS,
			19
		)
		val EXTENSION_REQUEST: ASN1ObjectIdentifier = ASN1ObjectIdentifier(
			OID_ISO, OID_MEMBER_BODY, OID_UNITED_STATES_ANSI,
			OID_RSA_DATA_SECURITY_INC, OID_PUBLIC_KEY_CRYPTOGRAPHY_STANDARDS, OID_PKCS_9,
			14
		)
		val EC_PUBLIC_KEY: ASN1ObjectIdentifier = ASN1ObjectIdentifier(
			OID_ISO, OID_MEMBER_BODY, OID_UNITED_STATES_ANSI,
			OID_ANSI_X9_62, OID_PUBLIC_KEY_TYPES,
			1
		)
		val SECP256R1: ASN1ObjectIdentifier = ASN1ObjectIdentifier(
			OID_ISO, OID_MEMBER_BODY, OID_UNITED_STATES_ANSI,
			OID_ANSI_X9_62, OID_ELLIPTIC_CURVE_DOMAIN_PARAMETERS, OID_NAMED_CURVES_PRIME_FIELDS,
			7
		)
		val SECP384R1: ASN1ObjectIdentifier = ASN1ObjectIdentifier(
			OID_ISO, OID_IDENTIFIED_ORGANIZATION, OID_STANDARDS_FOR_EFFICIENT_CRYPTOGRAPHY_GROUP,
			OID_CURVE_PRIME_FIELD,
			34
		)
		val SECP521R1: ASN1ObjectIdentifier = ASN1ObjectIdentifier(
			OID_ISO, OID_IDENTIFIED_ORGANIZATION, OID_STANDARDS_FOR_EFFICIENT_CRYPTOGRAPHY_GROUP,
			OID_CURVE_PRIME_FIELD,
			35
		)
		val ECDSA_WITH_SHA_224: ASN1ObjectIdentifier = ASN1ObjectIdentifier(
			OID_ISO, OID_MEMBER_BODY, OID_UNITED_STATES_ANSI,
			OID_ANSI_X9_62, OID_SIGNATURES, OID_ECDSA_WITH_SHA2,
			1
		)
		val ECDSA_WITH_SHA_256: ASN1ObjectIdentifier = ASN1ObjectIdentifier(
			OID_ISO, OID_MEMBER_BODY, OID_UNITED_STATES_ANSI,
			OID_ANSI_X9_62, OID_SIGNATURES, OID_ECDSA_WITH_SHA2,
			2
		)
		val ECDSA_WITH_SHA_384: ASN1ObjectIdentifier = ASN1ObjectIdentifier(
			OID_ISO, OID_MEMBER_BODY, OID_UNITED_STATES_ANSI,
			OID_ANSI_X9_62, OID_SIGNATURES, OID_ECDSA_WITH_SHA2,
			3
		)
		val ECDSA_WITH_SHA_512: ASN1ObjectIdentifier = ASN1ObjectIdentifier(
			OID_ISO, OID_MEMBER_BODY, OID_UNITED_STATES_ANSI,
			OID_ANSI_X9_62, OID_SIGNATURES, OID_ECDSA_WITH_SHA2,
			4
		)
		val ID_X25519: ASN1ObjectIdentifier = ASN1ObjectIdentifier(
			OID_ISO, OID_IDENTIFIED_ORGANIZATION, OID_UNKNOWN_RFC_8410,
			110
		)
		val ID_X448: ASN1ObjectIdentifier = ASN1ObjectIdentifier(
			OID_ISO, OID_IDENTIFIED_ORGANIZATION, OID_UNKNOWN_RFC_8410,
			111
		)
		val ID_ED25519: ASN1ObjectIdentifier = ASN1ObjectIdentifier(
			OID_ISO, OID_IDENTIFIED_ORGANIZATION, OID_UNKNOWN_RFC_8410,
			112
		)
		val ID_ED448: ASN1ObjectIdentifier = ASN1ObjectIdentifier(
			OID_ISO, OID_IDENTIFIED_ORGANIZATION, OID_UNKNOWN_RFC_8410,
			113
		)

		fun BigInteger.toBytes(n: Int): ByteArray {
			val full = this.toByteArray()
			return when {
				full.size == n -> full
				full.size < n -> ByteArray(n - full.size) + full
				full.size == n + 1 && full[0] == 0.toByte() -> full.copyOfRange(1, n + 1)
				else -> throw IllegalArgumentException("Invalid size for r or s [${full.size} / $n]")
			}
		}
	}

	val tbsCertificate: ASN1Sequence = ASN1Sequence(
		ASN1Tagged(0, true, ASN1Integer(2)),
		ASN1Integer(BigInteger("B4EAD", 16) + BigInteger.valueOf(System.currentTimeMillis())),
		ASN1Sequence(
			ECDSA_WITH_SHA_512,
			ASN1Element(
				ASN1ElementIdentifier(
					ASN1ElementClass.UNIVERSAL, ASN1ElementConstruction.PRIMITIVE,
					ASN1Tag.NULL
				)
			)
		),
		ASN1Sequence(
			ASN1Set(
				ASN1Sequence(
					ATTRIBUTE_COMMON_NAME,
					ASN1UTFString(commonName)
				)
			)
		),
		run {
			val now = ZonedDateTime.now()
			val identifier = ASN1ElementIdentifier(
				ASN1ElementClass.UNIVERSAL,
				ASN1ElementConstruction.PRIMITIVE,
				ASN1Tag.UTC_TIME
			)
			ASN1Sequence(
				ASN1UTCTime(identifier, now),
				ASN1UTCTime(identifier, now.plus(validityPeriod))
			)
		},
		ASN1Sequence(
			ASN1Set(
				ASN1Sequence(
					ATTRIBUTE_COMMON_NAME,
					ASN1UTFString(commonName)
				)
			)
		),
		run {
			val (fieldOID, size) = when (val public = keyPair.public) {
				is ECPublicKey -> when (val fieldSize = public.params.curve.field.fieldSize) {
					384 -> SECP384R1 to public.params.curve.field.fieldSize
					else -> throw IllegalArgumentException("EC Field Size [$fieldSize]")
				}

				else -> throw UnsupportedOperationException("Public Key [$public]")
			}
			val (keyOID, key) = when (val algo = keyPair.public.algorithm) {
				"EC" -> EC_PUBLIC_KEY to byteArrayOf(
					0x04,
					*(keyPair.public as ECPublicKey).w.affineX.toBytes(size),
					*(keyPair.public as ECPublicKey).w.affineY.toBytes(size)
				)

				else -> throw UnsupportedOperationException("Algorithm [$algo]")
			}
			ASN1Sequence(
				ASN1Sequence(keyOID, fieldOID),
				ASN1BitString(key)
			)
		},
		ASN1Tagged(
			3, true, ASN1Sequence(
				ASN1Sequence(
					EXTENSION_BASIC_CONSTRAINTS,
					ASN1Boolean(true),
					ASN1OctetString(
						ASN1Sequence(
							ASN1Boolean(false)
						).asBytes()
					)
				),
				ASN1Sequence(
					EXTENSION_SUBJECT_ALT_NAME,
					ASN1Boolean(false),
					ASN1OctetString(
						ASN1Sequence(
							ASN1Tagged(
								2, false,
								ASN1OctetString(commonName.toByteArray(Charsets.US_ASCII))
							)
						).asBytes()
					)
				),
				*extensions
			)
		)
	)

	val x509: ASN1Sequence = ASN1Sequence(
		tbsCertificate,
		ASN1Sequence(
			ECDSA_WITH_SHA_512,
			ASN1Element(
				ASN1ElementIdentifier(
					ASN1ElementClass.UNIVERSAL, ASN1ElementConstruction.PRIMITIVE,
					ASN1Tag.NULL
				)
			)
		),
		Signature.getInstance("SHA512withECDSA").let {
			it.initSign(keyPair.private)
			it.update(tbsCertificate.asBytes())
			ASN1BitString(it.sign())
		}
	)
}