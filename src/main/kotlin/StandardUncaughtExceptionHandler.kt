package org.bread_experts_group

import java.util.logging.Level
import java.util.logging.Logger

class StandardUncaughtExceptionHandler(private val use: Logger) : Thread.UncaughtExceptionHandler {
	override fun uncaughtException(t: Thread?, e: Throwable?) {
		use.log(Level.SEVERE, e) { "General failure during thread [$t] operation" }
	}
}