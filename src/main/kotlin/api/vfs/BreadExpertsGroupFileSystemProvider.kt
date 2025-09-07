package org.bread_experts_group.api.vfs

import java.net.URI
import java.nio.channels.SeekableByteChannel
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileAttribute
import java.nio.file.attribute.FileAttributeView
import java.nio.file.spi.FileSystemProvider
import kotlin.io.path.Path

/**
 * Implementation of the **EXPERIMENTAL** Bread Experts Group Filesystem.
 * ## Data Types
 * * *FSBS:* FileSystem block size, as defined in FileSystem Initial Block.
 * * *Block:* FSBS-octet segment for general purpose data.
 * * *System block:* Block designated for dynamic number storage, preceding file descriptors.
 * * *Schema block:* Block designated for dynamic number storage, for use filesystem-wide.
 * * **UDI:** Dynamic Unsigned Integer
 * * **YUDIP:** Dynamic Unsigned Integer, System-Block Pointed
 *    * ```text
 *      [UDI] System Block (relative offset since last system block)
 *      [UDI] System Block Entry #
 *      ```
 * * **SUDIP:** Dynamic Unsigned Integer, Schema-Block Pointed
 *    * ```text
 *      [UDI] Schema Block
 *      [UDI] Schema Block Entry #
 *      ```
 * * **YDIP:** Dynamic Signed Integer, System-Block Pointed
 * * **SDIP:** Dynamic Signed Integer, Schema-Block Pointed
 * * **EDYDIP:** Extent-Derived YDIP: the true value of the YDIP is shifted left by 2 bits. If bit 0 is set,
 * it indicates the next YDIP (non-EDYDIP) is the end of a range (inclusively) defined at the start of this EDYDIP. If
 * bit 1 is set, it indicates the extent range addresses a multiple of FSBS octets; otherwise, direct octet addressing.
 * The addressed location is derived from the current sector (if bit 1 is set), or the current byte (if bit 1 is clear).
 * * **EDYUDIP:** Extent-Derived YUDIP, follows the same rules as EDYDIP, but addresses in one-way only; the base
 * location is context-specific, and may be 0 (start of volume).
 * ### Capability Block
 * ```text
 * [SUDIP] Capability Identifier
 * [ *** ] Capability Data
 * ```
 * ### FileSystem Initial Block
 * ```text
 * [DIP] FileSystem Specification Version
 * [DIP] Block Size
 * ```
 * ### FileSystem Information Block
 * ```text
 * [YUDIP] Free Block Count
 *   [EDYUDIP] Free Block(s)
 * [YUDIP] Capability Count
 *   [EDYDIP] Capability Locations
 * ```
 * ## File Descriptor
 * ```text
 * [YUDIP] File Block Count
 *   [EDYDIP] File Block Location(s)
 * [YUDIP] Capability Count
 *   [EDYDIP] Capability Location(s)
 * ```
 * ### Directories
 * #### Defined Schema
 * * **Defined Schema Name:** String Encoding
 * * **Defined Schema Identifier:** 1
 * ```text
 * [   0] ASCII
 * [   1] UTF-8
 * ```
 * #### Defined Capability
 * * **Defined Capability Identifier:** 1
 * ```text
 * [YUDIP] File Reference Count
 *   [SUDIP] String Encoding
 *   [YUDIP] File Name Length (octets)
 *   [ *** ] File Name
 *   [EDYDIP] File Reference
 * ```
 * ### File Times
 * #### Defined Schema
 * * **Defined Schema Name:** Time Meaning
 * * **Defined Schema Identifier:** 0
 * ```text
 * [   0] Creation
 * [   1] Modification
 * [   2] Access
 * [   3] Free
 * ```
 * * **Defined Schema Name:** Time Unit
 * * **Defined Schema Identifier:** 1
 * ```text
 * [SDIP] Second Scale
 * [SDIP] Delta seconds from Jan 1, 1970 00:00:00 UTC
 * ```
 * #### Defined Capability
 * * **Defined Capability Identifier:** 0
 * ```text
 * [YUDIP] Time Count
 *   [SUDIP] Time Meaning
 *   [SUDIP] Time Unit
 *   [YUDIP] Unit Magnitude
 * ```
 * @author Miko Elbrecht
 * @since 4.1.0
 */
