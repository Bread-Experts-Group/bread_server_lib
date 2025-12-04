package org.bread_experts_group.project_incubator.dns

import org.bread_experts_group.Mappable

enum class DNSType(override val id: UShort, override val tag: String) : Mappable<DNSType, UShort> {
	A(1u, "IPv4 Host Address"),
	NS(2u, "Authoritative Name Server"),
	MD(3u, "Mail Destination (obsolete, use MX)"),
	MF(4u, "Mail Forwarder (obsolete, use MX)"),
	CNAME(5u, "Canonical Name for an alias"),
	SOA(6u, "Start of Authority"),
	MB(7u, "Mailbox Domain Name"),
	MG(8u, "Mail Group Member"),
	MR(9u, "Mail Rename Domain Name"),
	NULL(10u, "Null Record"),
	WKS(11u, "Well Known Service"),
	PTR(12u, "Domain Pointer"),
	HINFO(13u, "Host Information"),
	MINFO(14u, "Mailbox / Mail List Information"),
	MX(15u, "Mail Exchange"),
	TXT(16u, "Textual Data"),
	RP(17u, "Responsible Person"),
	AFSDB(18u, "AFS Data Base Location"),
	X25(19u, "X.25 PSDN Address"),
	ISDN(20u, "ISDN Address"),
	RT(21u, "Route Through"),
	NSAP(22u, "NSAP Address (deprecated)"),
	NSAP_PTR(23u, "Pointer to NSAP Address (deprecated)"),
	SIG(24u, "Security Signature"),
	KEY(25u, "Security Key"),
	PX(26u, "X.400 Mail Mapping Information"),
	GPOS(27u, "Geographical Position"),
	AAAA(28u, "IPv6 Host Address"),
	LOC(29u, "Location Information"),
	NXT(30u, "Next Domain (obsolete)"),
	EID(31u, "Endpoint Identifier"),
	NIMLOC(32u, "Nimrod Locator"),
	SRV(33u, "Server Selection"),
	ATMA(34u, "ATM Address"),
	NAPTR(35u, "Naming Authority Pointer"),
	KX(36u, "Key Exchange"),
	CERT(37u, "Cert"),
	A6(38u, "A6 (obsolete, use AAAA)"),
	DNAME(39u, "DNAME"),
	SINK(40u, "Sink"),
	OPT(41u, "EDNS(0) Opt");

	override fun toString(): String = stringForm()
}