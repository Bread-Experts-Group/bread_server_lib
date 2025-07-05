package org.bread_experts_group.crypto.jws

import org.bread_experts_group.coder.fixed.json.JSONConvertible
import org.bread_experts_group.crypto.KeyPairFile
import org.bread_experts_group.crypto.read
import org.bread_experts_group.crypto.x509.X509ASN1Certificate.Companion.toBytes
import java.security.KeyPair
import java.security.MessageDigest
import java.security.interfaces.ECPublicKey
import java.util.*


data class JSONWebKey(
	val keyType: String,
	val curve: String,
	val x: String,
	val y: String
) : JSONConvertible {
	override fun toJSON(): String = buildString {
		append('{')
		append("\"kty\":\"$keyType\",")
		append("\"crv\":\"$curve\",")
		append("\"x\":\"$x\",")
		append("\"y\":\"$y\"")
		append('}')
	}

	fun thumbprint(): String {
		val canonicalJson = """{"crv":"$curve","kty":"$keyType","x":"$x","y":"$y"}"""
		val md = MessageDigest.getInstance("SHA-256")
		val hash = md.digest(canonicalJson.toByteArray(Charsets.UTF_8))
		return Base64.getUrlEncoder().withoutPadding().encode(hash).decodeToString()
	}

	companion object {
		fun newEclipticCurveJWT(
			pair: KeyPairFile? = null
		): Pair<JSONWebKey, KeyPair> {
			val keyPair = pair.read()
			val (x, y) = Base64.getUrlEncoder().withoutPadding().let {
				val ecPublicKey = keyPair.public as ECPublicKey
				val x = it.encodeToString(ecPublicKey.w.affineX.toBytes(32))
				val y = it.encodeToString(ecPublicKey.w.affineY.toBytes(32))
				x to y
			}
			return JSONWebKey(
				"EC", "P-256",
				x, y
			) to keyPair
		}
	}
}
