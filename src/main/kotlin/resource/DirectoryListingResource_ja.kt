package org.bread_experts_group.resource

import java.util.*

/**
 * @author <a href="https://github.com/ATPStorages">Miko Elbrecht (EN)</a>
 * Translator
 * @since 2.19.0
 */
@Suppress("unused", "ClassName")
class DirectoryListingResource_ja : ListResourceBundle() {
	override fun getContents(): Array<out Array<out Any>> = arrayOf(
		arrayOf("files", "ファイル"),
		arrayOf("folders", "フォルダ"),
		arrayOf("loops", "ループ"),
		arrayOf("name", "名前"),
		arrayOf("size", "サイズ"),
		arrayOf("last_modification", "最終更新"),
		arrayOf("empty", "空"),
		arrayOf("unreadable", "読み取り不可"),
		arrayOf("tree_errors", "木構造のエラー"),
		arrayOf("folder_empty", "空のフォルダ"),
		arrayOf("folder_inaccessible", "アクセスできないフォルダ"),
		arrayOf("outside_of_store", "仮想ルートディレクトリ外"),
		arrayOf("this_is_symlink", "シンボリックリンクです"),
	)
}