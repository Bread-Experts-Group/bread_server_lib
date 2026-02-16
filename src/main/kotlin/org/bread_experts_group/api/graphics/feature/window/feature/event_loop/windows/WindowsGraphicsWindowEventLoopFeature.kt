package org.bread_experts_group.api.graphics.feature.window.feature.event_loop.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.graphics.feature.window.feature.GraphicsWindowEventLoopFeature
import org.bread_experts_group.api.graphics.feature.window.feature.event_loop.*

class WindowsGraphicsWindowEventLoopFeature(
	val events: MutableList<GraphicsWindowEventSubscriber<*, *>>
) : GraphicsWindowEventLoopFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun add(vararg features: GraphicsWindowEventLoopSubscriptionFeature): List<GraphicsWindowEventLoopSubscriptionData> {
		val subscribers = features.filterIsInstance<GraphicsWindowEventSubscriber<GraphicsWindowEventLoopEventResult,
				GraphicsWindowEventLoopEventParameter>>()
		events.addAll(subscribers)
		return subscribers.map {
			object : GraphicsWindowEventSubscription
			<GraphicsWindowEventLoopEventResult, GraphicsWindowEventLoopEventParameter>(
				it
			) {
				override fun close() {
					events.remove(it)
				}
			}
		}
	}
}