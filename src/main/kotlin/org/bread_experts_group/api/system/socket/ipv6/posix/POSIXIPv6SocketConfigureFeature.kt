package org.bread_experts_group.api.system.socket.ipv6.posix

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.feature.linux.LinuxSystemNetworkingSocketsFeature.Companion.IPPROTO_IPV6
import org.bread_experts_group.api.system.feature.linux.LinuxSystemNetworkingSocketsFeature.Companion.IPV6_V6ONLY
import org.bread_experts_group.api.system.socket.feature.SocketConfigureFeature
import org.bread_experts_group.api.system.socket.ipv6.config.IPv6ConfigureDataIdentifier
import org.bread_experts_group.api.system.socket.ipv6.config.IPv6ConfigureFeatureIdentifier
import org.bread_experts_group.api.system.socket.ipv6.config.WindowsIPv6SocketConfigurationFeatures
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.posix.nativeSetSockOpt
import org.bread_experts_group.ffi.posix.throwLastErrno
import org.bread_experts_group.ffi.threadLocalInt
import java.lang.foreign.ValueLayout

class POSIXIPv6SocketConfigureFeature(
	private val socket: Int,
	expresses: FeatureExpression<SocketConfigureFeature<IPv6ConfigureFeatureIdentifier, IPv6ConfigureDataIdentifier>>
) : SocketConfigureFeature<IPv6ConfigureFeatureIdentifier, IPv6ConfigureDataIdentifier>(expresses) {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun configure(vararg features: IPv6ConfigureFeatureIdentifier): List<IPv6ConfigureDataIdentifier> {
		val supportedFeatures = mutableListOf<IPv6ConfigureDataIdentifier>()
		for (feature in features) when (feature) {
			WindowsIPv6SocketConfigurationFeatures.RESTRICT_IPV6_ONLY,
			WindowsIPv6SocketConfigurationFeatures.ALLOW_IPV6_AND_IPV4 -> {
				threadLocalInt.set(
					ValueLayout.JAVA_INT, 0,
					if (feature == WindowsIPv6SocketConfigurationFeatures.RESTRICT_IPV6_ONLY) 1
					else 0
				)
				val status = nativeSetSockOpt!!.invokeExact(
					capturedStateSegment,
					socket,
					IPPROTO_IPV6,
					IPV6_V6ONLY,
					threadLocalInt,
					threadLocalInt.byteSize().toInt()
				) as Int
				if (status != 0) throwLastErrno()
				supportedFeatures.add(feature)
			}

			else -> {}
		}
		return supportedFeatures
	}
}