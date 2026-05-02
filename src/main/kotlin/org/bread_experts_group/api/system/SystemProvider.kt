package org.bread_experts_group.api.system

import org.bread_experts_group.api.apiRootLogger
import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.FeatureProvider
import org.bread_experts_group.api.system.device.linux.x64.LinuxX64SystemGetCurrentWorkingDeviceFeature
import org.bread_experts_group.api.system.device.windows.*
import org.bread_experts_group.api.system.feature.SystemFeatureImplementation
import org.bread_experts_group.api.system.feature.linux.x64.LinuxX64SystemNetworkingSocketsFeature
import org.bread_experts_group.api.system.feature.macos.MacOSSystemThreadLocalUserFeature
import org.bread_experts_group.api.system.feature.windows.WindowsSystemGetThreadLocalUserFeature
import org.bread_experts_group.api.system.feature.windows.WindowsSystemGetUptimeFeature
import org.bread_experts_group.api.system.feature.windows.WindowsSystemNetworkingSocketsFeature
import org.bread_experts_group.generic.logging.LevelLogger

object SystemProvider : FeatureProvider<SystemFeatureImplementation<*>> {
	override val logger = LevelLogger("system", apiRootLogger)
	override val features: MutableList<SystemFeatureImplementation<*>> = mutableListOf(
		WindowsSystemGetUptimeFeature(),
		WindowsSystemGetThreadLocalUserFeature(),
		WindowsSystemNetworkingSocketsFeature(),
		WindowsSystemDeviceAddedEventFeature(),
		WindowsSystemDeviceRemovedEventFeature(),
		WindowsSystemDeviceEnumerationFeature(),
		WindowsSystemProjectedFileHierarchyFeature(),
		WindowsSystemGetCurrentWorkingDeviceFeature(),
		WindowsSystemSetCurrentWorkingDeviceFeature(),
		WindowsSystemGetTemporaryStorageDeviceFeature(),
		WindowsSystemGetPathDeviceDirectFeature(),
		MacOSSystemThreadLocalUserFeature(),
		LinuxX64SystemGetCurrentWorkingDeviceFeature(),
		LinuxX64SystemNetworkingSocketsFeature()
	)
	override val supportedFeatures: MutableMap<
			FeatureExpression<out SystemFeatureImplementation<*>>,
			MutableList<SystemFeatureImplementation<*>>> = mutableMapOf()
}