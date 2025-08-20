package org.bread_experts_group.protocol.old.http

import org.bread_experts_group.io.reader.ReadingByteBuffer
import org.bread_experts_group.logging.ColoredHandler
import java.nio.channels.ReadableByteChannel
import java.nio.channels.WritableByteChannel
import java.util.*
import java.util.logging.Logger

open class HTTPProtocolSelector(
	val from: ReadableByteChannel?,
	val to: WritableByteChannel
) {
	val logger: Logger = ColoredHandler.newLoggerResourced("http_selector")

	open fun setupClientReading(
		reading: ReadingByteBuffer,
		responses: Queue<Result<HTTPResponse>>
	) {
		TODO("ALPHA")
	}

	open fun setupServerReading(reading: ReadingByteBuffer) {
		TODO("BETA")
	}
}