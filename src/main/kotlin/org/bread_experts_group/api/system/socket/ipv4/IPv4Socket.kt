package org.bread_experts_group.api.system.socket.ipv4

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.FeatureProvider
import org.bread_experts_group.api.system.socket.feature.SocketFeatureImplementation
import org.bread_experts_group.api.system.socket.feature.close.SocketCloseFeatureIdentifier
import java.util.logging.Logger

abstract class IPv4Socket<T> : FeatureProvider<SocketFeatureImplementation<*>> {
	override val logger: Logger
		get() = TODO("Not yet implemented")
	override val supportedFeatures: MutableMap<
			FeatureExpression<out SocketFeatureImplementation<*>>,
			MutableList<SocketFeatureImplementation<*>>> = mutableMapOf()

	abstract fun close(
		vararg features: SocketCloseFeatureIdentifier
	): List<SocketCloseFeatureIdentifier>
}