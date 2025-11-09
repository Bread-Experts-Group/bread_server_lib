package org.bread_experts_group.api.coding

import org.bread_experts_group.api.FeatureExpression

object CodingFormats {
	val UTF_8 = object : FeatureExpression<CodingFormatImplementation> {
		override val name: String = "Unicode Transformation Format, 8-bit (UTF-8)"
	}
}