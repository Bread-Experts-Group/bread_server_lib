package org.bread_experts_group.socket

import java.net.InetAddress
import java.net.SocketAddress

data class BSLInetSocketAddress(val address: InetAddress) : SocketAddress()