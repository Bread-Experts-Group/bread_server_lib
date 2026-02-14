package org.bread_experts_group.generic.protocol.http.h11

/**
 * Provides status when a parsing failure occurs for an HTTP/1.1 response.
 * @author Miko Elbrecht
 * @since D1F3N6P0
 */
sealed class HTTP11ResponseParsingStatus :
	org.bread_experts_group.generic.protocol.http.h11.HTTP11ResponseParsingDataIdentifier {
	/**
	 * The structure of the message was incorrect in some way.
	 * @author Miko Elbrecht
	 * @since D1F3N6P0
	 */
	sealed class BadForm : HTTP11ResponseParsingStatus() {
		/**
		 * The status line's code is not 3 digits.
		 * @param was The incorrect value read.
		 * @author Miko Elbrecht
		 * @since D1F3N6P0
		 */
		data class StatusCode(val was: CharSequence) : BadForm()

		/**
		 * The status line's reason phrase was structurally incorrect.
		 * @author Miko Elbrecht
		 * @since D1F3N6P0
		 */
		sealed class ReasonPhrase : BadForm() {
			/**
			 * The reason phrase came immediately after the code without a space.
			 * @author Miko Elbrecht
			 * @since D1F3N6P0
			 */
			object NoSP : ReasonPhrase()

			/**
			 * The reason phrase was too large.
			 * @param max The maximum phrase length the parser was willing to read.
			 * @author Miko Elbrecht
			 * @since D1F3N6P0
			 */
			data class TooLarge(val max: Int) : ReasonPhrase()
		}
	}

	/**
	 * The message contained a structure which ended too early.
	 * @author Miko Elbrecht
	 * @since D1F3N6P0
	 */
	sealed class EndedPremature : HTTP11ResponseParsingStatus() {
		/**
		 * The status line ended before the version and status code elements could be read.
		 * @author Miko Elbrecht
		 * @since D1F3N6P0
		 */
		object StatusLine : EndedPremature()

		/**
		 * The status code ended before all 3 digits could be read.
		 * @author Miko Elbrecht
		 * @since D1F3N6P0
		 */
		object StatusCode : EndedPremature()
	}
}