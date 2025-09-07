package org.bread_experts_group.api

/**
 * Feature providers allow programs to use features from APIs where *expressed features* may not be present across all
 * systems (e.g. graphics), and still allow them to take advantage where they are present.
 * Additionally, providers allow the use of *emulated features,* where the expressed feature cannot be more effectively
 * produced natively, and is implemented in the JVM.
 * * *expressed feature:* A generic feature, such as the concept of windowing.
 * * *implemented feature:* A feature that can be produced on the local system, such as through a Desktop Environment.
 * * *emulated feature:* A feature that can be produced on the local system, using platform-agnostic features through
 * the JVM.
 * @see FeatureImplementation
 * @author Miko Elbrecht
 * @since 4.2.0
 */
interface FeatureProvider<X> {
	fun <I : X, E : FeatureExpression<I>> get(feature: E, allowEmulated: Boolean = false): I?
}