package org.bread_experts_group.protocol.http.h2

import org.bread_experts_group.Mappable

/**
 * Standard HTTP/2 frame types, as defined in [IETF RFC 9113](https://datatracker.ietf.org/doc/html/rfc9113).
 * @author Miko Elbrecht
 * @since D1F3N6P0
 */
enum class HTTP2StandardFrameTypes(override val id: Int) : Mappable<HTTP2StandardFrameTypes, Int> {
	DATA(0x00),
	HEADERS(0x01),
	PRIORITY(0x02),
	RST_STREAM(0x03),
	SETTINGS(0x04),
	PUSH_PROMISE(0x05),
	PING(0x06),
	GOAWAY(0x07),
	WINDOW_UPDATE(0x08),
	CONTINUATION(0x09);

	override val tag: String = name
	override fun toString(): String = stringForm()
}