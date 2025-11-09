package org.bread_experts_group.project_incubator.console

open class ContextTab(
	val name: String,
	val selectChar: Char?,
	val populate: (MutableList<ContextTab>) -> Unit = {}
) {
	constructor(name: String, selectChar: Char?, vararg tabs: ContextTab) : this(
		name, selectChar,
		{ it.addAll(tabs) }
	)

	var renderCallback: () -> Unit = {}
	val tabs: MutableList<ContextTab> = mutableListOf()

	var x: Int = 0
	var y: Int = 0
	var w: Int = 0
	var h: Int = 0

	open fun opened() {}
	open fun closed() {}
}