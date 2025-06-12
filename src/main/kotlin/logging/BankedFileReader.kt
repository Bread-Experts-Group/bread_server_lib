package org.bread_experts_group.logging

import org.bread_experts_group.logging.BankedFileHandler.Companion.readMemoryBank
import org.bread_experts_group.logging.ansi_colorspace.ANSI16
import org.bread_experts_group.logging.ansi_colorspace.ANSI16Color
import org.bread_experts_group.stream.readExtensibleLong
import org.bread_experts_group.stream.readExtensibleULong
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.time.Instant
import java.util.logging.Level
import java.util.logging.LogRecord

class BankedFileReader(
	bankPath: Path,
	contentPath: Path,
	timestampPath: Path
) {
	constructor(fromHandler: BankedFileHandler) : this(
		fromHandler.bankPath,
		fromHandler.contentPath,
		fromHandler.timestampPath
	)

	val bank: FileChannel = FileChannel.open(
		bankPath,
		StandardOpenOption.CREATE, StandardOpenOption.READ
	)
	val content: FileChannel = FileChannel.open(
		contentPath,
		StandardOpenOption.CREATE, StandardOpenOption.READ
	)
	val timestamp: FileChannel = FileChannel.open(
		timestampPath,
		StandardOpenOption.CREATE, StandardOpenOption.READ
	)
	val memoryBank = readMemoryBank(bank)
	val timestamps = buildList {
		timestamp.position(10)
		while (timestamp.position() < timestamp.size()) {
			val initialPosition = timestamp.position()
			val backed = ByteBuffer.allocate(10)
			timestamp.read(backed)
			val data = backed.array().inputStream()
			add(data.readExtensibleULong())
			timestamp.position(initialPosition + (backed.capacity() - data.available()))
		}
	}

	var nanos = 0L
	fun nextRecord(): LogRecord {
		val initialPosition = content.position()
		val data = ByteBuffer.allocate(90)
		content.read(data)
		val stream = data.array().inputStream()
		val time = timestamps[stream.readExtensibleULong().toInt()].let {
			nanos += stream.readExtensibleLong()
			Instant.ofEpochSecond(
				it.toLong(),
				nanos
			)
		}
		val (levelName, resourceBundleName) = stream.readExtensibleULong().let {
			if (it and 1u == 1uL) {
				val bundleName = memoryBank[stream.readExtensibleULong().toInt()]
				bundleName to memoryBank[(it shr 1).toInt()]
			} else memoryBank[(it shr 1).toInt()] to null
		}
		val levelValue = stream.readExtensibleLong().toInt()
		val level = try {
			Level.parse(levelName)
		} catch (_: IllegalArgumentException) {
			ColoredLevel(levelName, ANSI16Color(ANSI16.MAGENTA), levelValue, resourceBundleName)
		}
		val loggerName = memoryBank[stream.readExtensibleULong().toInt()]
		val threadID = stream.readExtensibleLong()
		val messageLength = stream.readExtensibleULong()
		val messageData = ByteBuffer.allocate((messageLength * 10u).toInt())
		val messageStream = messageData.array().inputStream()
		content.position(initialPosition + (data.capacity() - stream.available()))
		val dataPosition = content.position()
		content.read(messageData)
		val message = buildString {
			val length = messageLength.toInt()
			repeat(length) {
				append(memoryBank[messageStream.readExtensibleULong().toInt()])
				append(' ')
			}
		}
		content.position(dataPosition + (messageData.capacity() - messageStream.available()))
		val record = LogRecord(level, message)
		record.instant = time
		record.loggerName = loggerName
		record.longThreadID = threadID
		record.sourceMethodName = "readback"
		return record
	}
}