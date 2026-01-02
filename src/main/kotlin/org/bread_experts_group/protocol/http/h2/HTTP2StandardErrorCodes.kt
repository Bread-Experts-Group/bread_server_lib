package org.bread_experts_group.protocol.http.h2

import org.bread_experts_group.Mappable

enum class HTTP2StandardErrorCodes(
	override val id: UInt,
	override val tag: String
) : Mappable<HTTP2StandardErrorCodes, UInt> {
	NO_ERROR(0x00u, "The associated condition is not a result of an error."),
	PROTOCOL_ERROR(0x01u, "The endpoint detected an unspecific protocol error."),
	FLOW_CONTROL_ERROR(0x03u, "The endpoint detected that its peer violated the flow-control protocol."),
	STREAM_CLOSED(0x05u, "The endpoint received a frame after a stream was half-closed."),
	FRAME_SIZE_ERROR(0x06u, "The endpoint received a frame with an invalid size."),
	REFUSED_STREAM(0x07u, "The endpoint refused the stream prior to performing any application processing."),
	COMPRESSION_ERROR(
		0x09u,
		"The endpoint is unable to maintain the field section compression context for the connection."
	)
}