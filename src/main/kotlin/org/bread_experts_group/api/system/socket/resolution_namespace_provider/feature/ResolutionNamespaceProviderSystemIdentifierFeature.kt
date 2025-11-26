package org.bread_experts_group.api.system.socket.resolution_namespace_provider.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource

class ResolutionNamespaceProviderSystemIdentifierFeature<T>(
	override val source: ImplementationSource,
	override val expresses: FeatureExpression<ResolutionNamespaceProviderSystemIdentifierFeature<T>>,
	val identifier: T
) : ResolutionNamespaceProviderFeatureImplementation<ResolutionNamespaceProviderSystemIdentifierFeature<T>>()