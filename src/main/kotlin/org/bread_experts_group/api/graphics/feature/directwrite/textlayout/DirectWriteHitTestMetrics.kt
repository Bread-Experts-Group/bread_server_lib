package org.bread_experts_group.api.graphics.feature.directwrite.textlayout

data class DirectWriteHitTestMetrics(
	val textPosition: Int,
	val length: Int,
	val left: Float,
	val top: Float,
	val width: Float,
	val height: Float,
	val bidiLevel: Int,
	val isText: Boolean,
	val isTrimmed: Boolean
)