package org.bread_experts_group.socket

/**
 * A socket has already been bound to an address that is not the provided address.
 * @author Miko Elbrecht
 * @since 4.0.0
 */
class SocketAlreadyBoundException(message: String) : OperatingSystemException(message)