package org.bread_experts_group.api.system.socket.ipv4.windows

import org.bread_experts_group.api.feature.CheckedImplementation
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.feature.windows.WindowsSystemNetworkingSocketsFeature.Companion.AF_INET
import org.bread_experts_group.api.system.feature.windows.WindowsSystemNetworkingSocketsFeature.Companion.IPPROTO_TCP
import org.bread_experts_group.api.system.feature.windows.WindowsSystemNetworkingSocketsFeature.Companion.SOCK_STREAM
import org.bread_experts_group.api.system.socket.ipv4.InternetProtocolV4AddressData
import org.bread_experts_group.api.system.socket.ipv4.stream.tcp.feature.IPV4TCPResolutionFeature
import org.bread_experts_group.api.system.socket.resolution.*
import org.bread_experts_group.api.system.socket.resolution_namespace_provider.ResolutionNamespaceProvider
import org.bread_experts_group.api.system.socket.resolution_namespace_provider.ResolutionNamespaceProviderFeatures
import org.bread_experts_group.api.system.socket.resolution_namespace_provider.type.ResolutionNamespaceTypeIdentifier
import org.bread_experts_group.api.system.socket.resolution_namespace_provider.type.StandardResolutionNamespaceTypes
import org.bread_experts_group.api.system.socket.resolution_namespace_provider.type.WindowsResolutionNamespaceTypes
import org.bread_experts_group.ffi.GUID
import org.bread_experts_group.ffi.windows.threadLocalPTR
import org.bread_experts_group.ffi.windows.wsa.*
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

class WindowsIPV4TCPResolutionFeature : IPV4TCPResolutionFeature(), CheckedImplementation {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeGetAddrInfoExW != null && nativeFreeAddrInfoExW != null

	companion object {
		const val NS_ALL = 0
		const val NS_DNS = 12
		const val NS_NLA = 15
		const val NS_BTH = 16
		const val NS_NTDS = 32
		const val NS_EMAIL = 37
		const val NS_PNRPNAME = 38
		const val NS_PNRPCLOUD = 39
	}

