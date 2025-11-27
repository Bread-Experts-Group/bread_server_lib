package org.bread_experts_group.api.system.socket

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.socket.sys_feature.SystemSocketProvideResolutionNamespaceProvidersFeature
import org.bread_experts_group.api.system.socket.sys_feature.SystemSocketProviderInternetProtocolV4Feature
import org.bread_experts_group.api.system.socket.sys_feature.SystemSocketProviderInternetProtocolV6Feature
import org.bread_experts_group.api.system.socket.sys_feature.SystemSocketProviderTextualFeature

object SystemSocketProviderFeatures {
	val INTERNET_PROTOCOL_V4 = object : FeatureExpression<SystemSocketProviderInternetProtocolV4Feature> {
		override val name: String = "Internet Protocol, version 4"
	}

	val INTERNET_PROTOCOL_V6 = object : FeatureExpression<SystemSocketProviderInternetProtocolV6Feature> {
		override val name: String = "Internet Protocol, version 6"
	}

	val PROVIDER_DESCRIPTION = object : FeatureExpression<SystemSocketProviderTextualFeature> {
		override val name: String = "Socket Provider Description"
	}

	val PROVIDER_SYSTEM_STATUS = object : FeatureExpression<SystemSocketProviderTextualFeature> {
		override val name: String = "Socket Provider System Status"
	}

	val RESOLUTION_NAMESPACE_PROVIDERS =
		object : FeatureExpression<SystemSocketProvideResolutionNamespaceProvidersFeature> {
			override val name: String = "Resolution Namespace Providers"
		}
}