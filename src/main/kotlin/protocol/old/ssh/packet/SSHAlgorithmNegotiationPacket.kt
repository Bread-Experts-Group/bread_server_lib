package org.bread_experts_group.protocol.old.ssh.packet

import org.bread_experts_group.protocol.old.ssh.SSHNamedAlgorithm
import org.bread_experts_group.protocol.old.ssh.SSHPrivateAlgorithm
import org.bread_experts_group.stream.read32
import org.bread_experts_group.stream.readString
import org.bread_experts_group.stream.write32
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.URI
import kotlin.random.Random

class SSHAlgorithmNegotiationPacket(
	val cookie: ByteArray = Random.nextBytes(16),
	val keyExchangeAlgorithms: Set<SSHNamedAlgorithm> = setOf(),
	val serverHostKeyAlgorithms: Set<SSHNamedAlgorithm> = setOf(),
	val encryptionAlgorithmsCTS: Set<SSHNamedAlgorithm> = setOf(),
	val encryptionAlgorithmsSTC: Set<SSHNamedAlgorithm> = setOf(),
	val macAlgorithmsCTS: Set<SSHNamedAlgorithm> = setOf(),
	val macAlgorithmsSTC: Set<SSHNamedAlgorithm> = setOf(),
	val compressionAlgorithmsCTS: Set<SSHNamedAlgorithm> = setOf(),
	val compressionAlgorithmsSTC: Set<SSHNamedAlgorithm> = setOf(),
	val languagesCTS: Set<SSHNamedAlgorithm> = setOf(),
	val languagesSTC: Set<SSHNamedAlgorithm> = setOf(),
	val firstExchangeFollows: Boolean = false,
	val reserved: Int = 0
) : SSHIdentifiablePacket(SSHPacketIdentity.SSH_MSG_KEXINIT) {
	override fun toString(): String = super.toString() + "[key exchange: $keyExchangeAlgorithms, " +
			"server host key: $serverHostKeyAlgorithms, Client-To-Server: [" +
			"encryption: $encryptionAlgorithmsCTS, mac: $macAlgorithmsCTS, compression: $compressionAlgorithmsCTS, " +
			"languages: $languagesCTS], Server-To-Client: [encryption: $encryptionAlgorithmsSTC, " +
			"mac: $macAlgorithmsSTC, compression: $compressionAlgorithmsSTC, languages: $languagesSTC] | " +
			"$reserved${if (firstExchangeFollows) ", first exchange follows" else ""}][${cookie.toHexString()}]"

	val data: ByteArray = ByteArrayOutputStream().use {
		it.write(cookie)
		it.writeAlgorithmList(keyExchangeAlgorithms)
		it.writeAlgorithmList(serverHostKeyAlgorithms)
		it.writeAlgorithmList(encryptionAlgorithmsCTS)
		it.writeAlgorithmList(encryptionAlgorithmsSTC)
		it.writeAlgorithmList(macAlgorithmsCTS)
		it.writeAlgorithmList(macAlgorithmsSTC)
		it.writeAlgorithmList(compressionAlgorithmsCTS)
		it.writeAlgorithmList(compressionAlgorithmsSTC)
		it.writeAlgorithmList(languagesCTS)
		it.writeAlgorithmList(languagesSTC)
		it.write(if (firstExchangeFollows) 1 else 0)
		it.write32(reserved)
		it.toByteArray()
	}

	override fun identityComputeSize(): Int = data.size
	override fun identityWrite(stream: OutputStream) = stream.write(data)

	companion object {
		fun decode(from: InputStream) = SSHAlgorithmNegotiationPacket(
			from.readNBytes(16),
			from.readAlgorithmList(), from.readAlgorithmList(),
			from.readAlgorithmList(), from.readAlgorithmList(),
			from.readAlgorithmList(), from.readAlgorithmList(),
			from.readAlgorithmList(), from.readAlgorithmList(),
			from.readAlgorithmList(), from.readAlgorithmList(),
			from.read() != 0, from.read32()
		)

		fun OutputStream.writeAlgorithmList(set: Set<SSHNamedAlgorithm>) {
			val asString = (set.joinToString(",") { it.stringForm() }).toByteArray(Charsets.ISO_8859_1)
			this.write32(asString.size)
			this.write(asString)
		}

		fun InputStream.readAlgorithmList(): Set<SSHNamedAlgorithm> = buildSet {
			val string = this@readAlgorithmList.readString(this@readAlgorithmList.read32())
			addAll(string.split(','))
		}.map {
			val split = it.split('@', limit = 2)
			if (split.size > 1) SSHPrivateAlgorithm(split[0], URI(split[1]))
			else SSHNamedAlgorithm(split[0])
		}.toSet()
	}
}