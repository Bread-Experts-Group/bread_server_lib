package org.bread_experts_group.api.graphics.feature.window.feature.event_loop

class GraphicsWindowEventSubscriber<R : GraphicsWindowEventLoopEventResult, P : GraphicsWindowEventLoopEventParameter>(
	val receptiveTo: GraphicsWindowEventLoopEventType<R, P>,
	val lambda: (Array<P>) -> R
) : GraphicsWindowEventLoopSubscriptionFeature