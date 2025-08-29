package org.bread_experts_group.socket

import org.bread_experts_group.ffi.OperatingSystemException

/**
 * A socket has already been bound to an address that is not the provided address.
 * @author Miko Elbrecht
 * @since 4.0.0
 */
class SocketAlreadyBoundException(message: String) : OperatingSystemException(message)