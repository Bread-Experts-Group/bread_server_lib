package org.bread_experts_group.api.system.feature

import org.bread_experts_group.api.FeatureExpression
import org.bread_experts_group.api.system.SystemFeatures

abstract class SystemGetUptimeFeature : SystemFeatureImplementation<SystemGetUptimeFeature>() {
	override val expresses: FeatureExpression<SystemGetUptimeFeature> = SystemFeatures.GET_UPTIME_MS
	abstract val uptime: ULong
}