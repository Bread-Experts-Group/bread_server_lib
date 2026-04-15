package org.bread_experts_group.project_incubator.mmui

data class FileEntry(
	val directName: String,
	val shellName: String = directName,
	val shellType: String,
	var selected: Boolean = false
)