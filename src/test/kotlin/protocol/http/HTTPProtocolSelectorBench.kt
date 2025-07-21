package org.bread_experts_group.protocol.http

import org.junit.jupiter.api.Test
import java.net.InetSocketAddress
import java.net.URI
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.util.concurrent.CountDownLatch
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.measureTime

class HTTPProtocolSelectorBench {
	@Test
	fun bench() {
		var d = Duration.ZERO
		val samp = 1000000
		val channel = ServerSocketChannel.open()
		channel.bind(InetSocketAddress("127.0.0.1", 0))
		val r = HTTPRequest(HTTPMethod.GET, URI.create("/"))
		Thread.ofVirtual().start {
			val c = HTTPResponse(r, 200)
			while (true) {
				val rxFrom = channel.accept()
				Thread.ofVirtual().start {
					val server = HTTPProtocolSelector(HTTPVersion.HTTP_1_1, rxFrom, rxFrom, true)
					repeat(samp) {
						d += measureTime {
							server.sendResponse(c)
						}
					}
				}
			}
		}
		val lock = CountDownLatch(10)
		repeat(10) {
			Thread.ofVirtual().start {
				val sendTo = SocketChannel.open()
				sendTo.connect(channel.localAddress)
				sendTo.configureBlocking(false)
				val client = HTTPProtocolSelector(HTTPVersion.HTTP_1_1, sendTo, sendTo, false)
				repeat(samp) {
					client.sendRequest(r)
				}
				sendTo.configureBlocking(true)
				repeat(samp) {
					client.nextResponse()
				}
				lock.countDown()
			}
		}
		lock.await()
		println(d / (samp * 10))
		println("done ${(1000000 / (d / (samp * 10)).toDouble(DurationUnit.MICROSECONDS))} r / s")
	}
}