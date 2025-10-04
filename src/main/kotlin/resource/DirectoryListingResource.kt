package org.bread_experts_group.resource

import java.util.*

/**
 * @author <a href="https://github.com/ATPStorages">Miko Elbrecht (EN)</a>
 * Translator
 * @since D0F0N0P0
 */
@Suppress("unused")
class DirectoryListingResource : ListResourceBundle() {
	override fun getContents(): Array<out Array<out Any>> = arrayOf(
		arrayOf("files", "File(s)"),
		arrayOf("folders", "Folder(s)"),
		arrayOf("loops", "Loop(s)"),
		arrayOf("name", "Name"),
		arrayOf("size", "Size"),
		arrayOf("last_modification", "Last Modification"),
		arrayOf("empty", "Empty"),
		arrayOf("unreadable", "Unreadable"),
		arrayOf("tree_errors", "Tree Error(s)"),
		arrayOf("folder_empty", "Folder empty"),
		arrayOf("folder_inaccessible", "Folder inaccessible"),
		arrayOf("outside_of_store", "Outside of store"),
		arrayOf("this_is_symlink", "This is a symbolic link"),
	)

	companion object {
		fun get(locale: Locale? = null): ResourceBundle {
			val baseName = "org.bread_experts_group.resource.DirectoryListingResource"
			return if (locale != null) getBundle(baseName, locale) else getBundle(baseName)
		}
	}
}