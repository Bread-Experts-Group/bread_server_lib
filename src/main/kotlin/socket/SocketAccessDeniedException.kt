package org.bread_experts_group.socket

import org.bread_experts_group.ffi.OperatingSystemException

/**
 * An operation involving a socket was denied by the operating system, usually from a lack of permissions; e.g.
 * creating a [BSLInternetRawSocket] without Administrator on Windows systems.
 * @author Miko Elbrecht
 * @since 4.0.0
 */
class SocketAccessDeniedException : OperatingSystemException("Socket operation access was denied.")