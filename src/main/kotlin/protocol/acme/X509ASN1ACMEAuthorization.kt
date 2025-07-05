package org.bread_experts_group.protocol.acme

import org.bread_experts_group.coder.format.asn1.element.ASN1Boolean
import org.bread_experts_group.coder.format.asn1.element.ASN1ObjectIdentifier
import org.bread_experts_group.coder.format.asn1.element.ASN1OctetString
import org.bread_experts_group.coder.format.asn1.element.ASN1Sequence
import org.bread_experts_group.crypto.x509.X509ASN1Certificate.Companion.OID_DEPARTMENT_OF_DEFENSE
import org.bread_experts_group.crypto.x509.X509ASN1Certificate.Companion.OID_IDENTIFIED_ORGANIZATION
import org.bread_experts_group.crypto.x509.X509ASN1Certificate.Companion.OID_INTERNET
import org.bread_experts_group.crypto.x509.X509ASN1Certificate.Companion.OID_ISO
import org.bread_experts_group.crypto.x509.X509ASN1Certificate.Companion.OID_MECHANISMS
import org.bread_experts_group.crypto.x509.X509ASN1Certificate.Companion.OID_PKIX
import org.bread_experts_group.crypto.x509.X509ASN1Certificate.Companion.OID_PRIVATE_EXTENSION
import org.bread_experts_group.crypto.x509.X509ASN1Certificate.Companion.OID_SECURITY

class X509ASN1ACMEAuthorization(keyAuthorization: ByteArray) : ASN1Sequence(
	EXTENSION_ACME,
	ASN1Boolean(true),
	ASN1OctetString(
		ASN1OctetString(keyAuthorization).asBytes()
	)
) {
	companion object {
		val EXTENSION_ACME: ASN1ObjectIdentifier = ASN1ObjectIdentifier( // id-pe
			OID_ISO, OID_IDENTIFIED_ORGANIZATION, OID_DEPARTMENT_OF_DEFENSE,
			OID_INTERNET, OID_SECURITY, OID_MECHANISMS, OID_PKIX, OID_PRIVATE_EXTENSION,
			31
		)
	}
}