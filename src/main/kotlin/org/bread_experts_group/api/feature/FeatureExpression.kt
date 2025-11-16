package org.bread_experts_group.api.feature

interface FeatureExpression<I : FeatureImplementation<I>> {
	val name: String
}