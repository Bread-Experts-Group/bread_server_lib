package org.bread_experts_group.api.socket.windows

import org.bread_experts_group.coder.MappedEnumeration
import org.bread_experts_group.ffi.debugString
import org.bread_experts_group.ffi.windows.WindowsGUID
import java.lang.foreign.MemorySegment
import java.nio.ByteOrder
import java.util.*

data class WSAProtocol(
	val serviceFlags1: EnumSet<WSAProtocolServiceFlags1>,
	val serviceFlags2: Int,
	val serviceFlags3: Int,
	val serviceFlags4: Int,
	val providerFlags: EnumSet<WSAProviderFlags>,
	val guid: WindowsGUID,
	val catalogID: Int,
	val protocolChain: List<Int>,
	val version: Int,
	val addressFamily: MappedEnumeration<Int, WSAAddressFamily>,
	val addressMax: Int,
	val addressMin: Int,
	val socketType: MappedEnumeration<Int, WSASocketType>,
	val protocol: MappedEnumeration<Int, WSAProtocolDefinition>,
	val protocolMaxOffset: Int,
	val endian: ByteOrder,
	val securityScheme: MappedEnumeration<Int, WSASecurityScheme>,
	val maximumMessageSize: UInt,
	val providerReserved: UInt,
	val name: String,
	val pointer: MemorySegment
) {
	override fun toString(): String = "$name ($guid, version $version, #$catalogID)" +
			"\n\tAddress Family / Socket Type: $addressFamily / $socketType" +
			"\n\tProtocol: $protocol [+ $protocolMaxOffset]" +
			"\n\tAddresses: $addressMin .. $addressMax byte(s)" +
			"\n\tMessage Size: $maximumMessageSize byte(s)" +
			"\n\tEndian / Security: $endian / $securityScheme" +
			"\n\t[$providerReserved] [$protocolChain]" +
			"\n\tService flags: $serviceFlags1" +
			"\n\t               [$serviceFlags2, $serviceFlags3, $serviceFlags4]" +
			"\n\tProvider flags: $providerFlags" +
			"\n\t[${pointer.debugString()}]"
}