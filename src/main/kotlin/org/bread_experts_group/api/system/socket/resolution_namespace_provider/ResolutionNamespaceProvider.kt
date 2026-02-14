@file:Suppress("LongLine")

package org.bread_experts_group.api.system.socket.resolution_namespace_provider

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.FeatureProvider
import org.bread_experts_group.api.system.socket.resolution.ResolutionNamespaceEntity
import org.bread_experts_group.api.system.socket.resolution_namespace_provider.feature.ResolutionNamespaceProviderFeatureImplementation
import org.bread_experts_group.api.system.socket.resolution_namespace_provider.type.ResolutionNamespaceTypeIdentifier

abstract class ResolutionNamespaceProvider : FeatureProvider<ResolutionNamespaceProviderFeatureImplementation<*>>,
	ResolutionNamespaceEntity {
	override val logger
		get() = TODO("Not yet implemented")
	override val supportedFeatures: MutableMap<
			FeatureExpression<out ResolutionNamespaceProviderFeatureImplementation<*>>,
			MutableList<ResolutionNamespaceProviderFeatureImplementation<*>>> = mutableMapOf()

	abstract val type: ResolutionNamespaceTypeIdentifier
	abstract val enabled: Boolean
}