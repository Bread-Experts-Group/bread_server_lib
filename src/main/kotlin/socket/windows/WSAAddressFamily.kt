package org.bread_experts_group.socket.windows

import org.bread_experts_group.coder.Mappable

enum class WSAAddressFamily(override val id: Int, override val tag: String) : Mappable<WSAAddressFamily, Int> {
	AF_UNIX(1, "Unix"),
	AF_INET(2, "Internet Protocol, version 4"),
	AF_IMPLINK(3, "ARPANET IMP"),
	AF_PUP(4, "PUP Protocols"),
	AF_CHAOS(5, "Chaos (MIT) Protocols"),
	AF_IPX(6, "NWLink IPX/SPX"),
	AF_ISO(7, "ISO Protocols"),
	AF_ECMA(8, "ECMA Protocols"),
	AF_DATAKIT(9, "Datakit Protocols"),
	AF_CCITT(10, "CCITT Protocols"),
	AF_SNA(11, "IBM SNA"),
	AF_DECNET(12, "DECnet"),
	AF_DLI(13, "Direct Data Link"),
	AF_LAT(14, "LAT"),
	AF_HYLINK(15, "NSC Hyperchannel"),
	AF_APPLETALK(16, "AppleTalk"),
	AF_NETBIOS(17, "NetBIOS"),
	AF_VOICEVIEW(18, "VoiceView"),
	AF_FIREFOX(19, "Firefox Protocols"),
	AF_UNKNOWN1(20, "Somebody is using this? (winsdk)"),
	AF_BAN(21, "Banyan"),
	AF_ATM(22, "ATM"),
	AF_INET6(23, "Internet Protocol, version 6"),
	AF_CLUSTER(24, "Microsoft Wolfpack"),
	AF_12844(25, "IEEE 1284.4 WG AF"),
	AF_IRDA(26, "Infrared Data Association (IrDA)"),
	AF_NETDES(28, "Network Designers OSI & Gateway"),
	AF_TCNPROCESS(29, "TCNPROCESS [unspec]"),
	AF_TCNMESSAGE(30, "TCNMESSAGE [unspec]"),
	AF_ICLFXBM(31, "ICLFXBM"),
	AF_BTH(32, "Bluetooth"),
	AF_LINK(33, "LINK [unspec]"),
	AF_HYPERV(34, "Hyper-V");

	override fun toString(): String = stringForm()
}