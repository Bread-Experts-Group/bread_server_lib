package org.bread_experts_group.api.coding.feature

import org.bread_experts_group.api.coding.CodingFeatureImplementation
import org.bread_experts_group.api.coding.codepage.CodePageDataIdentifier

abstract class CodePageCodingFeature : CodingFeatureImplementation<CodePageCodingFeature>() {
	abstract val codePage: CodePageDataIdentifier
}