package org.bread_experts_group.api.system.socket.resolution

interface ResolutionProvidingFeature<T> {
	fun resolve(
		hostName: String,
		vararg features: ResolutionFeatureIdentifier
	): List<ResolutionDataIdentifier>
}