package org.bread_experts_group.api.coding.codepage.windows

import org.bread_experts_group.api.coding.CodingFormats
import org.bread_experts_group.api.coding.codepage.CodePageDataIdentifier
import org.bread_experts_group.api.coding.feature.CodePageCodingFeature
import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource

class WindowsUTF8CodePageFeature : CodePageCodingFeature() {
	override val expresses: FeatureExpression<CodePageCodingFeature> = CodingFormats.CODE_PAGE_UTF8
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override val codePage: CodePageDataIdentifier = WindowsCodePage(65001u)
}