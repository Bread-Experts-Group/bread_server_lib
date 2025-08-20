package org.bread_experts_group.protocol.old.ssh

import org.bread_experts_group.coder.format.parse.InvalidInputException
import org.bread_experts_group.crypto.KeyPairFile
import org.bread_experts_group.crypto.read
import org.bread_experts_group.protocol.old.ssh.packet.*
import org.bread_experts_group.stream.*
import org.bread_experts_group.version
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.math.BigInteger
import java.net.URI
import java.security.KeyFactory
import java.security.PublicKey
import java.security.Security
import java.security.spec.NamedParameterSpec
import java.security.spec.X509EncodedKeySpec
import java.security.spec.XECPublicKeySpec
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.Semaphore

class SSHConnection(from: InputStream, to: OutputStream) {
	val backlog: LinkedBlockingQueue<Result<SSHBasePacket>> = LinkedBlockingQueue<Result<SSHBasePacket>>()
	fun next(): Result<SSHBasePacket> = backlog.take()

	init {
		val kex = mutableSetOf<SSHNamedAlgorithm>()
		val pub = mutableSetOf<SSHNamedAlgorithm>()
		val cip = mutableSetOf<SSHNamedAlgorithm>()
		val com = mutableSetOf(SSHNamedAlgorithm("none"))
		val mac = mutableSetOf<SSHNamedAlgorithm>()
		for (macAlgo in Security.getAlgorithms("Mac")) when (macAlgo) {
			"HMACSHA1" -> mac.add(SSHNamedAlgorithm("hmac-sha1"))
			"HMACMD5" -> mac.add(SSHNamedAlgorithm("hmac-md5"))
			"HMACSHA256" -> mac.add(SSHNamedAlgorithm("hmac-sha2-256"))
			"HMACSHA512" -> mac.add(SSHNamedAlgorithm("hmac-sha2-512"))
		}
		cip.add(SSHPrivateAlgorithm("chacha20-poly1305", URI("openssh.com")))
		kex.add(SSHNamedAlgorithm("curve25519-sha256"))
		pub.add(SSHNamedAlgorithm("ssh-ed25519"))

		var state = SSHConnectionState.BANNER
		val keypair = KeyPairFile(
			File("./test.pub"),
			File("./test.pri"),
		).read()
		Thread.ofVirtual().name("SSH-2.0 Backlogger").start {
			val version = from.scanPattern(CRLF).decodeToString()
			backlog.add(Result.success(SSHBannerPacket(version)))
			if (!version.startsWith("SSH-2.0")) throw InvalidInputException(
				"Client sent bad SSH header; $version"
			)
			to.writeString("SSH-2.0-BreadExpertsGroup${version()}\r\n")

			state = SSHConnectionState.KEY_EXCHANGE_NEGOTIATION
			val fromLock = Semaphore(1)
			lateinit var negotiated: SSHAlgorithmNegotiationPacket
			lateinit var clientKey: PublicKey
			while (true) {
				fromLock.acquire()
				val length = (from.read32ul() - 1).toULong()
				val padding = from.read().toLong()
				fromLock.release()
				val packet = SSHPacket(
					object : LockedLongInputStream(fromLock, length - padding.toULong()) {
						override fun readLocked(): Int = from.read()
						override fun unlock(): Int {
							from.skipNBytes(padding)
							return super.unlock()
						}
					}
				)
				val next = when (state) {
					SSHConnectionState.KEY_EXCHANGE_NEGOTIATION -> {
						val next = SSHIdentifiablePacket.decode(packet) as SSHAlgorithmNegotiationPacket
						negotiated = SSHAlgorithmNegotiationPacket(
							keyExchangeAlgorithms = setOf(next.keyExchangeAlgorithms.intersect(kex).first()),
							serverHostKeyAlgorithms = setOf(next.serverHostKeyAlgorithms.intersect(pub).first()),
							encryptionAlgorithmsCTS = setOf(next.encryptionAlgorithmsCTS.intersect(cip).first()),
							encryptionAlgorithmsSTC = setOf(next.encryptionAlgorithmsSTC.intersect(cip).first()),
							compressionAlgorithmsCTS = setOf(next.compressionAlgorithmsCTS.intersect(com).first()),
							compressionAlgorithmsSTC = setOf(next.compressionAlgorithmsSTC.intersect(com).first()),
							macAlgorithmsCTS = setOf(next.macAlgorithmsCTS.intersect(mac).first()),
							macAlgorithmsSTC = setOf(next.macAlgorithmsSTC.intersect(mac).first())
						)
						negotiated.write(to)
						state = SSHConnectionState.KEY_EXCHANGE
						next
					}

					SSHConnectionState.KEY_EXCHANGE -> {
						val init = SSHIdentifiablePacket.decode(packet) as SSHECDHKeyExchangeInitializationPacket
						val param = NamedParameterSpec("X25519")
						val pubSpec = XECPublicKeySpec(param, BigInteger(init.publicKey))
						val kf = KeyFactory.getInstance("XDH")
						clientKey = kf.generatePublic(pubSpec)
						val publicKeyBlob = ByteArrayOutputStream().use {
							val type = "ssh-ed25519".toByteArray(Charsets.ISO_8859_1)
							it.write32(type.size)
							it.write(type)
							it.write32(keypair.public.encoded.size)
							it.write(keypair.public.encoded)
							it.toByteArray()
						}
						val x25519 = KeyFactory
							.getInstance("X25519")
							.generatePublic(X509EncodedKeySpec(keypair.public.encoded))
						val reply = SSHECDHKeyExchangeReplyPacket(
							publicKeyBlob,
							x25519.encoded,
							byteArrayOf()
						)
						reply.write(to)
						init
					}

					else -> packet
				}
				backlog.add(Result.success(next))
			}
		}.uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { _, t ->
			backlog.add(Result.failure(t))
		}
	}
}