	override fun resolve(
		hostName: String,
		vararg features: ResolutionFeatureIdentifier
	): List<ResolutionDataIdentifier> {
		val data = mutableListOf<ResolutionDataIdentifier>()
		Arena.ofConfined().use { tempArena ->
			val name = tempArena.allocateFrom(hostName, Charsets.UTF_16LE)
			val hints = tempArena.allocate(ADDRINFOEXW)
			var flags = 0
			var fqdn = false
			var cn = false
			if (features.contains(WindowsResolutionFeatures.PASSIVE)) {
				flags = flags or 0x01
				data.add(WindowsResolutionFeatures.PASSIVE)
			}
			if (features.contains(WindowsResolutionFeatures.CANONICAL_NAME)) {
				cn = true
				flags = flags or 0x02
				data.add(WindowsResolutionFeatures.CANONICAL_NAME)
			}
			if (features.contains(WindowsResolutionFeatures.NUMERIC_HOST)) {
				flags = flags or 0x04
				data.add(WindowsResolutionFeatures.NUMERIC_HOST)
			}
			if (features.contains(WindowsResolutionFeatures.REQUIRE_CONFIGURED_GLOBAL_ADDRESS)) {
				flags = flags or 0x0400
				data.add(WindowsResolutionFeatures.REQUIRE_CONFIGURED_GLOBAL_ADDRESS)
			}
			if (features.contains(WindowsResolutionFeatures.FULLY_QUALIFIED_DOMAIN_NAME)) {
				fqdn = true
				flags = flags or 0x00020000
				data.add(WindowsResolutionFeatures.FULLY_QUALIFIED_DOMAIN_NAME)
			}
			if (features.contains(WindowsResolutionFeatures.HINT_FILE_SHARE_USE)) {
				flags = flags or 0x00040000
				data.add(WindowsResolutionFeatures.HINT_FILE_SHARE_USE)
			}
			if (features.contains(WindowsResolutionFeatures.DISABLE_IDN_ENCODING)) {
				flags = flags or 0x00080000
				data.add(WindowsResolutionFeatures.DISABLE_IDN_ENCODING)
			}
			val nameSpace = when (val id = features.firstNotNullOfOrNull { it as? ResolutionNamespaceTypeIdentifier }) {
				StandardResolutionNamespaceTypes.DOMAIN_NAME_SYSTEM -> {
					data.add(id)
					NS_DNS
				}

				StandardResolutionNamespaceTypes.BLUETOOTH -> {
					data.add(id)
					NS_BTH
				}

				StandardResolutionNamespaceTypes.EMAIL -> {
					data.add(id)
					NS_EMAIL
				}

				WindowsResolutionNamespaceTypes.P2P_NAME -> {
					data.add(id)
					NS_PNRPNAME
				}

				WindowsResolutionNamespaceTypes.P2P_COLLECTION -> {
					data.add(id)
					NS_PNRPCLOUD
				}

				WindowsResolutionNamespaceTypes.NT_DIRECTORY_SERVICE -> {
					data.add(id)
					NS_NTDS
				}

				else -> NS_ALL
			}
			val nsp = features.firstNotNullOfOrNull { it as? ResolutionNamespaceProvider }
			val nspGUID = nsp
				?.getOrNull(ResolutionNamespaceProviderFeatures.SYSTEM_IDENTIFIER)
				?.identifier as? GUID
			val nspArea = if (nspGUID != null) {
				data.add(nsp)
				nspGUID.allocate(tempArena)
			} else MemorySegment.NULL
			ADDRINFOEXW_ai_flags.set(hints, 0L, flags)
			ADDRINFOEXW_ai_family.set(hints, 0L, AF_INET)
			ADDRINFOEXW_ai_socktype.set(hints, 0L, SOCK_STREAM)
			ADDRINFOEXW_ai_protocol.set(hints, 0L, IPPROTO_TCP)
			val status = nativeGetAddrInfoExW!!.invokeExact(
				name,
				MemorySegment.NULL, // TODO SERVICE NAME
				nameSpace,
				nspArea,
				hints,
				threadLocalPTR,
				MemorySegment.NULL,
				MemorySegment.NULL,
				MemorySegment.NULL,
				MemorySegment.NULL
			) as Int
			if (status != 0) TODO("WS2 ERRORS $status")
			var rsvData = threadLocalPTR.get(ValueLayout.ADDRESS, 0)
			var iter = 0
			if (fqdn && cn) {
				while (rsvData != MemorySegment.NULL) {
					rsvData = rsvData.reinterpret(ADDRINFOEX2W.byteSize())
					val dataPart = mutableListOf<ResolutionDataPartIdentifier>()
					if (iter++ == 0) {
						dataPart.add(
							CanonicalNameData(
								(ADDRINFOEX2W_ai_canonname.get(rsvData, 0L) as MemorySegment)
									.reinterpret(Long.MAX_VALUE)
									.getString(0, Charsets.UTF_16LE)
							)
						)
						dataPart.add(
							FullyQualifiedDomainNameData(
								(ADDRINFOEX2W_ai_fqdn.get(rsvData, 0L) as MemorySegment)
									.reinterpret(Long.MAX_VALUE)
									.getString(0, Charsets.UTF_16LE)
							)
						)
					}
					val addrData = (ADDRINFOEX2W_ai_addr.get(rsvData, 0L) as MemorySegment)
						.reinterpret(ADDRINFOEX2W_ai_addrlen.get(rsvData, 0L) as Long)
					dataPart.add(
						InternetProtocolV4AddressData(
							(sockaddr_in_sin_addr.invokeExact(addrData, 0L) as MemorySegment)
								.toArray(ValueLayout.JAVA_BYTE)
						)
					)
					data.add(ResolutionDataPart(dataPart))
					rsvData = ADDRINFOEX2W_ai_next.get(rsvData, 0L) as MemorySegment
				}
			} else {
				while (rsvData != MemorySegment.NULL) {
					rsvData = rsvData.reinterpret(ADDRINFOEXW.byteSize())
					val dataPart = mutableListOf<ResolutionDataPartIdentifier>()
					if (iter++ == 0) {
						if (fqdn || cn) {
							val label = (ADDRINFOEXW_ai_canonname.get(rsvData, 0L) as MemorySegment)
								.reinterpret(Long.MAX_VALUE)
								.getString(0, Charsets.UTF_16LE)
							if (fqdn) dataPart.add(FullyQualifiedDomainNameData(label))
							else dataPart.add(CanonicalNameData(label))
						}
					}
					val addrData = (ADDRINFOEXW_ai_addr.get(rsvData, 0L) as MemorySegment)
						.reinterpret(ADDRINFOEXW_ai_addrlen.get(rsvData, 0L) as Long)
					dataPart.add(
						InternetProtocolV4AddressData(
							(sockaddr_in_sin_addr.invokeExact(addrData, 0L) as MemorySegment)
								.toArray(ValueLayout.JAVA_BYTE)
						)
					)
					data.add(ResolutionDataPart(dataPart))
					rsvData = ADDRINFOEXW_ai_next.get(rsvData, 0L) as MemorySegment
				}
			}
			nativeFreeAddrInfoExW!!.invoke(threadLocalPTR.get(ValueLayout.ADDRESS, 0))
		}
		return data
	}
}