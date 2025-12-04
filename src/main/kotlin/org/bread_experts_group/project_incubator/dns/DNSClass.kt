package org.bread_experts_group.project_incubator.dns

import org.bread_experts_group.Mappable

enum class DNSClass(override val id: UShort, override val tag: String) : Mappable<DNSClass, UShort> {
	Internet(1u, "Internet (IN)"),
	Chaos(3u, "Chaos (CH)"),
	Hesiod(4u, "Hesiod (HS)"),
	QCLASS_NONE(254u, "QCLASS NONE"),
	QCLASS_ANY(255u, "QCLASS *");

	override fun toString(): String = stringForm()
}