package org.bread_experts_group.protocol.http

import java.nio.channels.Selector
import java.nio.channels.SocketChannel

class SocketHTTPProtocolSelector(
	selector: Selector,
	on: SocketChannel
) : HTTPProtocolSelector(on, on) {

}