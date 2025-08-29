package org.bread_experts_group.socket

import org.bread_experts_group.ffi.OperatingSystemException

/**
 * An operation on a socket that requires binding has been called when the socket is unbound.
 * @author Miko Elbrecht
 * @since 4.0.0
 */
class SocketNotBoundException : OperatingSystemException("The socket has not been bound.")