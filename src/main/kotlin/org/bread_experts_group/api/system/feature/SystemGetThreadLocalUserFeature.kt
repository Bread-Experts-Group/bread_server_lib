package org.bread_experts_group.api.system.feature

import org.bread_experts_group.api.FeatureExpression
import org.bread_experts_group.api.system.SystemFeatures
import org.bread_experts_group.api.system.user.SystemUser

abstract class SystemGetThreadLocalUserFeature : SystemFeatureImplementation<SystemGetThreadLocalUserFeature>() {
	override val expresses: FeatureExpression<SystemGetThreadLocalUserFeature> = SystemFeatures.GET_THREAD_LOCAL_USER
	abstract val user: SystemUser
}