package org.bread_experts_group.api.system.socket.system.posix

import org.bread_experts_group.api.system.feature.linux.LinuxSystemNetworkingSocketsFeature.Companion.AF_INET6
import org.bread_experts_group.api.system.socket.ipv6.InternetProtocolV6AddressData
import org.bread_experts_group.api.system.socket.resolution.*
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.posix.*
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

fun posixResolve(
	hostName: String,
	serviceName: String,
	domain: Int,
	type: Int,
	protocol: Int,
	vararg features: ResolutionFeatureIdentifier
): List<ResolutionDataIdentifier> {
	val data = mutableListOf<ResolutionDataIdentifier>()
	Arena.ofConfined().use { tempArena ->
		val hints = tempArena.allocate(addrinfo)
		addrinfo_ai_family.set(hints, 0L, domain)
		addrinfo_ai_socktype.set(hints, 0L, type)
		addrinfo_ai_protocol.set(hints, 0L, protocol)
		var flags = 0
		if (features.contains(StandardResolutionFeatures.CANONICAL_NAME)) {
			flags = flags or 0x02
			data.add(StandardResolutionFeatures.CANONICAL_NAME)
		}
		val hostName = if (features.contains(StandardResolutionFeatures.PASSIVE)) {
			flags = flags or 0x01
			data.add(StandardResolutionFeatures.PASSIVE)
			MemorySegment.NULL
		} else tempArena.allocateFrom(hostName, Charsets.UTF_8)
		addrinfo_ai_flags.set(hints, 0L, flags)
		val results = tempArena.allocate(ValueLayout.ADDRESS)
		val status = nativeGetAddrInfo!!.invokeExact(
			capturedStateSegment,
			hostName,
			tempArena.allocateFrom(serviceName, Charsets.UTF_8),
			hints,
			results
		) as Int
		if (status != 0) TODO("Get addr info errors")

		fun getSockAddr(addrData: MemorySegment): ResolutionDataPartIdentifier {
			return when (domain) {
//				AF_INET -> InternetProtocolV4AddressData(
//					(sockaddr_in_sin_addr.invokeExact(addrData, 0L) as MemorySegment)
//						.toArray(ValueLayout.JAVA_BYTE)
//				)
//
				AF_INET6 -> InternetProtocolV6AddressData(
					(sockaddr_in6_sin6_addr.invokeExact(addrData, 0L) as MemorySegment)
						.toArray(ValueLayout.JAVA_BYTE)
				)

				else -> TODO("ADDRESS FAMILY $domain")
			}
		}

		var resultSegment = results.get(ValueLayout.ADDRESS, 0)
			.reinterpret(addrinfo.byteSize(), tempArena) { nativeFreeAddrInfo!!.invokeExact(it) }
		while (resultSegment != MemorySegment.NULL) {
			val dataPart = mutableListOf<ResolutionDataPartIdentifier>()
			val addrData = (addrinfo_ai_addr.get(resultSegment, 0L) as MemorySegment)
				.reinterpret(addrinfo_ai_addrlen.get(resultSegment, 0L) as Long)
			dataPart.add(getSockAddr(addrData))
			data.add(ResolutionDataPart(dataPart))
			resultSegment = (addrinfo_ai_next.get(resultSegment, 0L) as MemorySegment)
				.reinterpret(addrinfo.byteSize())
		}
	}
	return data
}