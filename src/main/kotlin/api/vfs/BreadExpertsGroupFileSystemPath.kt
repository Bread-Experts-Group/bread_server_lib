package org.bread_experts_group.api.vfs

import java.net.URI
import java.nio.file.*

class BreadExpertsGroupFileSystemPath(
	private val of: FileSystem
) : Path {
	override fun getFileSystem(): FileSystem = this.of
	override fun isAbsolute(): Boolean {
		TODO("Not yet implemented")
	}

	override fun getRoot(): Path? {
		TODO("Not yet implemented")
	}

	override fun getFileName(): Path? {
		TODO("Not yet implemented")
	}

	override fun getParent(): Path? {
		TODO("Not yet implemented")
	}

	override fun getNameCount(): Int {
		TODO("Not yet implemented")
	}

	override fun getName(index: Int): Path {
		TODO("Not yet implemented")
	}

	override fun subpath(beginIndex: Int, endIndex: Int): Path {
		TODO("Not yet implemented")
	}

	override fun startsWith(other: Path): Boolean {
		TODO("Not yet implemented")
	}

	override fun endsWith(other: Path): Boolean {
		TODO("Not yet implemented")
	}

	override fun normalize(): Path {
		TODO("Not yet implemented")
	}

	override fun resolve(other: Path): Path {
		TODO("Not yet implemented")
	}

	override fun relativize(other: Path): Path {
		TODO("Not yet implemented")
	}

	override fun toUri(): URI {
		TODO("Not yet implemented")
	}

	override fun toAbsolutePath(): Path {
		TODO("Not yet implemented")
	}

	override fun toRealPath(vararg options: LinkOption): Path {
		TODO("Not yet implemented")
	}

	override fun register(
		watcher: WatchService,
		events: Array<out WatchEvent.Kind<*>>,
		vararg modifiers: WatchEvent.Modifier?
	): WatchKey {
		TODO("Not yet implemented")
	}

	override fun compareTo(other: Path): Int {
		TODO("Not yet implemented")
	}
}