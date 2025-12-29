package org.bread_experts_group.protocol.http.h2

import org.bread_experts_group.Mappable

enum class HTTP2StandardErrorCodes(
	override val id: UInt,
	override val tag: String
) : Mappable<HTTP2StandardErrorCodes, UInt> {
	NO_ERROR(0x00u, "The associated condition is not a result of an error."),
	PROTOCOL_ERROR(0x01u, "The endpoint detected an unspecific protocol error."),
	FLOW_CONTROL_ERROR(0x03u, "The endpoint detected that its peer violated the flow-control protocol."),
	FRAME_SIZE_ERROR(0x06u, "The endpoint received a frame with an invalid size.")
}