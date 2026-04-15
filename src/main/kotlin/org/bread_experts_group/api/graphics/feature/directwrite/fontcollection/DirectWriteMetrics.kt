package org.bread_experts_group.api.graphics.feature.directwrite.fontcollection

data class DirectWriteMetrics(
	val designUnitsPerEm: UShort,
	val ascent: UShort,
	val descent: UShort,
	val lineGap: Short,
	val capHeight: UShort,
	val xHeight: UShort,
	val underlinePosition: Short,
	val underlineThickness: UShort,
	val strikethroughPosition: Short,
	val strikethroughThickness: UShort
)