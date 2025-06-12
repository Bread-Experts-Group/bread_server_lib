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

	fun createLevel(name: String, resourceBundle: String?, value: Int): Level = try {
		Level.parse(name)
	} catch (_: IllegalArgumentException) {
		ColoredLevel(name, ANSI16Color(ANSI16.MAGENTA), value, resourceBundle)
	}

	var nanos = 0L
	val savedLevels = mutableMapOf<ULong, Level>()
	val messages = mutableListOf<String>()
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
		val levelDescriptor = stream.readExtensibleULong()
		val levelIndex = levelDescriptor shr 1
		val level = if (levelDescriptor and 1u == 1uL) {
			val nameIndex = stream.readExtensibleULong()
			createLevel(
				memoryBank[nameIndex.toInt()],
				memoryBank[levelIndex.toInt()],
				stream.readExtensibleLong().toInt()
			).also { savedLevels[nameIndex] = it }
		} else {
			savedLevels[levelIndex] ?: createLevel(
				memoryBank[levelIndex.toInt()],
				null,
				stream.readExtensibleLong().toInt()
			).also { savedLevels[levelIndex] = it }
		}
		val loggerName = memoryBank[stream.readExtensibleULong().toInt()]
		val threadID = stream.readExtensibleLong()
		val messageDescriptor = stream.readExtensibleULong()
		val messageLength = (messageDescriptor shr 1).toInt()
		content.position(initialPosition + (data.capacity() - stream.available()))
		val message = if (messageDescriptor and 1u == 0uL) {
			val messageData = ByteBuffer.allocate(messageLength * 10)
			val messageStream = messageData.array().inputStream()
			val dataPosition = content.position()
			content.read(messageData)
			val message = buildString {
				repeat(messageLength) {
					append(memoryBank[messageStream.readExtensibleULong().toInt()])
					append(' ')
				}
			}
			messages.add(message)
			content.position(dataPosition + (messageData.capacity() - messageStream.available()))
			message
		} else messages[messageLength]
		val record = LogRecord(level, message)
		record.instant = time
		record.loggerName = loggerName
		record.longThreadID = threadID
		record.sourceMethodName = "readback"
		return record
	}
}