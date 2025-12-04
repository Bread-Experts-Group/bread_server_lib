package org.bread_experts_group.api.system.socket.feature

import org.bread_experts_group.api.feature.FeatureExpression

abstract class SocketBindFeature<F : D, D>(
	override val expresses: FeatureExpression<SocketBindFeature<F, D>>
) : SocketFeatureImplementation<SocketBindFeature<F, D>>() {
	abstract fun bind(vararg features: F): List<D>
}