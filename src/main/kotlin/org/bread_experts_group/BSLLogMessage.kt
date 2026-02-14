package org.bread_experts_group

import org.bread_experts_group.generic.logging.LevelLogger
import org.bread_experts_group.generic.logging.LogMessage
import java.util.logging.Level

data class BSLLogMessage(
	val level: Level,
	val message: String,
	val throwable: Throwable? = null
) : LogMessage {
	companion object {
		fun LevelLogger<BSLLogMessage>.log(l: Level, e: Throwable, s: () -> String) = this.log(
			BSLLogMessage(l, s(), e)
		)

		fun LevelLogger<BSLLogMessage>.finer(s: String) = this.log(
			BSLLogMessage(Level.FINER, s)
		)

		fun LevelLogger<BSLLogMessage>.finer(s: () -> String) = this.log(
			BSLLogMessage(Level.FINER, s())
		)

		fun LevelLogger<BSLLogMessage>.fine(s: String) = this.log(
			BSLLogMessage(Level.FINE, s)
		)

		fun LevelLogger<BSLLogMessage>.fine(s: () -> String) = this.log(
			BSLLogMessage(Level.FINE, s())
		)

		fun LevelLogger<BSLLogMessage>.info(s: String) = this.log(
			BSLLogMessage(Level.INFO, s)
		)

		fun LevelLogger<BSLLogMessage>.info(s: () -> String) = this.log(
			BSLLogMessage(Level.INFO, s())
		)

		fun LevelLogger<BSLLogMessage>.warning(s: String) = this.log(
			BSLLogMessage(Level.WARNING, s)
		)

		fun LevelLogger<BSLLogMessage>.warning(s: () -> String) = this.log(
			BSLLogMessage(Level.WARNING, s())
		)

		fun LevelLogger<BSLLogMessage>.severe(s: String) = this.log(
			BSLLogMessage(Level.SEVERE, s)
		)

		fun LevelLogger<BSLLogMessage>.severe(s: () -> String) = this.log(
			BSLLogMessage(Level.SEVERE, s())
		)
	}
}