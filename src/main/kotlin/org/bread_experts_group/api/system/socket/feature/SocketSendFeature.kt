package org.bread_experts_group.api.system.socket.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.io.SendFeature

abstract class SocketSendFeature<F : D, D>(
	override val expresses: FeatureExpression<SocketSendFeature<F, D>>
) : SocketFeatureImplementation<SocketSendFeature<F, D>>(), SendFeature<F, D>