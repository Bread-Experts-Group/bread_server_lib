package org.bread_experts_group.api.socket

import java.nio.channels.NetworkChannel

abstract class BSLSocket(
	protected val accepts: Array<String>
) : AddressableByteChannel, NetworkChannel