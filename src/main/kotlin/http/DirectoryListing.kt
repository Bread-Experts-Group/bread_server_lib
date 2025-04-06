package bread_experts_group.http

import bread_experts_group.dateTimeFormatter
import java.io.File
import java.time.Instant
import java.time.ZoneId
import kotlin.collections.forEach
import kotlin.collections.sortByDescending

fun getHTML(store: File, file: File, css: String): String = buildString {
	append("<!doctype html><html><head><style>")
	append("*{font-family:\"Lucida Console\",monospace;text-align:left;$css}")
	append("</style></head><body><table style=\"width:100%\">")
	append("<thead><th>Name</th><th>Size</th><th>Last Modified</th></thead><tbody>")
	val files = file.listFiles()
	if (files != null) {
		files.sortByDescending { it.lastModified() }
		files.sortByDescending { it.isDirectory }
		files.forEach {
			append("<tr><td><a href=\"${it.name}/\">${it.name}</td>")
			append("<td>${if (it.isDirectory) "Directory" else it.length()}</td>")
			val mod = Instant.ofEpochMilli(it.lastModified())
				.atZone(ZoneId.systemDefault())
			append("<td>${dateTimeFormatter.format(mod)}</td></tr>")
		}
	} else {
		append("<tr><td>Folder not accessible</td><td>-1</td><td>-1</td></tr>")
	}
	append("<caption>")
	val itParent = store.parentFile
	append(itParent.invariantSeparatorsPath + '/')
	var accessible = file.invariantSeparatorsPath
		.substring(itParent.invariantSeparatorsPath.length + 1)
	var completeCaption = ""
	var backReferences = ""
	accessible.split('/').reversed().forEachIndexed { index, it ->
		completeCaption =
			if (index == 0) it
			else "<a href=\"$backReferences\">$it</a>/$completeCaption"
		backReferences += "../"
	}
	append("$completeCaption</caption></tbody></table></body></html>")
}