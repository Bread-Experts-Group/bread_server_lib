package org.bread_experts_group.api.system.socket.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.io.ReceiveFeature

abstract class SocketReceiveFeature<F : D, D>(
	override val expresses: FeatureExpression<SocketReceiveFeature<F, D>>
) : SocketFeatureImplementation<SocketReceiveFeature<F, D>>(), ReceiveFeature<F, D>