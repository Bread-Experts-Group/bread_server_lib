package org.bread_experts_group.api.coding

abstract class CodingFormat {
	abstract val systemName: String
	abstract val descriptor: CodingFormatDescriptors

	override fun toString(): String = "$descriptor \"$systemName\""
}