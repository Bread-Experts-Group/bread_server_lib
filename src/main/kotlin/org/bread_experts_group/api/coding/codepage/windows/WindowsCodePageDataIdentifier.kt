package org.bread_experts_group.api.coding.codepage.windows

import org.bread_experts_group.api.coding.codepage.CodePageDataIdentifier

interface WindowsCodePageDataIdentifier : CodePageDataIdentifier {
	val raw: UInt
}