package org.bread_experts_group.api.system.socket.feature

import org.bread_experts_group.api.feature.FeatureExpression

abstract class SocketListenFeature<F : D, D>(
	override val expresses: FeatureExpression<SocketListenFeature<F, D>>
) : SocketFeatureImplementation<SocketListenFeature<F, D>>() {
	abstract fun listen(vararg features: F): List<D>
}