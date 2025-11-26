@file:Suppress("LongLine")

package org.bread_experts_group.api.system.socket.resolution_namespace_provider

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.socket.resolution_namespace_provider.feature.ResolutionNamespaceProviderSystemIdentifierFeature

object ResolutionNamespaceProviderFeatures {
	val SYSTEM_IDENTIFIER = object : FeatureExpression<ResolutionNamespaceProviderSystemIdentifierFeature<Any>> {
		override val name: String = "System Identifier"
	}

	val SYSTEM_LABEL = object : FeatureExpression<ResolutionNamespaceProviderSystemIdentifierFeature<String>> {
		override val name: String = "System Label"
	}

	val SYSTEM_VERSION = object : FeatureExpression<ResolutionNamespaceProviderSystemIdentifierFeature<Int>> {
		override val name: String = "System Version"
	}
}