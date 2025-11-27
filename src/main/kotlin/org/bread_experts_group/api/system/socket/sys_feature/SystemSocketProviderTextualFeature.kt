@file:Suppress("LongLine")

package org.bread_experts_group.api.system.socket.sys_feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource

class SystemSocketProviderTextualFeature(
	override val source: ImplementationSource,
	override val expresses: FeatureExpression<SystemSocketProviderTextualFeature>,
	val text: String
) : SystemSocketProviderFeatureImplementation<SystemSocketProviderTextualFeature>()