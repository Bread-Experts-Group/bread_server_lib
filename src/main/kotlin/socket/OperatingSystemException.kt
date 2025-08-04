package org.bread_experts_group.socket

/**
 * An error has occurred while interacting with the operating system natively.
 * @author Miko Elbrecht
 * @since 4.0.0
 */
abstract class OperatingSystemException(message: String) : Exception(message)