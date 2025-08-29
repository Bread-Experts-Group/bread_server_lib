package org.bread_experts_group.vfs

import org.bread_experts_group.socket.NoSocketAvailableException
import java.nio.ByteBuffer
import java.nio.file.Path
import java.util.*

/**
 * An interface for implementing userspace program controlled virtual filesystems on the local computer.
 * @author Miko Elbrecht
 * @since 4.0.0
 */
abstract class VirtualFileSystem(
	val accepts: Array<String>
) {
	companion object {
		fun open(): VirtualFileSystem = ServiceLoader.load(VirtualFileSystem::class.java).firstOrNull {
			it.accepts.contains(System.getProperty("os.name"))
		} ?: throw NoSocketAvailableException(
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