package org.bread_experts_group.generic.protocol.http.h11

/**
 * Provides status when a parsing failure occurs for an HTTP/1.1 message.
 * @author Miko Elbrecht
 * @since D1F3N6P0
 */
sealed class HTTP11ParsingStatus : org.bread_experts_group.generic.protocol.http.h11.HTTP11RequestParsingDataIdentifier,
	org.bread_experts_group.generic.protocol.http.h11.HTTP11ResponseParsingDataIdentifier {
	/**
	 * The structure of the message was incorrect in some way.
	 * @author Miko Elbrecht
	 * @since D1F3N6P0
	 */
	sealed class BadForm : HTTP11ParsingStatus() {
		/**
		 * The status line's version is structurally incorrect.
		 * @author Miko Elbrecht
		 * @since D1F3N6P0
		 */
		sealed class Version : BadForm() {
			/**
			 * The string before /1.1 was not "HTTP".
			 * @param was The incorrect value read.
			 * @author Miko Elbrecht
			 * @since D1F3N6P0
			 */
			data class HTTP(val was: CharSequence) : Version()

			/**
			 * The character between HTTP and 1.1 was not '/'.
			 * @param was The incorrect value read.
			 * @author Miko Elbrecht
			 * @since D1F3N6P0
			 */
			data class Slash(val was: String) : Version()

			/**
			 * The version indicated after HTTP/ was not "1.1".
			 * @param was The incorrect value read.
			 * @author Miko Elbrecht
			 * @since D1F3N6P0
			 */
			data class HTTP11(val was: CharSequence) : Version()
		}

		/**
		 * The header fields were structurally incorrect.
		 * @author Miko Elbrecht
		 * @since D1F3N6P0
		 */
		sealed class Fields : BadForm() {
			/**
			 * There are too many header fields.
			 * @param max The maximum length the parser was willing to read.
			 * @author Miko Elbrecht
			 * @since D1F3N6P0
			 */
			data class TooMany(val max: Int) : Fields()

			/**
			 * The key for a header field was too large.
			 * @param max The maximum length the parser was willing to read.
			 * @author Miko Elbrecht
			 * @since D1F3N6P0
			 */
			data class KeyTooLarge(val max: Int) : Fields()

			/**
			 * The value for a header field was too large.
			 * @param max The maximum length the parser was willing to read.
			 * @author Miko Elbrecht
			 * @since D1F3N6P0
			 */
			data class ValueTooLarge(val max: Int) : Fields()
		}
	}

	/**
	 * The message contained a structure which ended too early.
	 * @author Miko Elbrecht
	 * @since D1F3N6P0
	 */
	sealed class EndedPremature : HTTP11ParsingStatus() {
		/**
		 * The field ended before a value could be read.
		 * @author Miko Elbrecht
		 * @since D1F3N6P0
		 */
		object FieldKey : EndedPremature()
	}

	/**
	 * The parser timed out waiting for data.
	 * @author Miko Elbrecht
	 * @since D1F3N6P0
	 */
	object TimedOut : HTTP11ParsingStatus()
}