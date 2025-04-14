package bread_experts_group.http

import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.collections.forEach
import kotlin.collections.sortByDescending

val siKeys = listOf("k", "M", "G", "T", "P", "E")
val siIntervals = listOf(0.0, 1000.0, 1e+6, 1e+9, 1e+12, 1e+15, 1e+18).map { it.toLong() }
fun truncateSI(n: Long, decimals: Int = 2): String {
	val intIdx = siIntervals.indexOf(siIntervals.firstOrNull { it > n } ?: siIntervals.last())
	val interval = siIntervals[intIdx - 1]
	return String.format(
		"%.${decimals}f ${siKeys[intIdx - 2]}",
		if (interval > 0) (n.toDouble() / interval) else n
	)
}

fun truncateSizeHTML(size: Long): String =
	if (size < 1000) "$size B"
	else "<span class=\"tooltip\" title=\"$size B\">${truncateSI(size)}B</span>"

fun getHTML(store: File, file: File, css: String): String = buildString {
	append("<!doctype html><html><head><style>")
	append("*{font-family:\"Lucida Console\",monospace;text-align:left;$css}")
	append(".tooltip{text-decoration:underline dotted}</style></head><body><table style=\"width:100%\">")
	append("<thead><th>Name</th><th>Size</th><th>Last Modified</th></thead><tbody>")
	val files = file.listFiles()
	if (files != null) {
		if (files.isNotEmpty()) {
			files.sortByDescending { it.lastModified() }
			files.sortByDescending { it.isDirectory }
			files.forEach {
				append("<tr><td><a href=\"${it.name}/\">${it.name}</td>")
				append("<td>${if (it.isDirectory) "Directory" else truncateSizeHTML(it.length())}</td>")
				val mod = Instant.ofEpochMilli(it.lastModified())
					.atZone(ZoneId.systemDefault())
				append("<td>${DateTimeFormatter.RFC_1123_DATE_TIME.format(mod)}</td></tr>")
			}
		} else append("<tr><td>Folder empty</td><td>-1</td><td>-1</td></tr>")
	} else append("<tr><td>Folder not accessible</td><td>-1</td><td>-1</td></tr>")
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
	val sizeStat = buildString {
		append(" [ ")
		append(truncateSizeHTML(file.usableSpace) + " / ")
		append(truncateSizeHTML(file.freeSpace) + " / ")
		append(truncateSizeHTML(file.totalSpace) + " ]")
	}
	append("$completeCaption$sizeStat</caption></tbody></table></body></html>")
}