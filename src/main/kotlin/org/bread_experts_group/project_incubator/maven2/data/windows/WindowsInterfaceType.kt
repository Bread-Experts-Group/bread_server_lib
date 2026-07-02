package org.bread_experts_group.project_incubator.maven2.data.windows

import org.bread_experts_group.generic.MappedEnumeration
import org.bread_experts_group.project_incubator.maven2.networking.iface.windows.WindowsIFaceDefinitions

interface WindowsInterfaceType {
	val interfaceType: MappedEnumeration<Int, WindowsIFaceDefinitions>
}