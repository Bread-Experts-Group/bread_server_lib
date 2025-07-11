package org.bread_experts_group.coder.format.parse.iso_bmff.box

interface ISOBMFFFullBox {
	val version: Int
	val flags: Int

	fun fullBoxString() = "[v$version, $flags]"
}