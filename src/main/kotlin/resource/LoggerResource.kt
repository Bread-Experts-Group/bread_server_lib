package org.bread_experts_group.resource

import java.util.*

/**
 * @author <a href="https://github.com/ATPStorages">Miko Elbrecht (EN)</a>
 * Translator
 * @since 2.41.0
 */
@Suppress("unused")
class LoggerResource : ListResourceBundle() {
	override fun getContents(): Array<out Array<out Any>> = arrayOf(
		arrayOf("tests.huffman", "Huffman Branch Tests"),
		arrayOf("tests.gamemaker", "GameMaker WIN InputStream Tests"),
		arrayOf("tests.banked_file_handler", "Banked File Handler Tests"),
		arrayOf("tests.banked_file_reader", "Banked File Reader Tests"),
		arrayOf("tests.colored_handler", "Colored Logger Tests"),
		arrayOf("tests.rmi", "Remote Method Invocation Tests"),
		arrayOf("tests.elf", "ELF InputStream Tests"),
		arrayOf("tests.isobmff", "ISOBMFF InputStream Tests"),
		arrayOf("tests.http_protocol_selection", "HTTP Protocol Selection Tests"),
		arrayOf("tests.private_socket_util", "Bread Experts Group Private Socket Utilities Tests"),
		arrayOf("tests.ia_32_assembler", "IA-32 Assembler Tests"),
		arrayOf("fuse", "Filesystem in Userspace"),
		arrayOf("fuse_callbacks", "Filesystem in Userspace Callbacks"),
		arrayOf("ffi", "Foreign Function Interface"),
		arrayOf("html_directory_listing", "HTML Directory Listing"),
		arrayOf("program_argument_retrieval", "Program Argument Retrieval"),
		arrayOf("ia32_processor", "IA-32 Processor"),
		arrayOf("arm_v4_processor", "ARMv4 Processor"),
		arrayOf("key_pair_files", "Key Pair Files"),
		arrayOf("ia_32_assembler", "IA-32 Assembler"),
		arrayOf("rmi", "Remote Method Invocation"),
		arrayOf("http2header", "HTTP2 Header Frame"),
		arrayOf("http_selector", "HTTP Protocol Selector"),
		arrayOf("parser", "Parser")
	)

	companion object {
		fun get(locale: Locale? = null): ResourceBundle {
			val baseName = "org.bread_experts_group.resource.LoggerResource"
			return if (locale != null) getBundle(baseName, locale) else getBundle(baseName)
		}
	}
}