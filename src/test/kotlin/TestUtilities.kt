package org.bread_experts_group

import java.util.logging.Logger

fun Any.dumpLog(logger: Logger, prepend: String = "") {
	logger.info(prepend + this.toString())
	if (this is Iterable<*>) this.forEach { it?.dumpLog(logger, "$prepend ") }
}