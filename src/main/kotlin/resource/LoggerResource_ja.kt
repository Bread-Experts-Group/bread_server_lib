package org.bread_experts_group.resource

import java.util.*

/**
 * @author <a href="https://github.com/ATPStorages">Miko Elbrecht (EN)</a>
 * Translator
 * @since 2.41.0
 */
@Suppress("unused", "ClassName")
class LoggerResource_ja : ListResourceBundle() {
	override fun getContents(): Array<out Array<out Any>> = arrayOf(
		arrayOf("tests.huffman", "ハフマン分岐テスト"),
		arrayOf("tests.gamemaker", "GameMaker・WIN・InputStream・テスト"),
		arrayOf("tests.banked_file_handler", "区分ファイル処理テスト"),
		arrayOf("tests.banked_file_reader", "区分ファイル読み取りテスト"),
		arrayOf("tests.colored_handler", "色付きロガーテスト"),
		arrayOf("tests.rmi", "外部関数呼び出しテスト"),
		arrayOf("tests.isobmff", "ISOBMFF・InputStream・テスト"),
		arrayOf("tests.http_protocol_selection", "HTTPプロトコル選択テスト"),
		arrayOf("tests.private_socket_util", "パン専門家グループ・ソケットユーティリティテスト（内部用）"),
		arrayOf("fuse", "ユーザー空間内のファイルシステム"),
		arrayOf("fuse_callbacks", "ユーザー空間内のファイルシステム用コールバック"),
		arrayOf("ffi", "外部関数インターフェイス"),
		arrayOf("html_directory_listing", "HTMLディレクトリリスティング"),
		arrayOf("program_argument_retrieval", "プログラム引数を取得"),
		arrayOf("processor", "プロセッサー"),
		arrayOf("key_pair_files", "キーペアファイル"),
		arrayOf("ia_32_assembler", "IA-32アセンブラ"),
		arrayOf("rmi", "外部関数呼び出し"),
		arrayOf("http2header", "HTTP/2ヘッダーフレーム"),
		arrayOf("http_selector", "HTTPプロトコル選択"),
		arrayOf("parser", "パーサー")
	)
}