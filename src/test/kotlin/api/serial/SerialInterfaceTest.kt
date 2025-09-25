package org.bread_experts_group.api.serial

import org.bread_experts_group.api.gps.nmea0183.NMEAMessage
import org.bread_experts_group.api.gps.nmea0183.NMEARMCMessage
import org.bread_experts_group.api.gps.nmea0183.NMEAReceiverStatus
import org.bread_experts_group.channel.ByteBufferChannel
import org.bread_experts_group.coder.MappedEnumeration
import org.bread_experts_group.ffi.readString
import org.bread_experts_group.io.reader.ReadingByteBuffer
import org.bread_experts_group.io.writer.DatagramWriter
import org.bread_experts_group.protocol.ntp.NTPAssociationMode
import org.bread_experts_group.protocol.ntp.NTPLeapIndicator
import org.bread_experts_group.protocol.ntp.NetworkTimeProtocol
import org.bread_experts_group.protocol.ntp.NetworkTimeProtocolV4
import org.junit.jupiter.api.Test
import java.lang.foreign.Arena
import java.math.BigDecimal
import java.net.InetAddress
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.measureTimedValue

class SerialInterfaceTest {
	@Test
	fun gps() {
		var referenceTimestamp: BigDecimal = BigDecimal.ZERO
		var rootDelay: Duration = Duration.ZERO
		var lastNanos = System.nanoTime()
		Thread.ofPlatform().start {
			val serial = SerialInterface.open(
				15u,
				9600u, 8u, 1u, SerialParityScheme.NO_PARITY
			)
			val testBuffer = Arena.ofAuto().allocate(512)
			var partialMessage = ""
			while (true) {
				serial.read(testBuffer, 512)
				val read = measureTimedValue {
					testBuffer.readString(Charsets.US_ASCII, 512)
				}
				rootDelay = read.duration
				var decodedData = partialMessage + read.value
				while (true) {
					val edge = decodedData.indexOf("\r\n")
					if (edge == -1) break
					val message = NMEAMessage.decode(decodedData.substring(0, edge + 2))
					if (
						message.checksumValid &&
						message is NMEARMCMessage &&
						message.status.enum == NMEAReceiverStatus.DATA_VALID
					) {
						val instant = message.date
							.atTime(message.time)
							.toInstant()
							.plusSeconds(2_208_988_800)
						referenceTimestamp = BigDecimal(instant.epochSecond)
							.add(BigDecimal(instant.nano / 1_000_000_000.0))
						lastNanos = System.nanoTime()
						println(referenceTimestamp)
					}
					decodedData = decodedData.substring(edge + 2)
				}
				partialMessage = decodedData
			}
		}
		val udp = DatagramChannel.open()
		udp.bind(InetSocketAddress(InetAddress.getByName("0.0.0.0"), 53))
		while (true) {
			val data = ByteBuffer.allocate(65535)
			val client = udp.receive(data)
			println("$client")
			val initialRead = System.nanoTime()
			data.flip()
			val data2 = ByteBuffer.allocate(65535)
			val read = ByteBufferChannel(data)
			val reading = ReadingByteBuffer(read, data2, null)
			val ntp = try {
				NetworkTimeProtocol.layout.read(reading)
			} catch (e: Exception) {
				e.printStackTrace()
				continue
			}
			val reference = referenceTimestamp
			val tickingReference = reference + (BigDecimal.valueOf(
				System.nanoTime() - lastNanos
			).divide(BigDecimal.valueOf(1000000000), NetworkTimeProtocol.intPrecision))
			val rootDelayTS = rootDelay
				.toLong(DurationUnit.NANOSECONDS)
				.toBigDecimal()
				.divide(BigDecimal.valueOf(1000000000), NetworkTimeProtocol.intPrecision)
			val transmitTS = tickingReference + (BigDecimal.valueOf(
				System.nanoTime() - initialRead
			).divide(BigDecimal.valueOf(1000000000), NetworkTimeProtocol.intPrecision))
			NetworkTimeProtocol.layout.write(
				DatagramWriter(client, udp, ByteBuffer.allocate(65535)),
				NetworkTimeProtocolV4(
					MappedEnumeration(NTPLeapIndicator.NONE),
					MappedEnumeration(NTPAssociationMode.SERVER),
					1,
					6,
					-18,
					rootDelayTS,
					BigDecimal.ZERO,
					"GPS\u0000",
					reference,
					(ntp as NetworkTimeProtocolV4).transmitTimestamp,
					tickingReference,
					transmitTS
				)
			)
		}
	}
}