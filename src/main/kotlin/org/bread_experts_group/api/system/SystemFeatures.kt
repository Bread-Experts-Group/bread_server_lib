package org.bread_experts_group.api.system

import org.bread_experts_group.api.FeatureExpression
import org.bread_experts_group.api.system.device.SystemDeviceAddedEventFeature
import org.bread_experts_group.api.system.device.SystemDeviceEnumerationFeature
import org.bread_experts_group.api.system.device.SystemDeviceRemovedEventFeature
import org.bread_experts_group.api.system.feature.SystemThreadLocalUserFeature
import org.bread_experts_group.api.system.feature.SystemUptimeFeature

object SystemFeatures {
	val UPTIME_MS = object : FeatureExpression<SystemUptimeFeature> {
		override val name: String = "System Uptime (ms)"
	}

	val THREAD_LOCAL_USER = object : FeatureExpression<SystemThreadLocalUserFeature> {
		override val name: String = "Local User"
	}

	val DEVICE_ADDED_EVENT = object : FeatureExpression<SystemDeviceAddedEventFeature> {
		override val name: String = "System Device Added Event"
	}

	val DEVICE_REMOVED_EVENT = object : FeatureExpression<SystemDeviceRemovedEventFeature> {
		override val name: String = "System Device Removed Event"
	}

	val ENUMERATE_DEVICES = object : FeatureExpression<SystemDeviceEnumerationFeature> {
		override val name: String = "System Device Enumeration"
	}
}