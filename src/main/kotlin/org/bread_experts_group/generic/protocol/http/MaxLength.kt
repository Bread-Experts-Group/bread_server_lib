package org.bread_experts_group.generic.protocol.http

import org.bread_experts_group.generic.protocol.http.h11.HTTP11ParsingFeatureIdentifier
import org.bread_experts_group.generic.protocol.http.h11.HTTP11RequestParsingFeatureIdentifier
import org.bread_experts_group.generic.protocol.http.h11.HTTP11ResponseParsingFeatureIdentifier

/**
 * Defines the maximum length for an element in the request before issuing an error.
 * @param length The maximum length.
 * @author Miko Elbrecht
 * @since D1F3N6P0
 */
sealed class MaxLength(val length: Int) :
	org.bread_experts_group.generic.protocol.http.h11.HTTP11ParsingFeatureIdentifier {
	/**
	 * Defines the maximum length for the reason phrase.
	 * @param length The maximum length.
	 * @author Miko Elbrecht
	 * @since D1F3N6P0
	 */
	class ReasonPhrase(length: Int) : MaxLength(length),
		org.bread_experts_group.generic.protocol.http.h11.HTTP11ResponseParsingFeatureIdentifier

	/**
	 * Defines the maximum length for the request method.
	 * @param length The maximum length.
	 * @author Miko Elbrecht
	 * @since D1F3N6P0
	 */
	class Method(length: Int) : MaxLength(length),
		org.bread_experts_group.generic.protocol.http.h11.HTTP11RequestParsingFeatureIdentifier

	/**
	 * Defines the maximum length for the request target.
	 * @param length The maximum length.
	 * @author Miko Elbrecht
	 * @since D1F3N6P0
	 */
	class Target(length: Int) : MaxLength(length),
		org.bread_experts_group.generic.protocol.http.h11.HTTP11RequestParsingFeatureIdentifier

	/**
	 * Defines the maximum length for data pertinent to fields.
	 * @param length The maximum length.
	 * @author Miko Elbrecht
	 * @since D1F3N6P0
	 */
	sealed class Field(
		length: Int
	) : MaxLength(length), org.bread_experts_group.generic.protocol.http.h11.HTTP11ResponseParsingFeatureIdentifier,
		org.bread_experts_group.generic.protocol.http.h11.HTTP11RequestParsingFeatureIdentifier {
		/**
		 * Defines the maximum amount of fields that can be read.
		 * @param length The maximum length.
		 * @author Miko Elbrecht
		 * @since D1F3N6P0
		 */
		class Count(length: Int) : Field(length)

		/**
		 * Defines the maximum length for a field's key.
		 * @param length The maximum length.
		 * @author Miko Elbrecht
		 * @since D1F3N6P0
		 */
		class Key(length: Int) : Field(length)

		/**
		 * Defines the maximum length for a field's value.
		 * @param length The maximum length.
		 * @author Miko Elbrecht
		 * @since D1F3N6P0
		 */
		class Value(length: Int) : Field(length)
	}
}