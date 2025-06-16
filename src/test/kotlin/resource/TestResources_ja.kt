package org.bread_experts_group.resource

import java.util.*

@Suppress("HardCodedStringLiteral", "unused", "ClassName")
class TestResources_ja : ListResourceBundle() {
	override fun getContents(): Array<out Array<out Any>> = arrayOf(
		arrayOf("TEST_LEVEL", "テストレベル")
	)
}