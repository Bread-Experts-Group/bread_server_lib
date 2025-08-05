package org.bread_experts_group.socket

import java.io.IOException

/**
 * An error has occurred while interacting with the operating system natively.
 * @author Miko Elbrecht
 * @since 4.0.0
 */
abstract class OperatingSystemException(message: String) : IOException(message)