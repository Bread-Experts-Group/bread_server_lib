package org.bread_experts_group.protocol.old.http.provider

import org.bread_experts_group.channel.WritingByteBuffer
import org.bread_experts_group.io.reader.ReadingByteBuffer
import org.bread_experts_group.protocol.old.http.HTTPRequest
import org.bread_experts_group.protocol.old.http.HTTPResponse
import org.bread_experts_group.protocol.old.http.HTTPVersion
import java.util.*

abstract class HTTPProvider(val version: HTTPVersion) {
	abstract fun setupClientRead(
		from: ReadingByteBuffer,
		transmissionLog: Queue<HTTPRequest>,
		backlog: Queue<Result<HTTPResponse>>
	)

	abstract fun setupServerRead(from: ReadingByteBuffer, backlog: Queue<Result<HTTPRequest>>)

	abstract fun sendRequest(request: HTTPRequest, transmissionLog: Queue<HTTPRequest>, to: WritingByteBuffer)
	abstract fun sendResponse(response: HTTPResponse, to: WritingByteBuffer)
}