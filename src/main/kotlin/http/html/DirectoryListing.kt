package org.bread_experts_group.http.html

import java.io.File
import java.nio.file.FileSystems
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchKey
import java.security.MessageDigest
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.logging.Logger
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

object DirectoryListing {
	private val logger = Logger.getLogger("DirectoryListing")
	private val watcher = FileSystems.getDefault().newWatchService()
	private val directoryListingCache = mutableMapOf<File, CachedList>()
	private val reverseCache = mutableMapOf<WatchKey, File>()
	private val hasher = MessageDigest.getInstance("MD5")

	data class CachedList(
		val data: String,
		val hash: String
	)

	init {
		Thread.ofVirtual().name("DirectoryListing-CacheManager").start {
			logger.fine("Cache manager started")
			while (true) {
				logger.finest("Awaiting next watcher key ...")
				val next = watcher.take()
				logger.finer { "[$next] Watch key retrieved" }
				val reverse = reverseCache[next]
				if (reverse == null) {
					logger.finest { "[$next] Not valid. Cancelling" }
					next.cancel()
					continue
				}
				logger.finest { "[$next] Polling events" }
				val events = next.pollEvents()
				logger.finer { "[$next] ${events.size} events retrieved" }
				if (events.isNullOrEmpty()) continue
				logger.finer { "[$next] Cache invalidated." }
				next.cancel()
				reverseCache.remove(next)
				directoryListingCache.remove(reverse)
			}
		}
	}

	var css: String = ""

	@OptIn(ExperimentalEncodingApi::class)
	fun getDirectoryListingHTML(store: File, file: File): CachedList {
		logger.finer { "Getting directory listing for $file, store: $store" }
		val cache = directoryListingCache[file]
		if (cache != null) return cache
		val computed = computeDirectoryListingHTML(store, file)
		val watchKey = file.toPath().register(
			watcher,
			StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE,
			StandardWatchEventKinds.ENTRY_MODIFY
		)
		val cachedList = CachedList(
			computed,
			Base64.encode(hasher.digest(computed.encodeToByteArray()))
		)
		directoryListingCache[file] = cachedList
		reverseCache[watchKey] = file
		return cachedList
	}

	fun computeDirectoryListingHTML(store: File, file: File): String = buildString {
		logger.finer { "Computing directory listing for $file, store: $store" }
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
}