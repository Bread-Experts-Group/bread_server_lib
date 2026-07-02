package org.bread_experts_group.model.natives.nt.datatype

import org.bread_experts_group.model.natives.*

const val MAX_ADAPTER_ADDRESS_LENGTH = 8L

const val MAX_DHCPV6_DUID_LENGTH = 130L

abstract class _IP_ADAPTER_ADDRESSES_LH : Structure<_IP_ADAPTER_ADDRESSES_LH> {
	@Order(0)
	abstract var Union1: U1

	@Order(1)
	abstract var Next: Pointer<_IP_ADAPTER_ADDRESSES_LH>

	@Order(2)
	abstract var AdapterName: Pointer<NativeArray<CHAR>> // Actually a PCHAR

	@Order(3)
	abstract var FirstUnicastAddress: PIP_ADAPTER_UNICAST_ADDRESS_LH

	@Order(4)
	abstract var FirstAnycastAddress: PIP_ADAPTER_ANYCAST_ADDRESS_XP

	@Order(5)
	abstract var FirstMulticastAddress: PIP_ADAPTER_MULTICAST_ADDRESS_XP

	@Order(6)
	abstract var FirstDnsServerAddress: PIP_ADAPTER_DNS_SERVER_ADDRESS_XP

	@Order(7)
	abstract var DnsSuffix: PWCHAR

	@Order(8)
	abstract var Description: PWCHAR

	@Order(9)
	abstract var FriendlyName: PWCHAR

	@Order(10)
	abstract var PhysicalAddress: @ArraySize(MAX_ADAPTER_ADDRESS_LENGTH) NativeArray<BYTE>

	@Order(11)
	abstract var PhysicalAddressLength: ULONG

	@Order(12)
	abstract var Flags: ULONG

	@Order(13)
	abstract var Mtu: ULONG

	@Order(14)
	abstract var IfType: IFTYPE

	@Order(15)
	abstract var OperStatus: IF_OPER_STATUS

	@Order(16)
	abstract var Ipv6IfIndex: IF_INDEX

	@Order(17)
	abstract var ZoneIndices: @ArraySize(16) NativeArray<ULONG>

	@Order(18)
	abstract var FirstPrefix: PIP_ADAPTER_PREFIX_XP

	@Order(19)
	abstract var TransmitLinkSpeed: ULONG64

	@Order(20)
	abstract var ReceiveLinkSpeed: ULONG64

	@Order(21)
	abstract var FirstWinsServerAddress: PIP_ADAPTER_WINS_SERVER_ADDRESS_LH

	@Order(22)
	abstract var FirstGatewayAddress: PIP_ADAPTER_GATEWAY_ADDRESS_LH

	@Order(23)
	abstract var Ipv4Metric: ULONG

	@Order(24)
	abstract var Ipv6Metric: ULONG

	@Order(25)
	abstract var Luid: IF_LUID

	@Order(26)
	abstract var Dhcpv4Server: SOCKET_ADDRESS

	@Order(27)
	abstract var CompartmentId: NET_IF_COMPARTMENT_ID

	@Order(28)
	abstract var NetworkGuid: NET_IF_NETWORK_GUID

	@Order(29)
	abstract var ConnectionType: NET_IF_CONNECTION_TYPE

	@Order(30)
	abstract var TunnelType: TUNNEL_TYPE

	@Order(31)
	abstract var Dhcpv6Server: SOCKET_ADDRESS

	@Order(32)
	abstract var Dhcpv6ClientDuid: @ArraySize(MAX_DHCPV6_DUID_LENGTH) NativeArray<BYTE>

	@Order(33)
	abstract var Dhcpv6ClientDuidLength: ULONG

	@Order(34)
	abstract var Dhcpv6Iaid: ULONG

	@Order(35)
	abstract var FirstDnsSuffix: PIP_ADAPTER_DNS_SUFFIX

	abstract class U1 : Structure<U1> {
		@Order(0)
		abstract var Alignment: ULONGLONG

		@Order(0)
		abstract var Structure: S

		abstract class S : Structure<S> {
			@Order(0)
			abstract var Length: ULONG

			@Order(1)
			abstract var IfIndex: IF_INDEX
		}
	}
}

typealias IP_ADAPTER_ADDRESSES_LH = _IP_ADAPTER_ADDRESSES_LH
typealias PIP_ADAPTER_ADDRESSES_LH = Pointer<_IP_ADAPTER_ADDRESSES_LH>