package org.bread_experts_group.http.html

import java.io.File
import java.io.IOException
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
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
				var parent = reverse.parentFile
				while (parent != null) {
					// Invalidate upper caches in case a directory size was cached
					val wasRemoved = directoryListingCache.remove(parent)
					if (wasRemoved != null) reverseCache.filterNotTo(reverseCache) { it.value.name == parent.name }
					parent = parent.parentFile
				}
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
		val itParent = store.parentFile
		val files = file.listFiles()
		if (files != null) {
			if (files.isNotEmpty()) {
				files.sortByDescending { it.lastModified() }
				files.sortByDescending { it.isDirectory }
				files.forEach {
					if (it.isDirectory) {
						var errored = 0
						var unreadable = 0
						var calculatedSize = 0L
						var files = 0
						Files.walkFileTree(it.toPath(), object : SimpleFileVisitor<Path>() {
							override fun visitFileFailed(file: Path, exc: IOException): FileVisitResult {
								errored++
								return FileVisitResult.SKIP_SUBTREE
							}

							override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
								files++
								calculatedSize += file.toFile().length()
								return FileVisitResult.CONTINUE
							}

							override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult {
								if (!dir.toFile().canRead()) {
									unreadable++
									return FileVisitResult.SKIP_SUBTREE
								}
								return FileVisitResult.CONTINUE
							}

							override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult {
								return FileVisitResult.CONTINUE
							}
						})
						if (it.canRead()) append("<tr><td><a href=\"${it.name}/\">${it.name}</a>/</td>")
						else append("<tr><td><span class=\"tooltip\" title=\"Unreadable\">${it.name}</span>/</td>")
						append("<td>${truncateSizeHTML(calculatedSize)} [")
						val fileCount = if (files > 0) "$files files" else "empty"
						if (errored + unreadable > 0) {
							append("<span class=\"tooltip\" title=\"")
							append(
								buildList {
									if (errored > 0) add("$errored tree errors")
									if (unreadable > 0) add("$unreadable unreadable")
								}.joinToString(", ")
							)
							append("\">$fileCount</span>")
						} else append(fileCount)
						append("]</td>")
					} else {
						if (it.canRead()) append("<tr><td><a href=\"${it.name}\">${it.name}</a></td>")
						else append("<tr><td><span class=\"tooltip\" title=\"Unreadable\">${it.name}</span></td>")
						append("<td>${truncateSizeHTML(it.length())}</td>")
					}
					val mod = Instant.ofEpochMilli(it.lastModified())
						.atZone(ZoneId.systemDefault())
						.format(DateTimeFormatter.RFC_1123_DATE_TIME)
					append("<td>$mod</td></tr>")
				}
			} else append("<tr><td>Folder empty</td><td>-1</td><td>-1</td></tr>")
		} else append("<tr><td>Folder not accessible</td><td>-1</td><td>-1</td></tr>")
		append("<caption>")
		append(itParent.invariantSeparatorsPath + '/')
		val accessible = file.invariantSeparatorsPath
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
		val createdAt = Instant.ofEpochMilli(System.currentTimeMillis())
			.atZone(ZoneId.systemDefault())
			.format(DateTimeFormatter.RFC_1123_DATE_TIME)
		append("$completeCaption$sizeStat [${createdAt}]</caption></tbody></table></body></html>")
	}
}