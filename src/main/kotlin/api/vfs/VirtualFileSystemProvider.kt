package org.bread_experts_group.api.vfs

import java.nio.ByteBuffer
import java.nio.file.Path
import java.util.*

/**
 * An interface for implementing userspace program controlled virtual filesystems on the local computer.
 * @author Miko Elbrecht
 * @since 4.0.0
 */
abstract class VirtualFileSystemProvider(
	val accepts: Array<String>
) {
	companion object {
		fun open(): VirtualFileSystemProvider = ServiceLoader.load(VirtualFileSystemProvider::class.java).firstOrNull {
			it.accepts.contains(System.getProperty("os.name"))
		} ?: throw NoVirtualFileSystemAvailableException(
			"No available virtual filesystem implementation could be found for [${System.getProperty("os.name")}]."
		)
	}

	var enumerateDirectory: (DirectoryEnumerationContext) -> Iterable<Pair<String, VirtualEntry>> = { emptyList() }
	var placeholderInformation: (PlaceholderInformationContext) -> VirtualEntry? = { null }
	var fileData: (FileDataContext) -> ByteBuffer? = { null }

	abstract fun setRoot(path: Path)

	abstract fun remove(
		path: Path,
		readOnly: Boolean = false, dirtyMetadata: Boolean = false, dirtyData: Boolean = false
	)

	abstract fun start()
	abstract fun stop()
}