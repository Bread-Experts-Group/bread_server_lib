package org.bread_experts_group.socket

/**
 * An error involving the internals of a socket has failed, such as the kernel returning an invalid parameter error.
 * @author Miko Elbrecht
 * @since 4.0.0
 */
abstract class InternalSocketException(message: String) : OperatingSystemException(message)