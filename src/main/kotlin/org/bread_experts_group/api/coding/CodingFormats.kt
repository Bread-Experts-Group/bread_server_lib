package org.bread_experts_group.api.coding

import org.bread_experts_group.api.coding.feature.PortableNetworkGraphicsCodingFeature
import org.bread_experts_group.api.coding.feature.UTF8CodePageCodingFeature
import org.bread_experts_group.api.feature.FeatureExpression

object CodingFormats {
	val PORTABLE_NETWORK_GRAPHICS = object : FeatureExpression<PortableNetworkGraphicsCodingFeature> {
		override val name: String = "Portable Network Graphics"
	}

	val CODE_PAGE_UTF8 = object : FeatureExpression<UTF8CodePageCodingFeature> {
		override val name: String = "UTF-8 Code Page"
	}
}