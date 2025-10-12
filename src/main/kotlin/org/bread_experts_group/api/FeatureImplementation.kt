package org.bread_experts_group.api

interface FeatureImplementation<I : FeatureImplementation<I>> : Implementation {
	val expresses: FeatureExpression<I>
}