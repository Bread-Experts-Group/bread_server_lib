package org.bread_experts_group.project_incubator.dns

import org.bread_experts_group.Mappable

enum class DNSResponseCode(override val id: UInt, override val tag: String) : Mappable<DNSResponseCode, UInt> {
	NoError(0u, "No Error"),
	FormErr(1u, "Format Error"),
	ServFail(2u, "Server Failure"),
	NXDomain(3u, "Non-Existent Domain"),
	NotImp(4u, "Not Implemented"),
	Refused(5u, "Query Refused");

	override fun toString(): String = stringForm()
}