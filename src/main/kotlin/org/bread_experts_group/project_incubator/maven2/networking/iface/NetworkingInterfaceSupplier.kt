package org.bread_experts_group.project_incubator.maven2.networking

import org.bread_experts_group.project_incubator.maven2.networking.iface.NetworkingInterface

interface NetworkingInterfaceSupplier : NetworkingDevice {
	fun interfaces(): Result<Iterator<NetworkingInterface>>
}