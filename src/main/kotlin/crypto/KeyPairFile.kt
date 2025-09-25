package org.bread_experts_group.crypto

import org.bread_experts_group.logging.ColoredHandler
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.attribute.*
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.logging.Logger
import javax.security.auth.Destroyable

data class KeyPairFile(
	val publicKey: File,
	val privateKey: File
) : Destroyable {
	val logger: Logger = ColoredHandler.newLoggerResourced("key_pair_files")

	fun ensurePermissions() {
		ensureDestruction()
		if (!publicKey.exists())
			return logger.warning { "Public key does not exist [${publicKey.canonicalPath}]" }
		publicKey.restrictToLocal()
		if (!privateKey.exists())
			return logger.warning { "Private key does not exist [${privateKey.canonicalPath}]" }
		privateKey.restrictToLocal()
	}

	override fun destroy() {
		ensureDestruction()
		this.publicKey.delete()
		this.privateKey.delete()
	}

	private fun ensureDestruction() =
		if (this.isDestroyed) throw IllegalStateException("Already destroyed")
		else null
}

fun KeyPairFile?.read(keyFactoryAlgorithm: String = "Ed25519"): KeyPair {
	val saveToFiles = this != null && !(this.publicKey.exists() && this.privateKey.exists())
	return if (this != null && !saveToFiles) {
		val keyFactory = KeyFactory.getInstance(keyFactoryAlgorithm)
		this.ensurePermissions()
		KeyPair(
			keyFactory.generatePublic(
				X509EncodedKeySpec(this.publicKey.readBytes())
			),
			keyFactory.generatePrivate(
				PKCS8EncodedKeySpec(this.privateKey.readBytes())
			)
		)
	} else {
		val newKeyPair = KeyPairGenerator.getInstance(keyFactoryAlgorithm).generateKeyPair()
		if (saveToFiles) {
			this.publicKey.createNewFile()
			this.privateKey.createNewFile()
			this.ensurePermissions()
			this.publicKey.writeBytes(newKeyPair.public.encoded)
			this.privateKey.writeBytes(newKeyPair.private.encoded)
		}
		newKeyPair
	}
}

fun File.restrictToLocal() {
	// Windows / NTFS / ACL-supporting systems
	Files.getFileAttributeView(this.toPath(), AclFileAttributeView::class.java)?.let {
		val user = FileSystems.getDefault().userPrincipalLookupService.lookupPrincipalByName(
			System.getProperty("user.name")
		)
		it.acl = listOf(
			AclEntry.newBuilder()
				.setType(AclEntryType.ALLOW)
				.setFlags(AclEntryFlag.FILE_INHERIT, AclEntryFlag.DIRECTORY_INHERIT)
				.setPermissions(*AclEntryPermission.entries.toTypedArray())
				.setPrincipal(user)
				.build()
		)
		return
	}
	// POSIX systems
	Files.getFileAttributeView(this.toPath(), PosixFileAttributeView::class.java)?.let {
		it.setPermissions(
			setOf(
				PosixFilePermission.OWNER_READ,
				PosixFilePermission.OWNER_WRITE,
				PosixFilePermission.OWNER_EXECUTE
			)
		)
		return
	}
	// Otherwise, attempt to save permissions generically
	this.setReadable(false, false)
	this.setWritable(false, false)
	this.setExecutable(false, false)
	this.setReadable(true, true)
	this.setWritable(true, true)
	this.setExecutable(true, true)
}