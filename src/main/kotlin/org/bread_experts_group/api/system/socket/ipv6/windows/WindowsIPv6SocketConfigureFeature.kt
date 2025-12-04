package org.bread_experts_group.api.system.socket.ipv6.windows

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.socket.feature.SocketConfigureFeature
import org.bread_experts_group.api.system.socket.ipv6.config.IPv6ConfigureDataIdentifier
import org.bread_experts_group.api.system.socket.ipv6.config.IPv6ConfigureFeatureIdentifier
import org.bread_experts_group.api.system.socket.ipv6.config.WindowsIPv6SocketConfigurationFeatures
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.DWORD
import org.bread_experts_group.ffi.windows.threadLocalDWORD0
import org.bread_experts_group.ffi.windows.throwLastWSAError
import org.bread_experts_group.ffi.windows.wsa.nativeSetSockOpt

class WindowsIPv6SocketConfigureFeature(
	private val socket: Long,
	expresses: FeatureExpression<SocketConfigureFeature<IPv6ConfigureFeatureIdentifier, IPv6ConfigureDataIdentifier>>
) : SocketConfigureFeature<IPv6ConfigureFeatureIdentifier, IPv6ConfigureDataIdentifier>(expresses) {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun configure(vararg features: IPv6ConfigureFeatureIdentifier): List<IPv6ConfigureDataIdentifier> {
		val supportedFeatures = mutableListOf<IPv6ConfigureDataIdentifier>()
		for (feature in features) when (feature) {
			WindowsIPv6SocketConfigurationFeatures.RESTRICT_IPV6_ONLY,
			WindowsIPv6SocketConfigurationFeatures.ALLOW_IPV6_AND_IPV4 -> {
				threadLocalDWORD0.set(
					DWORD, 0,
					if (feature == WindowsIPv6SocketConfigurationFeatures.RESTRICT_IPV6_ONLY) 1
					else 0
				)
				val status = nativeSetSockOpt!!.invokeExact(
					capturedStateSegment,
					socket,
					IPPROTO_IPV6,
					IPV6_V6ONLY,
					threadLocalDWORD0,
					threadLocalDWORD0.byteSize().toInt()
				) as Int
				if (status != 0) throwLastWSAError()
				supportedFeatures.add(feature)
			}

			else -> {}
		}
		return supportedFeatures
	}
}