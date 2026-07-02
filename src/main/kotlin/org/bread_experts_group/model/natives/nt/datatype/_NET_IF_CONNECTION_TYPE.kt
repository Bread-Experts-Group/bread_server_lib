package org.bread_experts_group.model.natives.nt.datatype

import org.bread_experts_group.generic.Mappable
import org.bread_experts_group.model.natives.DatatypeBacked
import org.bread_experts_group.model.natives.Pointer

@DatatypeBacked("int")
enum class _NET_IF_CONNECTION_TYPE : Mappable<_NET_IF_CONNECTION_TYPE, Int> {
	NET_IF_CONNECTION_DEDICATED,
	NET_IF_CONNECTION_PASSIVE,
	NET_IF_CONNECTION_DEMAND,
	NET_IF_CONNECTION_MAXIMUM;

	override val id: Int = ordinal + 1
	override val tag: String = name
}

typealias NET_IF_CONNECTION_TYPE = _NET_IF_CONNECTION_TYPE
typealias PNET_IF_CONNECTION_TYPE = Pointer<_NET_IF_CONNECTION_TYPE>