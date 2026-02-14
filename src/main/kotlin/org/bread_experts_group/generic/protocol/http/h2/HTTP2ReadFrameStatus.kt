package org.bread_experts_group.generic.protocol.http.h2

/**
 * Statuses indicating problems when reading an HTTP/2 frame.
 * @author Miko Elbrecht
 * @since D1F3N6P0
 */
sealed class HTTP2ReadFrameStatus : HTTP2ReadFrameData {
	/**
	 * The frame read was too large for the local [HTTP2ConnectionManager].
	 * @param was The stated size of the frame.
	 * @author Miko Elbrecht
	 * @since D1F3N6P0
	 */
	data class TooLarge(val was: Int) : HTTP2ReadFrameStatus()
}