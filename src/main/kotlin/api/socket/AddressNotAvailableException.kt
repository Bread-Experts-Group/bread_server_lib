package org.bread_experts_group.api.socket

import org.bread_experts_group.ffi.OperatingSystemException

/**
 * An attempt to bind, connect, or some other network operation has failed because an address provided was
 * not available for the current system.
 * @author Miko Elbrecht
 * @since 4.0.0
 */
class AddressNotAvailableException : OperatingSystemException("Address not available.")