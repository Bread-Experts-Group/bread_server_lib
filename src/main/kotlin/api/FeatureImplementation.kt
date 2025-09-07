package org.bread_experts_group.api

interface FeatureImplementation<I : FeatureImplementation<I>> {
	val expresses: FeatureExpression<I>
	val source: FeatureImplementationSource
	fun supported(): Boolean
}