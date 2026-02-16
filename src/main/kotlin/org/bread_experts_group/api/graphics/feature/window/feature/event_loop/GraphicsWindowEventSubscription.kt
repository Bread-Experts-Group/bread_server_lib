package org.bread_experts_group.api.graphics.feature.window.feature.event_loop

abstract class GraphicsWindowEventSubscription
<R : GraphicsWindowEventLoopEventResult, P : GraphicsWindowEventLoopEventParameter>(
	val ofSubscriber: GraphicsWindowEventSubscriber<R, P>
) : GraphicsWindowEventLoopSubscriptionData, AutoCloseable