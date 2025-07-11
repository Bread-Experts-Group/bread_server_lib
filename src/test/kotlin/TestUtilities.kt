package org.bread_experts_group

import org.bread_experts_group.coder.LazyPartialResult
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
	if (this is Iterable<*>) this.forEach {
		if (it is LazyPartialResult<*, *>) it.result?.dumpLog(logger, "$prepend ")
		else it?.dumpLog(logger, "$prepend ")
	}
}

fun Any.dumpLogSafe(logger: Logger, prepend: String = "") {
	logger.info(prepend + this.toString())
	if (this is Iterable<*>) this.forEach {
		if (it is LazyPartialResult<*, *>) it.resultSafe?.dumpLogSafe(logger, "$prepend ")
		else it?.dumpLogSafe(logger, "$prepend ")
	}
}