package org.bread_experts_group.project_incubator.maven2.networking.iface.windows

import org.bread_experts_group.generic.Flaggable

enum class WindowsInterfaceSupplierFlags : Flaggable {
	GAA_FLAG_SKIP_UNICAST,
	GAA_FLAG_SKIP_ANYCAST,
	GAA_FLAG_SKIP_MULTICAST,
	GAA_FLAG_SKIP_DNS_SERVER,
	GAA_FLAG_INCLUDE_PREFIX,
	GAA_FLAG_SKIP_FRIENDLY_NAME,
	GAA_FLAG_INCLUDE_GATEWAYS,
	GAA_FLAG_INCLUDE_ALL_INTERFACES,
	GAA_FLAG_INCLUDE_ALL_COMPARTMENTS,
	GAA_FLAG_INCLUDE_TUNNEL_BINDINGORDER;

	override val position: Long = 1L shl ordinal
}