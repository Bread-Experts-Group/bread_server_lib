package org.bread_experts_group.api.system

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.device.feature.SystemDeviceAddedEventFeature
import org.bread_experts_group.api.system.device.feature.SystemDeviceEnumerationFeature
import org.bread_experts_group.api.system.device.feature.SystemDeviceRemovedEventFeature
import org.bread_experts_group.api.system.feature.*

object SystemFeatures {
	val GET_UPTIME_MS = object : FeatureExpression<SystemGetUptimeFeature> {
		override val name: String = "Get System Uptime (ms)"
	}

	val GET_THREAD_LOCAL_USER = object : FeatureExpression<SystemGetThreadLocalUserFeature> {
		override val name: String = "Get Thread Local User"
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

	val PROJECTED_FILE_HIERARCHY = object : FeatureExpression<SystemProjectedFileHierarchyFeature> {
		override val name: String = "Projected File Hierarchy"
	}

	val NETWORKING_SOCKETS = object : FeatureExpression<SystemNetworkingSocketsFeature> {
		override val name: String = "Networking Sockets"
	}

	val GET_CURRENT_WORKING_PATH_DEVICE = object : FeatureExpression<SystemGetPathDeviceFeature> {
		override val name: String = "Get Current Working Path Device"
	}

	val GET_TEMPORARY_STORAGE_PATH_DEVICE = object : FeatureExpression<SystemGetPathDeviceFeature> {
		override val name: String = "Get Temporary Storage Path Device"
	}
}