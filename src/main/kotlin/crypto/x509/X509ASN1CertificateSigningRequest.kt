package org.bread_experts_group.crypto.x509

import org.bread_experts_group.coder.format.parse.asn1.element.*
import org.bread_experts_group.crypto.x509.X509ASN1Certificate.Companion.ATTRIBUTE_COMMON_NAME
import org.bread_experts_group.crypto.x509.X509ASN1Certificate.Companion.ECDSA_WITH_SHA_512
import org.bread_experts_group.crypto.x509.X509ASN1Certificate.Companion.EC_PUBLIC_KEY
import org.bread_experts_group.crypto.x509.X509ASN1Certificate.Companion.EXTENSION_REQUEST
import org.bread_experts_group.crypto.x509.X509ASN1Certificate.Companion.EXTENSION_SUBJECT_ALT_NAME
import org.bread_experts_group.crypto.x509.X509ASN1Certificate.Companion.SECP384R1
import org.bread_experts_group.crypto.x509.X509ASN1Certificate.Companion.toBytes
import java.security.KeyPair
import java.security.Signature
import java.security.interfaces.ECPublicKey

class X509ASN1CertificateSigningRequest(
	keyPair: KeyPair,
	names: List<String>
) {
	val certificationRequest: ASN1Sequence = ASN1Sequence(
		ASN1Integer(0),
		ASN1Sequence(
			ASN1Set(
				ASN1Sequence(
					ATTRIBUTE_COMMON_NAME,
					ASN1UTFString(names.first())
				)
			)
		),
		ASN1Sequence(
			ASN1Sequence(
				EC_PUBLIC_KEY,
				SECP384R1
			),
			ASN1BitString(
				byteArrayOf(
					0x04,
					*(keyPair.public as ECPublicKey).w.affineX.toBytes(48),
					*(keyPair.public as ECPublicKey).w.affineY.toBytes(48)
				)
			)
		),
		ASN1Tagged(
			0, true,
			ASN1Sequence(
				EXTENSION_REQUEST,
				ASN1Set(
					ASN1Sequence(
						ASN1Sequence(
							EXTENSION_SUBJECT_ALT_NAME,
							ASN1Boolean(false),
							ASN1OctetString(
								ASN1Sequence(
									*names.map {
										ASN1Tagged(
											2, false,
											ASN1UTFString(it)
										)
									}.toTypedArray()
								).asBytes()
							)
						)
					)
				)
			)
		)
	)

	val csr: ASN1Sequence = ASN1Sequence(
		certificationRequest,
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
			it.update(certificationRequest.asBytes())
			ASN1BitString(it.sign())
		}
	)
}