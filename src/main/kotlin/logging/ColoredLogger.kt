package org.bread_experts_group.logging

import org.bread_experts_group.logging.ansi_colorspace.ANSI16
import org.bread_experts_group.logging.ansi_colorspace.ANSI16Color
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.LogRecord
import java.util.logging.Logger
import kotlin.math.max

object ColoredLogger : Handler() {
	private val formatter = DateTimeFormatter.ofPattern("MM/dd HH:mm:ss.SSSSSS")
	private val levelNamePad = System.Logger.Level.entries.maxOf { it.name.length }
	private var closed: Boolean = false
	var coloring: Boolean = true
	var stackTraceOnlyInLoggingModule: Boolean = false
	var standardLevel: Level = Level.INFO

	private fun createExceptionMessage(
		record: LogRecord,
		prefix: ANSIString,
		exceptionName: String,
		exceptionMessage: List<String>,
		spaced: String,
		thrown: Throwable = record.thrown,
		hitThrown: Set<Throwable> = emptySet()
	): String = if (hitThrown.contains(thrown)) " =⁂= CYCLE THROWN CAUSE DETECTED =⁂= " else ansi {
		setResets = coloring
		color(ANSI16Color(ANSI16.LIGHT_GRAY)) {
			val fromModuleName = try {
				if (record.sourceClassName == null) null
				else this::class.java.classLoader.loadClass(record.sourceClassName).module.name
			} catch (_: ClassNotFoundException) {
				null
			}

			val stack = thrown.stackTrace
			var moduleNamePad = 0
			var moduleVersionPad = 0
			var filePad = 0
			var classLoadPad = 0
			var classNamePad = 0
			var methodNamePad = 0

			var truncatedCount = 0
			var firstTruncatedSet = false
			for (trace in stack) {
				if (stackTraceOnlyInLoggingModule && trace.moduleName != fromModuleName) {
					if (!firstTruncatedSet) truncatedCount++
					continue
				} else firstTruncatedSet = true
				if (trace.className.length > classNamePad) classNamePad = trace.className.length
				if (trace.methodName.length > methodNamePad) methodNamePad = trace.methodName.length
				if (trace.moduleName != null && trace.moduleName.length > moduleNamePad)
					moduleNamePad = trace.moduleName.length
				if (trace.moduleVersion != null && trace.moduleVersion.length > moduleVersionPad)
					moduleVersionPad = trace.moduleVersion.length
				val fileName = trace.fileName
				if (fileName != null && fileName.length > filePad) filePad = fileName.length
				val classLoaderName = trace.classLoaderName
				if (classLoaderName != null && classLoaderName.length > classLoadPad)
					classLoadPad = classLoaderName.length
			}

			var traceIndex = 0

			fun traceString() = "<$truncatedCount excluded trace${if (truncatedCount > 1) 's' else ""}>"
			fun logTruncated() = if (truncatedCount > 0) {
				color(ANSI16Color(ANSI16.LIGHT_GRAY)) { append("\n${traceString()}") }
				truncatedCount = 0
			} else null

			color(ANSI16Color(ANSI16.LIGHT_GRAY)) {
				var additionalOffset = 0
				if (truncatedCount > 0) traceString().also {
					additionalOffset = it.length
					append(it)
				}
				append(" ".repeat(max(0, prefix.length() - exceptionName.length - 3 - additionalOffset)))
				append('[')
				color(ANSI16Color(ANSI16.RED)) { append(exceptionName) }
				append(']')
			}
			append(' ')
			color(ANSI16Color(ANSI16.YELLOW)) {
				append(exceptionMessage[0])
				if (exceptionMessage.size > 1) append(
					('\n' + exceptionMessage[1]).replace("\n", "\n$spaced", true)
				)
			}
			for (trace in stack) {
				if (truncatedCount > 0) {
					truncatedCount--
					continue
				}
				if (stackTraceOnlyInLoggingModule && trace.moduleName != fromModuleName) {
					truncatedCount++
					traceIndex++
					continue
				}
				logTruncated()
				append('\n')
				if (traceIndex == 0) color(ANSI16Color(ANSI16.RED)) { append('.') }
				else append('^')
				append(" [")
				if (trace.moduleName != null || trace.moduleVersion != null) {
					append('[')
					color(ANSI16Color(ANSI16.BLUE)) {
						append((trace.moduleName ?: "").padEnd(moduleNamePad))
						color(ANSI16Color(ANSI16.LIGHT_GRAY)) { append(':') }
						append((trace.moduleVersion ?: "").padEnd(moduleVersionPad))
					}
					append("] ")
				} else append(" ".repeat(moduleNamePad + moduleVersionPad + 4))
				color(ANSI16Color(ANSI16.CYAN)) {
					append((trace.fileName ?: "").padEnd(filePad))
					color(ANSI16Color(ANSI16.LIGHT_GRAY)) { append(" | ") }
					append((trace.classLoaderName ?: "").padEnd(classLoadPad))
				}
				append("] ")
				color(ANSI16Color(ANSI16.GREEN)) { append(trace.className.padEnd(classNamePad)) }
				append('.')
				color(ANSI16Color(if (trace.isNativeMethod) ANSI16.MAGENTA else ANSI16.DEFAULT)) {
					append(trace.methodName.padEnd(methodNamePad))
				}
				if (trace.lineNumber > -1) {
					append(';')
					color(ANSI16Color(ANSI16.YELLOW)) { append(trace.lineNumber.toString()) }
				}
				traceIndex++
			}
			logTruncated()
		}
	}.build() + (thrown.cause?.let {
		'\n' + createExceptionMessage(
			record, prefix, exceptionName, exceptionMessage, spaced,
			it, hitThrown + thrown
		)
	} ?: "")

