package org.bread_experts_group.org.bread_experts_group

import java.nio.file.Files
import java.nio.file.Path
import java.util.logging.Logger
import kotlin.io.path.*

@OptIn(ExperimentalPathApi::class)
val testBase = Path("./src/test/out").also {
	it.deleteRecursively()
	it.createDirectories()
}

fun Any.dumpLog(logger: Logger, prepend: String = "") {
	logger.info(prepend + this.toString())
	if (this is Iterable<*>) this.forEach { it?.dumpLog(logger, "$prepend ") }
}

fun Any.dumpLogSafe(logger: Logger, prepend: String = "") {
	logger.info(prepend + this.toString())
	if (this is Iterable<*>) this.forEach { it?.dumpLogSafe(logger, "$prepend ") }
}

fun getResource(path: String): Path {
	val uri = (MissingResourceError::class.java.getResource(path)
		?: throw MissingResourceError(path)).toURI()
	return uri.toURL().openStream().use {
		val tempFile = Files.createTempFile("test", System.currentTimeMillis().toString())
		it.transferTo(tempFile.outputStream())
		tempFile
	}
}