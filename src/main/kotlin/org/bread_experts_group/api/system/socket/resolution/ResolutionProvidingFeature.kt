package org.bread_experts_group.api.system.socket.resolution

interface ResolutionProvidingFeature {
	fun resolve(
		hostName: String,
		serviceName: String,
		vararg features: ResolutionFeatureIdentifier
	): List<ResolutionDataIdentifier>

	fun resolve(
		hostName: String,
		port: UShort,
		vararg features: ResolutionFeatureIdentifier
	): List<ResolutionDataIdentifier> = resolve(hostName, port.toString(), *features)
}