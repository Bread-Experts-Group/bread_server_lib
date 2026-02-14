package org.bread_experts_group.generic.protocol.http

import org.bread_experts_group.generic.protocol.http.h11.HTTP11RequestParsingFeatureIdentifier
import org.bread_experts_group.generic.protocol.http.h11.HTTP11ResponseParsingFeatureIdentifier
import kotlin.time.Duration

/**
 * The amount of time the parser can wait during an individual read operation before the request is considered
 * [org.bread_experts_group.generic.protocol.http.h11.HTTP11ParsingStatus.TimedOut].
 * @param timeout The amount of time to wait, at maximum.
 * @author Miko Elbrecht
 * @since D1F3N6P0
 */
data class ParsingBufferReadTimeout(
	val timeout: Duration
) : org.bread_experts_group.generic.protocol.http.h11.HTTP11ResponseParsingFeatureIdentifier,
	org.bread_experts_group.generic.protocol.http.h11.HTTP11RequestParsingFeatureIdentifier