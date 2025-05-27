package org.bread_experts_group.resource

import java.util.*

class DirectoryListingResource_ja_JP : ListResourceBundle() {
	override fun getContents(): Array<out Array<out Any>> = arrayOf(
		arrayOf("files", "ファイル"),
		arrayOf("name", "名前"),
		arrayOf("size", "サイズ"),
		arrayOf("last_modification", "最終更新"),
		arrayOf("empty", "空"),
		arrayOf("unreadable", "読み取り不可"),
		arrayOf("tree_errors", "木構造のエラー"),
		arrayOf("folder_empty", "空のフォルダ"),
		arrayOf("folder_inaccessible", "アクセスできないフォルダ"),
		arrayOf("outside_of_store", "仮想ルートディレクトリ外")
	)
}