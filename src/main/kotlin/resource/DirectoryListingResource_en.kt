package org.bread_experts_group.resource

import java.util.*

class DirectoryListingResource_en : ListResourceBundle() {
	override fun getContents(): Array<out Array<out Any>> = arrayOf(
		arrayOf("files", "Files"),
		arrayOf("name", "Name"),
		arrayOf("size", "Size"),
		arrayOf("last_modification", "Last Modification"),
		arrayOf("empty", "Empty"),
		arrayOf("unreadable", "Unreadable"),
		arrayOf("tree_errors", "Tree Errors"),
		arrayOf("folder_empty", "Folder empty"),
		arrayOf("folder_inaccessible", "Folder inaccessible"),
		arrayOf("outside_of_store", "Outside of store")
	)
}