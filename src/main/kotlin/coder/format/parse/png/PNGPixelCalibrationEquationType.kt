package org.bread_experts_group.coder.format.parse.png

import org.bread_experts_group.coder.Mappable

enum class PNGPixelCalibrationEquationType(
	override val id: Int,
	override val tag: String
) : Mappable<PNGPixelCalibrationEquationType, Int> {
	LINEAR_MAPPING(0, $$"Linear Mapping [%3$s + %4$s * original_sample / (%2$s-%1$s)] = pV"),
	BASE_E_EXPONENTIAL_MAPPING(
		1,
		$$"Base-e Exponential Mapping [%3$s + %4$s * exp(%5$s * original_sample / (%2$s-%1$s))] = pV"
	),
	ARBITRARY_BASE_EXPONENTIAL_MAPPING(
		2,
		$$"Arbitrary-base Exponential Mapping [%3$s + %4$s * pow(%5$s, (original_sample / (%2$s-%1$s)))] = pV"
	),
	HYPERBOLIC_MAPPING(
		3,
		$$"Hyperbolic Mapping [%3$s + %4$s * sinh(%5$s * (original_sample - %6$s) / (%2$s-%1$s))] = pV"
	);

	override fun toString(): String = stringForm()
}