package org.bread_experts_group.api.vfs

import org.bread_experts_group.ffi.OperatingSystemException

/**
 * No virtual file system implementation was found for the current operating system.
 * @author Miko Elbrecht
 * @since 4.0.0
 */
class NoVirtualFileSystemAvailableException(message: String) : OperatingSystemException(message)