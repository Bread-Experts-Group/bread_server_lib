package org.bread_experts_group

import java.io.File
import java.io.FileInputStream
import java.nio.file.Path
import java.security.KeyStore
import javax.net.ssl.*

fun getKeyStoreFromPath(path: String, password: String): KeyStore = getKeyStoreFromPath(Path.of(path), password)
fun getKeyStoreFromPath(path: Path, password: String): KeyStore = getKeyStoreFromPath(path.toFile(), password)
fun getKeyStoreFromPath(path: File, password: String): KeyStore {
	val keyStore = KeyStore.getInstance("PKCS12")
	FileInputStream(path).use { keyStore.load(it, password.toCharArray()) }
	return keyStore
}

fun KeyStore.getManagerFactoryX509(password: String): KeyManagerFactory = KeyManagerFactory
	.getInstance("SunX509")
	.also { it.init(this, password.toCharArray()) }

fun KeyManagerFactory.getTLSContext(trustManagers: Array<TrustManager>? = null): SSLContext = SSLContext
	.getInstance("TLS")
	.also { it.init(this.keyManagers, trustManagers, null) }

fun SSLContext.getServerSocket() =
	((this.serverSocketFactory as SSLServerSocketFactory).createServerSocket() as SSLServerSocket).also {
		it.enabledCipherSuites = goodSchemes
		it.enabledProtocols = allowedProtocols
	}

fun SSLContext.getSocket() =
	((this.socketFactory as SSLSocketFactory).createSocket() as SSLSocket).also {
		it.enabledCipherSuites = goodSchemes
		it.enabledProtocols = allowedProtocols
	}

fun getTLSContext(keystorePath: File, password: String, trustManagers: Array<TrustManager>? = null): SSLContext {
	val keystore = getKeyStoreFromPath(keystorePath, password)
	val managerFactory = keystore.getManagerFactoryX509(password)
	return managerFactory.getTLSContext(trustManagers)
}

val goodSchemes = arrayOf(
	// TLS 1.3
	"TLS_AES_256_GCM_SHA384", "TLS_CHACHA20_POLY1305_SHA256",
	// TLS 1.2
	"TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384", "TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256"
)
val allowedProtocols = arrayOf(
	"TLSv1.3", "TLSv1.2"
)

fun getSSLServerSocket(keystorePath: File, password: String): SSLServerSocket = getTLSContext(keystorePath, password)
	.getServerSocket()

fun getSSLSocket(keystorePath: File, password: String): SSLSocket = getTLSContext(keystorePath, password)
	.getSocket()