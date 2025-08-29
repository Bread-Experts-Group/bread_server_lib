package org.bread_experts_group.socket

import org.bread_experts_group.ffi.OperatingSystemException

/**
 * No socket implementation was found for the current operating system that would suffice all provided parameters;
 * such as the lack of networking drivers on the system, or the system does not have a BSL implementation yet.
 * @author Miko Elbrecht
 * @since 4.0.0
 */
class NoSocketAvailableException(message: String) : OperatingSystemException(message)