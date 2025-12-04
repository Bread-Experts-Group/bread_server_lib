package org.bread_experts_group.api.system.socket.listen

import org.bread_experts_group.api.system.socket.ipv4.listen.IPv4ListenFeatureIdentifier
import org.bread_experts_group.api.system.socket.ipv6.listen.IPv6ListenFeatureIdentifier

data class WindowsListenBacklogFeature(val backlog: Int) : IPv4ListenFeatureIdentifier, IPv6ListenFeatureIdentifier