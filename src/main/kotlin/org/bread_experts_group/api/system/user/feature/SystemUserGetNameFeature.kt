package org.bread_experts_group.api.system.user.feature

import org.bread_experts_group.api.FeatureExpression
import org.bread_experts_group.api.system.user.SystemUserFeatures

abstract class SystemUserGetNameFeature : SystemUserFeatureImplementation<SystemUserGetNameFeature>() {
	override val expresses: FeatureExpression<SystemUserGetNameFeature> = SystemUserFeatures.NAME_GET
	abstract val name: String
}