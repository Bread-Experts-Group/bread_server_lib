package org.bread_experts_group.http.html

import org.bread_experts_group.logging.ColoredHandler
import java.io.File
import java.io.IOException
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.security.MessageDigest
import java.time.Instant
import java.time.ZoneId
import java.time.chrono.Chronology
import java.time.format.DateTimeFormatter
import java.time.format.DecimalStyle
import java.time.format.FormatStyle
import java.time.format.ResolverStyle
import java.util.*
import kotlin.io.path.*
import kotlin.random.Random

object DirectoryListing {
	private val logger = ColoredHandler.newLogger("HTML Directory Listing")
	private val watcher = FileSystems.getDefault().newWatchService()
	private val directoryListingCache = mutableMapOf<File, MutableMap<Locale, CachedList>>()
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

	val base64Encoder: Base64.Encoder = Base64.getUrlEncoder().withoutPadding()
	fun getDirectoryListingHTML(
		store: File, file: File,
		locale: Locale = Locale.getDefault()
	): CachedList {
		logger.finer { "Getting directory listing for $file, store: $store" }
		val cache = directoryListingCache[file]?.get(locale)
		if (cache != null) return cache
		val computed = computeDirectoryListingHTML(store, file, locale)
		val watchKey = file.toPath().register(
			watcher,
			StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE,
			StandardWatchEventKinds.ENTRY_MODIFY
		)
		val cachedList = CachedList(
			computed,
			base64Encoder.encodeToString(hasher.digest(computed.encodeToByteArray()))
		)
		directoryListingCache.getOrPut(file) { mutableMapOf() }[locale] = cachedList
		reverseCache[watchKey] = file
		return cachedList
	}

	var css: String = ""
	val directoryListingStyle: String
		get() = buildString {
			append("*{font-family:monospace;text-align:left;$css}")
			append(".dotted{text-decoration:underline dotted}table{width:100%}")
			append(".symlink{background-color:#FFFF0055}")
		}
	val directoryListingFile: String = base64Encoder.encodeToString(Random.nextBytes(32)) + ".css"

