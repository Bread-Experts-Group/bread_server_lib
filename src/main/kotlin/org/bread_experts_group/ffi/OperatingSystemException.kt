package org.bread_experts_group.ffi

import java.io.IOException

/**
 * An error has occurred while interacting with the operating system natively.
 * @author Miko Elbrecht
 * @since D0F0N0P0
 */
open class OperatingSystemException(message: String) : IOException(message)