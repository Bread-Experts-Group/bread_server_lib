package org.bread_experts_group.api.system.socket.feature

import org.bread_experts_group.api.feature.FeatureExpression

abstract class SocketConnectFeature<F : D, D>(
	override val expresses: FeatureExpression<SocketConnectFeature<F, D>>
) : SocketFeatureImplementation<SocketConnectFeature<F, D>>() {
	abstract fun connect(
		vararg features: F
	): List<D>
}