package org.bread_experts_group.api.coding.windows

import org.bread_experts_group.api.coding.CodingFormat
import org.bread_experts_group.api.coding.CodingFormatImplementation
import org.bread_experts_group.api.coding.CodingFormats
import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource

class WindowsUTF8CodingFormatImplementation : CodingFormatImplementation() {
	override val expresses: FeatureExpression<CodingFormatImplementation> = CodingFormats.UTF_8
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override val coding: CodingFormat = WindowsCodingFormat(65001u)
}