class BreadExpertsGroupFileSystemProvider : FileSystemProvider() {
	override fun getScheme(): String = "begfs"

	private val images = mutableMapOf<Path, BreadExpertsGroupImageFileSystem>()
	private fun URI.readParameters() = buildMap {
		this@readParameters.query.split('&').forEach {
			val split = it.split('=', limit = 2)
			this[split[0]] = split.getOrNull(1)
		}
	}

	override fun newFileSystem(
		uri: URI,
		env: Map<String, *>
	): FileSystem {
		val parameters = uri.readParameters()
		parameters["image"]?.let {
			val path = Path(it).toAbsolutePath().normalize()
			if (images.contains(path)) throw FileSystemAlreadyExistsException()
			val fs = BreadExpertsGroupImageFileSystem(this, path)
			images[path] = fs
			return fs
		}
		throw UnsupportedOperationException("Parameters: $parameters")
	}

	override fun getFileSystem(uri: URI): FileSystem {
		val parameters = uri.readParameters()
		parameters["image"]?.let {
			val path = Path(it).toAbsolutePath().normalize()
			return images[path] ?: throw FileSystemNotFoundException()
		}
		throw UnsupportedOperationException("Parameters: $parameters")
	}

	override fun getPath(uri: URI): Path = BreadExpertsGroupFileSystemPath(getFileSystem(uri))

	override fun newByteChannel(
		path: Path,
		options: Set<OpenOption>,
		vararg attrs: FileAttribute<*>
	): SeekableByteChannel? {
		val path = path as BreadExpertsGroupFileSystemPath
		val fileSystem = path.fileSystem
		if (!fileSystem.isOpen) throw FileSystemNotFoundException()
		when (fileSystem) {
			is BreadExpertsGroupImageFileSystem -> {
				return null
			}

			else -> throw UnsupportedOperationException("File system: $fileSystem")
		}
	}

	override fun newDirectoryStream(
		dir: Path,
		filter: DirectoryStream.Filter<in Path>
	): DirectoryStream<Path> {
		TODO("Not yet implemented")
	}

	override fun createDirectory(dir: Path, vararg attrs: FileAttribute<*>) {
		TODO("Not yet implemented")
	}

	override fun delete(path: Path) {
		TODO("Not yet implemented")
	}

	override fun copy(
		source: Path,
		target: Path,
		vararg options: CopyOption
	) {
		TODO("Not yet implemented")
	}

	override fun move(
		source: Path,
		target: Path,
		vararg options: CopyOption
	) {
		TODO("Not yet implemented")
	}

	override fun isSameFile(path: Path, path2: Path): Boolean {
		TODO("Not yet implemented")
	}

	override fun isHidden(path: Path): Boolean {
		TODO("Not yet implemented")
	}

	override fun getFileStore(path: Path): FileStore {
		TODO("Not yet implemented")
	}

	override fun checkAccess(path: Path, vararg modes: AccessMode) {
		TODO("Not yet implemented")
	}

	override fun <V : FileAttributeView> getFileAttributeView(
		path: Path,
		type: Class<V>,
		vararg options: LinkOption?
	): V {
		TODO("Not yet implemented")
	}

	override fun <A : BasicFileAttributes> readAttributes(
		path: Path,
		type: Class<A>,
		vararg options: LinkOption?
	): A {
		TODO("Not yet implemented")
	}

	override fun readAttributes(
		path: Path,
		attributes: String,
		vararg options: LinkOption
	): Map<String, Any> {
		TODO("Not yet implemented")
	}

	override fun setAttribute(
		path: Path,
		attribute: String,
		value: Any,
		vararg options: LinkOption?
	) {
		TODO("Not yet implemented")
	}
}