package org.bread_experts_group.project_incubator.console

open class ContextTab(
	name: String,
	val selectChar: Char?,
	val populate: (MutableList<ContextTab>) -> Unit = {}
) {
	constructor(name: String, selectChar: Char?, vararg tabs: ContextTab) : this(
		name, selectChar,
		{ it.addAll(tabs) }
	)

	open var name: String = name
	var renderCallback: () -> Unit = {}
	val tabs: MutableList<ContextTab> = mutableListOf()

	var x: Int = 0
	var y: Int = 0
	var w: Int = 0
	var h: Int = 0

	open fun opened(): Boolean = false
	open fun closed() {}
}