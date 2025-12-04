package org.bread_experts_group.project_incubator.dns

import org.bread_experts_group.MappedEnumeration
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

abstract class DNSResourceRecord(
	var domain: String,
	val recordType: MappedEnumeration<UShort, DNSType>,
	val recordClass: MappedEnumeration<UShort, DNSClass>,
	val timeToLive: Duration
) {
	class Generic(
		domain: String,
		recordType: MappedEnumeration<UShort, DNSType>,
		recordClass: MappedEnumeration<UShort, DNSClass>,
		timeToLive: Duration,
		val data: ByteArray
	) : DNSResourceRecord(domain, recordType, recordClass, timeToLive) {
		override fun toString(): String = super.toString() + "\nGeneric (${data.size} bytes)"
	}

	class IPv4Address(
		domain: String,
		recordClass: MappedEnumeration<UShort, DNSClass>,
		timeToLive: Duration,
		val address: ByteArray
	) : DNSResourceRecord(domain, MappedEnumeration(DNSType.A), recordClass, timeToLive) {
		override fun toString(): String = super.toString() + "\nAddress: " +
				address.joinToString(".") { it.toUByte().toString() }
	}

	class AuthoritativeNameServer(
		domain: String,
		recordClass: MappedEnumeration<UShort, DNSClass>,
		timeToLive: Duration,
		val nameServerDomainName: String
	) : DNSResourceRecord(domain, MappedEnumeration(DNSType.NS), recordClass, timeToLive) {
		override fun toString(): String = super.toString() + "\nNS Domain: \"$nameServerDomainName\""
	}

	class CanonicalName(
		domain: String,
		recordClass: MappedEnumeration<UShort, DNSClass>,
		timeToLive: Duration,
		val canonicalName: String
	) : DNSResourceRecord(domain, MappedEnumeration(DNSType.CNAME), recordClass, timeToLive) {
		override fun toString(): String = super.toString() + "\nCanonical Name: \"$canonicalName\""
	}

	class StartOfAuthority(
		domain: String,
		recordClass: MappedEnumeration<UShort, DNSClass>,
		timeToLive: Duration,
		val nameServer: String,
		val responsibleMailbox: String,
		val serial: UInt,
		val refresh: Duration,
		val retry: Duration,
		val expire: Duration,
		val minimumTTl: Duration
	) : DNSResourceRecord(domain, MappedEnumeration(DNSType.SOA), recordClass, timeToLive) {
		override fun toString(): String = super.toString() + "\nAuthoritative Name Server: \"$nameServer\", " +
				"Responsible Mailbox: $responsibleMailbox, Serial ${serial.toHexString()}, " +
				"Refreshes: $refresh, Retry In: $retry, Expires: $expire, Minimum TTL: $minimumTTl"
	}

	class MailExchange(
		domain: String,
		recordClass: MappedEnumeration<UShort, DNSClass>,
		timeToLive: Duration,
		val preference: UShort,
		val exchange: String
	) : DNSResourceRecord(domain, MappedEnumeration(DNSType.MX), recordClass, timeToLive) {
		override fun toString(): String = super.toString() + "\nExchange Name: \"$exchange\" (${preference} pref.)"
	}

	class IPv6Address(
		domain: String,
		recordClass: MappedEnumeration<UShort, DNSClass>,
		timeToLive: Duration,
		val address: ByteArray
	) : DNSResourceRecord(domain, MappedEnumeration(DNSType.AAAA), recordClass, timeToLive) {
		override fun toString(): String = super.toString() + "\nAddress: " +
				address.joinToString(":") { it.toUShort().toHexString() }
	}

	class EDNSOpt(
		val udpPayloadSize: UShort,
		val responseCode: MappedEnumeration<UInt, EDNS0ResponseCode>,
		val version: UByte,
		val dnssecOK: Boolean,
		val options: List<EDNS0Option> = emptyList()
	) : DNSResourceRecord(
		".",
		MappedEnumeration(DNSType.OPT),
		MappedEnumeration(udpPayloadSize),
		(
				(responseCode.raw.toLong() shl 24) or (version.toLong() shl 16) or
						(if (dnssecOK) (1 shl 15) else 0)
				).toDuration(DurationUnit.SECONDS),
	) {
		override fun toString(): String = super.toString() + "\nUDP Payload Size: $udpPayloadSize, " +
				"Response Code: $responseCode, v$version [${if (dnssecOK) "DNSSEC OK" else ""}]" +
				"\n$options"
	}

	override fun toString(): String = "DNS RR \"$domain\" @ $recordClass ($recordType), TTL $timeToLive"
}