package org.bread_experts_group.api.graphics.feature.console.feature.device.feature

import org.bread_experts_group.api.FeatureExpression

abstract class GraphicsConsoleIOEventGetFeature :
	GraphicsConsoleIOFeatureImplementation<GraphicsConsoleIOEventGetFeature>() {
	override val expresses: FeatureExpression<GraphicsConsoleIOEventGetFeature> = GraphicsConsoleIOFeatures.EVENT_GET

	abstract fun getEvent(): GraphicsConsoleIOEvent?
	abstract fun pollEvent(): GraphicsConsoleIOEvent
	abstract fun peekEvent(): GraphicsConsoleIOEvent?
	abstract fun peekNextEvent(): GraphicsConsoleIOEvent
	abstract fun getEvents(returnIfNone: Boolean, maxLength: Int = 128): List<GraphicsConsoleIOEvent>
	abstract fun peekEvents(returnIfNone: Boolean, maxLength: Int = 128): List<GraphicsConsoleIOEvent>
	abstract fun getEventCount(): UInt
}
