package org.bread_experts_group.protocol.http.h11

/**
 * Provides status when a parsing failure occurs for an HTTP/1.1 request.
 * @author Miko Elbrecht
 * @since D1F3N6P0
 */
sealed class HTTP11RequestParsingStatus : HTTP11RequestParsingDataIdentifier {
	/**
	 * The structure of the message was incorrect in some way.
	 * @author Miko Elbrecht
	 * @since D1F3N6P0
	 */
	sealed class BadForm : HTTP11RequestParsingStatus() {
		/**
		 * The method was too large.
		 * @param max The maximum method length the parser was willing to read.
		 * @author Miko Elbrecht
		 * @since D1F3N6P0
		 */
		data class MethodTooLarge(val max: Int) : BadForm()

		/**
		 * The target was too large.
		 * @param max The maximum target length the parser was willing to read.
		 * @author Miko Elbrecht.
		 * @since D1F3N6P0
		 */
		data class TargetTooLarge(val max: Int) : BadForm()
	}

	/**
	 * There was extra data after the conclusion of the HTTP version.
	 * @author Miko Elbrecht
	 * @since D1F3N6P0
	 */
	object TrashVersion : HTTP11RequestParsingStatus()

	/**
	 * The message contained a structure which ended too early.
	 * @author Miko Elbrecht
	 * @since D1F3N6P0
	 */
	sealed class EndedPremature : HTTP11RequestParsingStatus() {
		/**
		 * The request line ended at the method.
		 * @author Miko Elbrecht
		 * @since D1F3N6P0
		 */
		object Method : EndedPremature()

		/**
		 * The request line ended at the target.
		 * @author Miko Elbrecht
		 * @since D1F3N6P0
		 */
		object Target : EndedPremature()
	}
}