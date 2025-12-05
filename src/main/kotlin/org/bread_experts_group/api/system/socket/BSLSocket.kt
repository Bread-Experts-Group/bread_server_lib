package org.bread_experts_group.api.system.socket

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.FeatureProvider
import org.bread_experts_group.api.system.socket.close.SocketCloseDataIdentifier
import org.bread_experts_group.api.system.socket.close.SocketCloseFeatureIdentifier
import org.bread_experts_group.api.system.socket.feature.SocketFeatureImplementation
import org.bread_experts_group.api.system.socket.resolution.ResolutionDataPartIdentifier
import java.util.logging.Logger

abstract class BSLSocket<T : ResolutionDataPartIdentifier> : FeatureProvider<SocketFeatureImplementation<*>> {
	override val logger: Logger
		get() = TODO("Not yet implemented")
	override val supportedFeatures: MutableMap<
			FeatureExpression<out SocketFeatureImplementation<*>>,
			MutableList<SocketFeatureImplementation<*>>> = mutableMapOf()

	abstract fun close(
		vararg features: SocketCloseFeatureIdentifier
	): List<SocketCloseDataIdentifier>
}