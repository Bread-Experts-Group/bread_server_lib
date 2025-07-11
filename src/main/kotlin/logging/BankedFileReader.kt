package org.bread_experts_group.logging

import org.bread_experts_group.logging.BankedFileHandler.Companion.readMemoryBank
import org.bread_experts_group.logging.ansi_colorspace.ANSI16
import org.bread_experts_group.logging.ansi_colorspace.ANSI16Color
import org.bread_experts_group.stream.readExtensibleLongV1
import org.bread_experts_group.stream.readExtensibleULongV1
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
	val memoryBank: MutableList<String> = readMemoryBank(bank)
	val timestamps: List<ULong> = buildList {
		timestamp.position(10)
		while (timestamp.position() < timestamp.size()) {
			val initialPosition = timestamp.position()
			val backed = ByteBuffer.allocate(10)
			timestamp.read(backed)
			val data = backed.array().inputStream()
			add(data.readExtensibleULongV1())
			timestamp.position(initialPosition + (backed.capacity() - data.available()))
		}
	}

	fun createLevel(name: String, resourceBundle: String?, value: Int): Level = try {
		Level.parse(name)
	} catch (_: IllegalArgumentException) {
		ColoredLevel(name, ANSI16Color(ANSI16.MAGENTA), value, resourceBundle)
	}

	var nanos: Long = 0L
	val savedLevels: MutableMap<ULong, Level> = mutableMapOf<ULong, Level>()
	val messages: MutableList<String> = mutableListOf<String>()
	fun nextRecord(): LogRecord {
		val initialPosition = content.position()
		val data = ByteBuffer.allocate(90)
		content.read(data)
		val stream = data.array().inputStream()
		val time = timestamps[stream.readExtensibleULongV1().toInt()].let {
			nanos += stream.readExtensibleLongV1()
			Instant.ofEpochSecond(
				it.toLong(),
				nanos
			)
		}
		val levelDescriptor = stream.readExtensibleULongV1()
		val levelIndex = levelDescriptor shr 1
		val level = if (levelDescriptor and 1u == 1uL) {
			val nameIndex = stream.readExtensibleULongV1()
			createLevel(
				memoryBank[nameIndex.toInt()],
				memoryBank[levelIndex.toInt()],
				stream.readExtensibleLongV1().toInt()
			).also { savedLevels[nameIndex] = it }
		} else {
			savedLevels[levelIndex] ?: createLevel(
				memoryBank[levelIndex.toInt()],
				null,
				stream.readExtensibleLongV1().toInt()
			).also { savedLevels[levelIndex] = it }
		}
		val loggerName = memoryBank[stream.readExtensibleULongV1().toInt()]
		val threadID = stream.readExtensibleLongV1()
		val messageDescriptor = stream.readExtensibleULongV1()
		val messageLength = (messageDescriptor shr 1).toInt()
		content.position(initialPosition + (data.capacity() - stream.available()))
		val message = if (messageDescriptor and 1u == 0uL) {
			val messageData = ByteBuffer.allocate(messageLength * 10)
			val messageStream = messageData.array().inputStream()
			val dataPosition = content.position()
			content.read(messageData)
			val message = buildString {
				repeat(messageLength) {
					append(memoryBank[messageStream.readExtensibleULongV1().toInt()])
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