package org.bread_experts_group.api.system.socket.feature

import org.bread_experts_group.api.feature.FeatureExpression

abstract class SocketConfigureFeature<F : D, D>(
	override val expresses: FeatureExpression<SocketConfigureFeature<F, D>>
) : SocketFeatureImplementation<SocketConfigureFeature<F, D>>() {
	abstract fun configure(vararg features: F): List<D>
}