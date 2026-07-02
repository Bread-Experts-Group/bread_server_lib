package org.bread_experts_group.project_incubator.maven2

import org.bread_experts_group.project_incubator.maven2.data.*
import org.bread_experts_group.project_incubator.maven2.data.windows.WindowsConnectionType
import org.bread_experts_group.project_incubator.maven2.data.windows.WindowsInterfaceType
import org.bread_experts_group.project_incubator.maven2.data.windows.WindowsOperationalStatus
import org.bread_experts_group.project_incubator.maven2.data.windows.WindowsTunnelType
import org.bread_experts_group.project_incubator.maven2.networking.NetworkingDevice
import org.bread_experts_group.project_incubator.maven2.networking.NetworkingInterfaceSupplier
import org.bread_experts_group.project_incubator.maven2.networking.iface.windows.WindowsInterfaceSupplierFlags
import org.bread_experts_group.project_incubator.maven2.networking.iface.windows.WindowsNetworkingInterfaceSupplier
import java.util.*


fun main() {
	val device = NetworkingDevice.new<NetworkingInterfaceSupplier>()!!
	(device as WindowsNetworkingInterfaceSupplier)
		.interfaces(flags = EnumSet.noneOf(WindowsInterfaceSupplierFlags::class.java))
		.getOrThrow()
		.forEach {
			println(it::class.java.interfaces.map { it.simpleName })
			if (it is FriendlyName) println(it.friendlyName)
			if (it is Name) println(it.name)
			if (it is Description) println(it.description)
			if (it is PhysicalAddress) println(it.physicalAddress.toHexString())
			if (it is WindowsTunnelType) println(it.tunnelType)
			if (it is WindowsConnectionType) println(it.connectionType)
			if (it is LinkSpeed.Rx) println("Rx...${it.linkSpeedRx}")
			if (it is LinkSpeed.Tx) println("Tx...${it.linkSpeedTx}")
			if (it is MaximumTransmissionUnit) println("Mtu...${it.mtu}")
			if (it is WindowsInterfaceType) println(it.interfaceType)
			if (it is WindowsOperationalStatus) println(it.operationalStatus)
			println()
		}
	val device2 = NetworkingDevice.new<NetworkingDevice>()!!
	println(device2)
}