package org.bread_experts_group.protocol.http

import org.bread_experts_group.getTLSContext
import org.bread_experts_group.logging.ColoredHandler
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.ByteBuffer
import java.nio.channels.Channels
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.nio.file.Files
import java.security.cert.X509Certificate
import java.util.concurrent.CountDownLatch
import java.util.logging.Logger
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLEngine
import javax.net.ssl.X509ExtendedTrustManager
import kotlin.io.path.outputStream


class HTTPProtocolSelectorTest {
	val logger: Logger = ColoredHandler.newLoggerResourced("tests.http_protocol_selection")
	val tlsContext: SSLContext
	val remoteClient: HttpClient

	init {
		val tempP12 = Files.createTempFile("temp", "temp")
		HTTPProtocolSelectorTest::class.java.getResourceAsStream("/http/http.p12")!!.copyTo(tempP12.outputStream())
		val trustManager: X509ExtendedTrustManager = object : X509ExtendedTrustManager() {
			override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
			override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
			}

			override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
			}

			override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String, socket: Socket) {
			}

			override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String, socket: Socket) {
			}

			override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String, engine: SSLEngine) {
			}

			override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String, engine: SSLEngine) {
			}
		}
		tlsContext = getTLSContext(tempP12.toFile(), "password", arrayOf(trustManager))
		remoteClient = HttpClient.newBuilder().sslContext(tlsContext).build()
	}

	fun testNextRequest(socket: ServerSocketChannel, version: HTTPVersion, latch: CountDownLatch) {
		while (true) {
			val connection = socket.accept()
			try {
				logger.info("Connection from [${connection.remoteAddress}]")
				val selector = HTTPProtocolSelector(version, connection, connection, true)
				val request = selector.nextRequest().getOrThrow()
				logger.info("Request: $request")
				selector.sendResponse(
					HTTPResponse(
						request, 200,
						data = Channels.newChannel("Hello, ${version.tag}".byteInputStream())
					)
				)
				latch.await()
				break
			} catch (_: SocketException) {
			}
			connection.close()
		}
		socket.close()
	}

	@Test
	fun nextRequestHTTP11(): Unit = assertDoesNotThrow {
		val socket = ServerSocketChannel.open()
		socket.bind(InetSocketAddress("localhost", 60511))
		logger.info("Server active on address [${socket.localAddress}]")
		val block = CountDownLatch(1)
		Thread.ofVirtual().name("HTTP/1.1 Selection Tests, Remote Client").start {
			val response = remoteClient.send(
				HttpRequest.newBuilder(URI("http://localhost:${(socket.localAddress as InetSocketAddress).port}"))
					.version(HttpClient.Version.HTTP_1_1)
					.build(),
				HttpResponse.BodyHandlers.ofString()
			)
			logger.info("Response: $response [${response.body()}]")
			block.countDown()
		}
		testNextRequest(socket, HTTPVersion.HTTP_1_1, block)
	}

	// TODO HTTP/2 Resumption
//	@Test
//	fun nextRequestHTTP2() = assertDoesNotThrow {
//		val socket = tlsContext.serverSocketFactory.createServerSocket() as SSLServerSocket
//		val parameters = socket.sslParameters
//		parameters.applicationProtocols = arrayOf("h2")
//		socket.sslParameters = parameters
//		socket.bind(InetSocketAddress("localhost", 60502))
//		logger.info("Server active on address [${socket.localSocketAddress}]")
//		val block = CountDownLatch(1)
//		Thread.ofVirtual().name("HTTP/2 Selection Tests, Remote Client").start {
//			val response = remoteClient.send(
//				HttpRequest.newBuilder(URI.create("https://localhost:${socket.localPort}"))
//					.version(HttpClient.Version.HTTP_2)
//					.build(),
//				HttpResponse.BodyHandlers.ofString()
//			)
//			logger.info("Response: $response [${response.body()}]")
//			block.countDown()
//		}
//		testNextRequest(socket, HTTPVersion.HTTP_2, block)
//	}

	fun testNextResponse(socket: SocketChannel, version: HTTPVersion) {
		val selector = HTTPProtocolSelector(version, socket, socket, false)
		selector.sendRequest(HTTPRequest(HTTPMethod.GET, URI.create("/")))
		val response = selector.nextResponse().getOrThrow()
		logger.info("Response: $response")
		val buffer = ByteBuffer.allocate(4096)
		val string = StringBuilder()
		while (true) {
			buffer.clear()
			val read = response.data.read(buffer)
			if (read == -1) break
			val bytes = ByteArray(read)
			buffer.flip()
			buffer.get(bytes, 0, buffer.remaining())
			string.append(bytes.decodeToString())
			Thread.sleep(10)
		}
		logger.info(string.toString())
	}

	@Test
	fun nextResponseHTTP11(): Unit = assertDoesNotThrow {
		fun site(s: String) {
			val socket = SocketChannel.open()
			socket.connect(InetSocketAddress(s, 80))
			testNextResponse(socket, HTTPVersion.HTTP_1_1)
			socket.close()
		}

		site("www.google.com")
		site("example.com")
	}
}