	val recentlyLogged = mutableSetOf<Long>()
	override fun publish(record: LogRecord) {
		if (!recentlyLogged.add(record.sequenceNumber)) return
		if (closed) return
		val prefix = ansi {
			setResets = coloring
			color(ANSI16Color(ANSI16.LIGHT_GRAY)) {
				append('[')
				color(
					when (val level = record.level) {
						Level.FINEST -> ANSI16Color(ANSI16.DARK_GRAY)
						Level.FINER -> ANSI16Color(ANSI16.LIGHT_GRAY)
						Level.FINE -> ANSI16Color(ANSI16.DEFAULT)
						Level.INFO -> ANSI16Color(ANSI16.CYAN)
						Level.WARNING -> ANSI16Color(ANSI16.YELLOW)
						Level.SEVERE -> ANSI16Color(ANSI16.RED)
						is ColoredLevel -> level.color
						else -> ANSI16Color(ANSI16.MAGENTA)
					}
				) { append(record.level.name.padEnd(levelNamePad)) }
				append(" | ")
				color(ANSI16Color(ANSI16.YELLOW)) {
					append(
						formatter.format(
							ZonedDateTime.ofInstant(
								record.instant,
								ZoneId.systemDefault()
							)
						)
					)
				}
				append(" | ")
				color(ANSI16Color(ANSI16.DEFAULT)) { append(record.loggerName) }
				append(" | ")
				color(ANSI16Color(ANSI16.DEFAULT)) {
					append(record.sourceMethodName)
					color(ANSI16Color(ANSI16.LIGHT_GRAY)) {
						append('[')
						color(ANSI16Color(ANSI16.CYAN)) { append(record.longThreadID.toString()) }
						append(']')
					}
				}
				append(']')
			}
			append(' ')
		}
		val spaced = " ".repeat(prefix.length())
		val paddedMessage = record.message.replace("\n", "\n$spaced", true)
		val fullMessage = if (record.thrown != null) {
			val exceptionName = record.thrown.javaClass.canonicalName ?: "???"
			val initialMessage = prefix.build() + paddedMessage
			val exceptionMessage = (record.thrown.localizedMessage ?: "<no message>")
				.split('\n', limit = 2, ignoreCase = true)
			initialMessage + '\n' + createExceptionMessage(record, prefix, exceptionName, exceptionMessage, spaced)
		} else prefix.build() + paddedMessage
		synchronized(System.out) {
			@Suppress("ReplaceJavaStaticMethodWithKotlinAnalog")
			System.out.println(fullMessage)
		}
	}

	override fun flush() {}

	override fun close() {
		closed = true
	}

	fun newLogger(name: String): Logger = Logger.getLogger(name).also {
		it.useParentHandlers = false
		it.addHandler(this)
		it.level = standardLevel
	}
}