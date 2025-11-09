package org.bread_experts_group.api.system.user.feature

import org.bread_experts_group.api.FeatureExpression
import org.bread_experts_group.api.system.user.SystemUserFeatures
import java.time.Instant

abstract class SystemUserGetLogonTimeFeature : SystemUserFeatureImplementation<SystemUserGetLogonTimeFeature>() {
	override val expresses: FeatureExpression<SystemUserGetLogonTimeFeature> = SystemUserFeatures.LOGON_TIME_GET
	abstract val logonTime: Instant
}