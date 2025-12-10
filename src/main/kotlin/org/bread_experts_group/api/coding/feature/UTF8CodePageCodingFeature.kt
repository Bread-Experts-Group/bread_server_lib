package org.bread_experts_group.api.coding.feature

import org.bread_experts_group.api.coding.CodingFeatureImplementation
import org.bread_experts_group.api.coding.codepage.CodePageDataIdentifier

abstract class UTF8CodePageCodingFeature : CodingFeatureImplementation<UTF8CodePageCodingFeature>() {
	abstract val codePage: CodePageDataIdentifier
}