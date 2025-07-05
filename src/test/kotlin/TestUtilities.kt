package org.bread_experts_group

import java.util.logging.Logger
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteRecursively

@OptIn(ExperimentalPathApi::class)
val testBase = Path("./src/test/out").also {
	it.deleteRecursively()
	it.createDirectories()
}

fun Any.dumpLog(logger: Logger, prepend: String = "") {
	logger.info(prepend + this.toString())
	if (this is Iterable<*>) this.forEach { it?.dumpLog(logger, "$prepend ") }
}