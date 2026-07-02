package org.bread_experts_group.project_incubator.maven2.networking.iface.windows

import org.bread_experts_group.generic.Mappable

enum class WindowsAddressFamilies(
	override val id: Int,
	override val tag: String
) : Mappable<WindowsAddressFamilies, Int> {
	AF_UNSPEC(0, "Unspecified"),
	AF_INET(2, "Internet Protocol v4"),
	AF_INET6(23, "Internet Protocol v6");
}