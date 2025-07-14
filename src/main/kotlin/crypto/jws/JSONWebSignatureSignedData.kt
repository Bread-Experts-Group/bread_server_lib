package org.bread_experts_group.crypto.jws

import org.bread_experts_group.coder.fixed.json.JSONConvertible
import org.bread_experts_group.coder.format.parse.asn1.ASN1Parser
import org.bread_experts_group.coder.format.parse.asn1.element.ASN1Integer
import org.bread_experts_group.coder.format.parse.asn1.element.ASN1Sequence
import org.bread_experts_group.crypto.x509.X509ASN1Certificate.Companion.toBytes
import java.security.KeyPair
import java.security.Signature
import java.util.*

data class JSONWebSignatureSignedData(
	val protected: String,
	val payload: String,
	val signature: String
) : JSONConvertible {
	override fun toJSON(): String = buildString {
		append('{')
		append("\"protected\":\"$protected\",")
		append("\"signature\":\"$signature\",")
		append("\"payload\":\"$payload\"")
		append('}')
	}

	companion object {
		fun createSignedData(
			protected: JSONWebKeyProtectedHeader,
			payload: JSONConvertible,
			keyPair: KeyPair
		): JSONWebSignatureSignedData {
			val encoder = Base64.getUrlEncoder().withoutPadding()
			val protectedB64 = encoder.encodeToString(protected.toJSON().toByteArray())
			val payloadB64 = encoder.encodeToString(payload.toJSON().toByteArray())
			val signatureB64 = Signature.getInstance("SHA256withECDSA").let {
				it.initSign(keyPair.private)
				it.update("$protectedB64.$payloadB64".toByteArray())
				val seq = ASN1Parser().setInput(it.sign().inputStream()).toList()
					.first() as ASN1Sequence
				encoder.encodeToString(
					((seq.first() as ASN1Integer).value).toBytes(32) +
							((seq.first() as ASN1Integer).value).toBytes(32)
				)
			}
			return JSONWebSignatureSignedData(
				protectedB64,
				payloadB64,
				signatureB64
			)
		}
	}
}