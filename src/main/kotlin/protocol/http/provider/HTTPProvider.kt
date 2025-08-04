package org.bread_experts_group.protocol.http.provider

import org.bread_experts_group.channel.ReadingByteBuffer
import org.bread_experts_group.channel.WritingByteBuffer
import org.bread_experts_group.protocol.http.HTTPRequest
import org.bread_experts_group.protocol.http.HTTPResponse
import org.bread_experts_group.protocol.http.HTTPVersion
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