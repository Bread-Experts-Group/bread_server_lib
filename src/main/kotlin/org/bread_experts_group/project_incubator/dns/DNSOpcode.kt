package org.bread_experts_group.project_incubator.dns

import org.bread_experts_group.Mappable

enum class DNSOpcode(override val id: UInt, override val tag: String) : Mappable<DNSOpcode, UInt> {
	Query(0u, "Standard Query"),
	IQuery(1u, "Inverse Query"),
	Status(2u, "Server Status Request");

	override fun toString(): String = stringForm()
}