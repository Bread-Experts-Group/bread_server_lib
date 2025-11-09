package org.bread_experts_group.api.system.feature

import org.bread_experts_group.api.FeatureExpression
import org.bread_experts_group.api.system.SystemFeatures

abstract class SystemUptimeFeature : SystemFeatureImplementation<SystemUptimeFeature>() {
	override val expresses: FeatureExpression<SystemUptimeFeature> = SystemFeatures.UPTIME_MS
	abstract val uptime: ULong
}