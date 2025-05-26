package org.bread_experts_group.dns.opt

import org.bread_experts_group.dns.DNSClass
import org.bread_experts_group.dns.DNSLabelLiteral
import org.bread_experts_group.dns.DNSResourceRecord
import org.bread_experts_group.dns.DNSType

class DNSOptionRecord(
	val dnsPayloadSize: Int,
	val responseCodeUpper: Int = 0,
	val eDNSVersion: Int = 0,
	val wantDNSSEC: Boolean = false,
	val options: List<DNSOption> = emptyList()
) : DNSResourceRecord(
	DNSLabelLiteral("."),
	DNSType.OPT__OPTION,
	DNSClass.OTHER,
	dnsPayloadSize,
	((eDNSVersion shl 8) or
			((responseCodeUpper and 0xFF0) shl 20) or
			(if (wantDNSSEC) 0x8000 else 0)).toLong(),
	byteArrayOf()
) {
	override fun toString(): String = buildString {
		append("(DNS, Option Record) UDP size: $dnsPayloadSize, RCODE upper: $responseCodeUpper, v$eDNSVersion")
		append("[${if (wantDNSSEC) "wants DNSSEC" else ""}], # OPTs: [${options.size}]")
		options.forEach { append("\n $it") }
	}
}