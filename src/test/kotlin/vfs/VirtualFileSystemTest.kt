package org.bread_experts_group.vfs

import org.bread_experts_group.logging.ColoredHandler
import org.junit.jupiter.api.Test
import java.nio.ByteBuffer
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.deleteRecursively

class VirtualFileSystemTest {
	val logger = ColoredHandler.newLogger("TMP logger")

	@OptIn(ExperimentalPathApi::class)
	@Test
	fun test() {
		val vfs = VirtualFileSystem.open()
		val vfsPath = Path("./test_vfs")
		vfsPath.createDirectory()
		vfs.setRoot(vfsPath)
		val files = mutableListOf(
			"Testfile.txt" to VirtualFile(400uL),
			"Testdirectory" to VirtualDirectory(),
			"Testdirectory2" to VirtualDirectory(),
		)
		vfs.enumerateDirectory = { if (it.of.toString() == "") files else emptyList() }
		vfs.placeholderInformation = { c -> files.firstOrNull { it.first == c.of.toString() }?.second }
		vfs.fileData = { ByteBuffer.wrap("${it.of}, ${it.offset}, ${it.size}".toByteArray()) }
		vfs.start()
		Thread.sleep(5000)
		files.removeIf { it.first == "Testfile.txt" }
		vfs.remove(Path("Testfile.txt"))
		Thread.sleep(5000)
		vfs.stop()
		vfsPath.deleteRecursively()
	}
}