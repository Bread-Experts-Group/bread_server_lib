package org.bread_experts_group.api

interface FeatureExpression<I : FeatureImplementation<I>> {
	val name: String
}