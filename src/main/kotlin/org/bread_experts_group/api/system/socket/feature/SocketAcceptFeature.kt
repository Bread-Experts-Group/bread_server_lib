package org.bread_experts_group.api.system.socket.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.socket.DeferredSocketOperation

abstract class SocketAcceptFeature<F : D, D>(
	override val expresses: FeatureExpression<SocketAcceptFeature<F, D>>
) : SocketFeatureImplementation<SocketAcceptFeature<F, D>>() {
	abstract fun accept(vararg features: F): DeferredSocketOperation<D>
}