	fun computeDirectoryListingHTML(
		store: File, file: File,
		locale: Locale = Locale.getDefault()
	): String = buildString {
		val bundle = ResourceBundle.getBundle(
			"org.bread_experts_group.resource.DirectoryListingResource",
			locale
		)
		logger.finer { "Computing directory listing for $file, store: $store" }
		val dateTimeFormatter = DateTimeFormatter
			.ofLocalizedDateTime(FormatStyle.FULL)
			.withLocale(locale)
			.withZone(ZoneId.systemDefault())
			.withChronology(Chronology.ofLocale(locale))
			.withDecimalStyle(DecimalStyle.of(locale))
			.withResolverStyle(ResolverStyle.SMART)
		append("<!doctype html><html><head><link rel=\"stylesheet\" href=\"/$directoryListingFile\"></head><body>")
		append("<table><thead><th>${bundle.getString("name")}</th><th>${bundle.getString("size")}")
		append("</th><th>${bundle.getString("last_modification")}</th></thead><tbody>")
		val trailer = run {
			val sizeStat = buildString {
				append(" [ ")
				append(truncateSizeHTML(file.usableSpace) + " / ")
				append(truncateSizeHTML(file.freeSpace) + " / ")
				append(truncateSizeHTML(file.totalSpace) + " ]")
			}
			val createdAt = Instant.ofEpochMilli(System.currentTimeMillis())
				.atZone(ZoneId.systemDefault())
				.format(dateTimeFormatter)
			"$sizeStat [${createdAt}]</caption></tbody></table></body></html>"
		}
		if (!file.canonicalPath.startsWith(store.canonicalPath)) {
			append("<tr><td>${bundle.getString("outside_of_store")}</tr></tbody>")
			append("<caption>")
			append(file.canonicalPath)
			append(trailer)
			return@buildString
		}
		val storeParent = store.parentFile
		val files = file.listFiles()
		if (files != null) {
			if (files.isNotEmpty()) {
				files.sortByDescending { it.lastModified() }
				files.sortByDescending { it.isDirectory }
				files.forEach { file ->
					val thisPath = file.toPath()
					val (classes, title) = run {
						val titles = mutableListOf<String>()
						val classes = mutableListOf<String>()
						if (!Files.isReadable(thisPath)) {
							classes.add("dotted")
							titles.add(bundle.getString("unreadable"))
						}
						if (Files.isSymbolicLink(thisPath)) {
							classes.add("symlink")
							titles.add(bundle.getString("this_is_symlink"))
						}
						(if (classes.isNotEmpty()) "class=\"${classes.joinToString(" ")}\"" else "") to
								(if (titles.isNotEmpty()) "title=\"${titles.joinToString(", ")}\"" else "")
					}
					if (thisPath.isDirectory()) {
						var errored = 0
						var unreadable = 0
						var calculatedSize = 0L
						var files = 0
						var loops = 0
						var directories = 0
						Files.walkFileTree(
							thisPath,
							setOf(FileVisitOption.FOLLOW_LINKS),
							Int.MAX_VALUE,
							object : FileVisitor<Path> {
								override fun visitFileFailed(file: Path, exc: IOException): FileVisitResult {
									when (exc) {
										is FileSystemLoopException -> loops++
										is AccessDeniedException -> unreadable++
										else -> {
											errored++
											logger.warning { "Support needed for [${exc::class.java.canonicalName}]!" }
										}
									}
									return FileVisitResult.SKIP_SUBTREE
								}

								override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
									files++
									calculatedSize += file.fileSize()
									return FileVisitResult.CONTINUE
								}

								override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult {
									if (!dir.isReadable()) {
										unreadable++
										return FileVisitResult.SKIP_SUBTREE
									}
									return FileVisitResult.CONTINUE
								}

								override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult {
									if (thisPath != dir) directories++
									return FileVisitResult.CONTINUE
								}
							}
						)
						if (thisPath.isReadable())
							append("<tr><td><a $classes $title href=\"${thisPath.name}/\">${thisPath.name}</a>/</td>")
						else append("<tr><td><u $classes $title>${thisPath.name}</u>/</td>")
						append("<td>${truncateSizeHTML(calculatedSize)} [")
						val entryCount = if (files + directories > 0) buildList {
							if (files > 0) add("$files ${bundle.getString("files").lowercase()}")
							if (directories > 0) add("$directories ${bundle.getString("folders").lowercase()}")
						}.joinToString(", ") else bundle.getString("empty").lowercase()
						if (errored + unreadable + loops > 0) {
							append("<u class=\"dotted\" title=\"")
							append(
								buildList {
									if (errored > 0)
										add("$errored ${bundle.getString("tree_errors").lowercase()}")
									if (unreadable > 0)
										add("$unreadable ${bundle.getString("unreadable").lowercase()}")
									if (loops > 0)
										add("$loops ${bundle.getString("loops").lowercase()}")
								}.joinToString(", ")
							)
							append("\">$entryCount</u>")
						} else append(entryCount)
						append("]</td>")
					} else {
						if (thisPath.isReadable())
							append("<tr><td><a $classes $title href=\"${thisPath.name}\">${thisPath.name}</a>")
						else append("<tr><td><u $classes $title>${thisPath.name}</u>")
						append("</td><td>${truncateSizeHTML(thisPath.fileSize())}</td>")
					}
					val mod = Instant.ofEpochMilli(thisPath.getLastModifiedTime().toMillis())
						.atZone(ZoneId.systemDefault())
						.format(dateTimeFormatter)
					append("<td>$mod</td></tr>")
				}
			} else append("<tr><td>${bundle.getString("folder_empty")}</td><td>-1</td><td>-1</td></tr>")
		} else append("<tr><td>${bundle.getString("folder_inaccessible")}</td><td>-1</td><td>-1</td></tr>")
		append("<caption>")
		val parentInvariantPath = "${storeParent.invariantSeparatorsPath}/"
		append(parentInvariantPath)
		val accessible = file.invariantSeparatorsPath.substring(parentInvariantPath.length)
		var completeCaption = ""
		var backReferences = ""
		accessible.split('/').also {
			val markerList = buildList {
				var localPath = storeParent.toPath()
				it.forEach { path ->
					localPath = localPath.resolve(path)
					if (localPath.isSymbolicLink())
						add(" class=\"symlink\" title=\"${bundle.getString("this_is_symlink")}\"")
					else add("")
				}
			}
			it.asReversed().forEachIndexed { index, path ->
				val marker = markerList[it.size - index - 1]
				completeCaption =
					if (index == 0) "<span$marker>$path</span>"
					else "<a$marker href=\"$backReferences\">$path</a>/$completeCaption"
				backReferences += "../"
			}
		}
		append(completeCaption)
		append(trailer)
	}
}