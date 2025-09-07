package org.bread_experts_group.api.vfs

import org.junit.jupiter.api.Test
import java.net.URI
import java.nio.file.FileSystems
import java.nio.file.Files

class BreadExpertsGroupFileSystemTest {
	@Test
	fun getFileSystem() {
		val bfs = FileSystems.newFileSystem(
			URI("begfs:///?image=./test.bfsimg"),
			mapOf<String, Any>()
		)
		Files.createFile(bfs.getPath("./test.bfsimg"))
	}
}