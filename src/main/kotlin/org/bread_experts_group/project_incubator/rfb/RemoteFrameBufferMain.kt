package org.bread_experts_group.project_incubator.rfb

import org.bread_experts_group.api.system.SystemFeatures
import org.bread_experts_group.api.system.SystemProvider
import org.bread_experts_group.api.system.socket.SystemSocketProviderFeatures
import org.bread_experts_group.api.system.socket.close.StandardCloseFeatures
import org.bread_experts_group.api.system.socket.ipv4.*
import org.bread_experts_group.api.system.socket.ipv4.stream.SystemInternetProtocolV4StreamProtocolFeatures
import org.bread_experts_group.api.system.socket.ipv4.stream.tcp.IPv4TCPFeatures
import org.bread_experts_group.api.system.socket.resolution.ResolutionDataPart
import org.bread_experts_group.api.system.socket.resolution.WindowsResolutionFeatures
import org.bread_experts_group.io.reader.BSLSocketReader
import org.bread_experts_group.io.reader.BSLSocketWriter
import java.io.File
import java.nio.ByteOrder
import java.util.*
import javax.imageio.ImageIO
import kotlin.system.exitProcess

fun main() {
	val tcpV4 = SystemProvider.get(SystemFeatures.NETWORKING_SOCKETS)
		.get(SystemSocketProviderFeatures.INTERNET_PROTOCOL_V4)
		.get(SystemInternetProtocolV4SocketProviderFeatures.STREAM_PROTOCOLS)
		.get(SystemInternetProtocolV4StreamProtocolFeatures.TRANSMISSION_CONTROL_PROTOCOL)
	val tcpV4Resolution = tcpV4.get(IPv4TCPFeatures.NAME_RESOLUTION)
	val tcpV4RecvAny = tcpV4Resolution.resolve(
		"", 5900u,
		WindowsResolutionFeatures.PASSIVE
	).firstNotNullOf {
		it as? ResolutionDataPart
	}.data.firstNotNullOf { it as? InternetProtocolV4AddressData }
	val tcpV4Socket = tcpV4.get(IPv4TCPFeatures.SOCKET)
		.openSocket()
	tcpV4Socket.get(IPv4SocketFeatures.BIND)
		.bind(InternetProtocolV4AddressPortData(tcpV4RecvAny.data, 5900u))
	tcpV4Socket.get(IPv4SocketFeatures.LISTEN)
		.listen()
	// PROTOTYPE LOGIC
	val acceptData = tcpV4Socket.get(IPv4SocketFeatures.ACCEPT)
		.accept()
	val acceptedAddress = acceptData.firstNotNullOf { it as? InternetProtocolV4AddressPortData }
	println("Connection @ $acceptedAddress")
	@Suppress("UNCHECKED_CAST")
	val acceptedSocket = acceptData.firstNotNullOf { it as? IPv4Socket }

	data class PixelFormatDataStructure(
		val bitsPerPixel: Int,
		val depth: Int,
		val bigEndian: Boolean,
		val trueColor: Boolean,
		val redMax: Int,
		val greenMax: Int,
		val blueMax: Int,
		val redShift: Int,
		val greenShift: Int,
		val blueShift: Int
	)

	val write = BSLSocketWriter(acceptedSocket.get(IPv4SocketFeatures.SEND))
	val read = BSLSocketReader(acceptedSocket.get(IPv4SocketFeatures.RECEIVE))
	write.order = ByteOrder.BIG_ENDIAN
	read.order = ByteOrder.BIG_ENDIAN

	fun writePixelFormatStructure(n: PixelFormatDataStructure) {
		write.write8i(n.bitsPerPixel)
		write.write8i(n.depth)
		write.write8i(if (n.bigEndian) 1 else 0)
		write.write8i(if (n.trueColor) 1 else 0)
		write.write16i(n.redMax)
		write.write16i(n.greenMax)
		write.write16i(n.blueMax)
		write.write8i(n.redShift)
		write.write8i(n.greenShift)
		write.write8i(n.blueShift)
		write.fill(3)
	}

	fun nextPixelFormatStructure(): PixelFormatDataStructure {
		val structure = PixelFormatDataStructure(
			read.readU8i(),
			read.readU8i(),
			read.readU8i() != 0,
			read.readU8i() != 0,
			read.readU16i(),
			read.readU16i(),
			read.readU16i(),
			read.readU8i(),
			read.readU8i(),
			read.readU8i()
		)
		read.skip(3)
		return structure
	}

	fun shutdown(reason: String) {
		println("Shutting down: $reason")
		acceptedSocket.close(
			StandardCloseFeatures.STOP_RX, StandardCloseFeatures.STOP_TX,
			StandardCloseFeatures.RELEASE
		)
		exitProcess(0)
	}

	// 7.1.1 ProtocolVersion Handshake
	write.write("RFB 003.008\n".toByteArray(Charsets.US_ASCII))
	write.flush()
	val clientPV = read.readN(12).toString(Charsets.US_ASCII).let {
		if (it.last() != '\n') shutdown("Protocol version did not end with line feed \"$it\"")
		it.take(it.length - 1)
	}
	val delimiter = clientPV.indexOf(' ')
	if (
		delimiter == -1 ||
		clientPV.take(delimiter) != "RFB"
	) shutdown("Bad header presented in protocol version \"$clientPV\"")
	val clientVersion = clientPV.substring(delimiter + 1)
		.split('.', limit = 2)
		.mapNotNull { it.toIntOrNull() }
	if (clientVersion.size != 2) shutdown("Bad protocol version format \"$clientPV\"")
	if (clientVersion[0] != 3) shutdown("Protocol version too high \"$clientPV\"")
	val minorVersionInUse = when (val minor = clientVersion[1]) {
		3, 7, 8 -> minor
		else -> 3
	}
	println("Client version... ${clientVersion[0]}.${clientVersion[1]} (using minor version $minorVersionInUse)")

	// 7.1.2 Security Handshake
	write.write8i(1)
	write.write8i(1)
	write.flush()
	if (read.readU8i() != 1) shutdown("Client did not agree to the provided security methods")

	// 7.1.3 SecurityResult Handshake
	write.write32(0)
	write.flush()

	// 7.3.1 ClientInit
	if (read.readU8i() == 0) println("Client wants desktop exclusive access")
	else println("Client is OK with desktop shared access")

	// 7.3.2 ServerInit (7.4 Pixel Format Data Structure)
	val alpha = ImageIO.read(File("C:/Users/Adenosine3Phosphate/Downloads/image.png"))
	write.write16i(alpha.width) // W
	write.write16i(alpha.height) // H
	var currentFormat = PixelFormatDataStructure(
		32, 24,
		bigEndian = true,
		trueColor = true,
		255, 255, 255,
		24, 16, 8
	)
	writePixelFormatStructure(currentFormat)
	write.write32(16)
	write.write("TestString16....".toByteArray(Charsets.US_ASCII))
	write.flush()

	val encodings = EnumSet.noneOf(RemoteFramebufferEncodings::class.java)
	// 7.5 Client-to-Server Messages
	while (true) {
		when (val type = read.readU8i()) {
			// 7.5.1 SetPixelFormat
			0 -> {
				read.skip(3)
				currentFormat = nextPixelFormatStructure()
			}

			// SetEncodings
			2 -> {
				read.skip(1)
				repeat(read.readU16i()) {
					when (val encoding = read.readS32()) {
						0 -> encodings.add(RemoteFramebufferEncodings.RAW)
						1 -> encodings.add(RemoteFramebufferEncodings.COPYRECT)
						2 -> encodings.add(RemoteFramebufferEncodings.RRE)
						5 -> encodings.add(RemoteFramebufferEncodings.HEXTILE)
						15 -> encodings.add(RemoteFramebufferEncodings.TRLE)
						16 -> encodings.add(RemoteFramebufferEncodings.ZRLE)
						-239 -> encodings.add(RemoteFramebufferEncodings.CURSOR_PSEUDO_ENCODING)
						-223 -> encodings.add(RemoteFramebufferEncodings.DESKTOP_SIZE_PSEUDO_ENCODING)
						else -> println("Unsupported encoding $encoding")
					}
				}
			}

			// 7.5.3 / 7.6.1 FramebufferUpdateRequest / FramebufferUpdate
			3 -> {
				println("Incremental ${read.readU8i()}")
				val x = read.readU16i()
				val y = read.readU16i()
				val w = read.readU16i()
				val h = read.readU16i()
				write.write8i(0)
				write.fill(1)
				write.write16i(1)
				// Rect
				write.write16i(x)
				write.write16i(y)
				write.write16i(w)
				write.write16i(h)
				write.write32(0) // RAW encoding
				for (y in y until (y + h)) {
					for (x in x until (x + w)) {
						val rgb = alpha.getRGB(x, y)
						val r = (rgb shr 16) and 0xFF
						val g = (rgb shr 8) and 0xFF
						val b = rgb and 0xFF
						var p = (r shl currentFormat.redShift) or
								(g shl currentFormat.greenShift) or
								(b shl currentFormat.blueShift)
						if (!currentFormat.bigEndian) p = Integer.reverseBytes(p)
						when (currentFormat.bitsPerPixel) {
							8 -> write.write8i(p)
							16 -> write.write16i(p)
							32 -> write.write32l(p.toLong())
							else -> TODO(currentFormat.toString())
						}
					}
				}
				write.flush()
			}

			// KeyEvent
			4 -> {
				println("Down ${read.readU8i()}")
				read.skip(2)
				println("Key ${read.readS32()}")
				write.write8i(2)
				write.flush()
			}

			// PointerEvent
			5 -> {
				println("Button Mask ${read.readU8i()}")
				println("X ${read.readU16i()}")
				println("Y ${read.readU16i()}")
			}

			else -> shutdown("??? $type")
		}
	}
}