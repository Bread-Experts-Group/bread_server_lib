package org.bread_experts_group.protocol.http.html

import org.bread_experts_group.io.retrieveBasicAttributes
import org.bread_experts_group.logging.ColoredHandler
import org.bread_experts_group.resource.DirectoryListingResource
import java.nio.file.*
import java.security.MessageDigest
import java.time.Instant
import java.time.ZoneId
import java.time.chrono.Chronology
import java.time.format.DateTimeFormatter
import java.time.format.DecimalStyle
import java.time.format.FormatStyle
import java.time.format.ResolverStyle
import java.util.*
import java.util.stream.Collectors
import kotlin.io.path.*
import kotlin.random.Random

object DirectoryListing {
	private val logger = ColoredHandler.newLoggerResourced("html_directory_listing")
	private val watcher = FileSystems.getDefault().newWatchService()
	private val directoryListingCache = mutableMapOf<Path, MutableMap<Locale, CachedList>>()
	private val directoryStatistics = mutableMapOf<Path, ComputedDirectoryStatistics>()
	private val reverseCache = mutableMapOf<WatchKey, Path>()
	private val hasher = MessageDigest.getInstance("MD5")

	fun computeDirectoryStatistics(path: Path): ComputedDirectoryStatistics = directoryStatistics.getOrPut(path) {
		val stat = ComputedDirectoryStatistics()
		runCatching {
			Files.list(path)
				.parallel()
				.map { file -> file to file.retrieveBasicAttributes() }
				.forEach { (file, attr) ->
					if (attr.isSymbolicLink) return@forEach
					if (attr.isDirectory) {
						stat.merge(computeDirectoryStatistics(file))
						stat.directories.increment()
					} else {
						stat.files.increment()
						stat.calculatedSize.add(attr.size())
					}
				}
		}.onFailure {
			stat.errored.increment()
			if (it is FileSystemException) stat.unreadable.increment()
		}
		stat
	}

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
				directoryStatistics.remove(reverse)
				var parent = reverse.parent
				while (parent != null) {
					// Invalidate upper caches in case a directory size was cached
					directoryStatistics.remove(parent)
					val wasRemoved = directoryListingCache.remove(parent)
					if (wasRemoved != null) reverseCache.filterNotTo(reverseCache) { it.value.name == parent.name }
					parent = parent.parent
				}
			}
		}
	}

	val base64Encoder: Base64.Encoder = Base64.getUrlEncoder().withoutPadding()
	fun getDirectoryListingHTML(
		store: Path, file: Path,
		locale: Locale = Locale.getDefault()
	): CachedList {
		logger.finer { "Getting directory listing for $file, store: $store" }
		val cache = directoryListingCache[file]?.get(locale)
		if (cache != null) return cache
		val computed = computeDirectoryListingHTML(store, file, locale)
		val watchKey = file.register(
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
		store: Path, file: Path,
		locale: Locale = Locale.getDefault()
	): String = buildString {
		val bundle = DirectoryListingResource.get(locale)
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
			val store = file.fileStore()
			val sizeStat = buildString {
				append(" [ ")
				append(truncateSizeHTML(store.usableSpace) + " / ")
				append(truncateSizeHTML(store.unallocatedSpace) + " / ")
				append(truncateSizeHTML(store.totalSpace) + " ]")
			}
			val createdAt = Instant.ofEpochMilli(System.currentTimeMillis())
				.atZone(ZoneId.systemDefault())
				.format(dateTimeFormatter)
			"$sizeStat [${createdAt}]</caption></tbody></table></body></html>"
		}
		if (!file.startsWith(store)) {
			append("<tr><td>${bundle.getString("outside_of_store")}</tr></tbody>")
			append("<caption>")
			append(file.pathString)
			append(trailer)
			return@buildString
		}
		val storeParent = store.parent
		Files.list(file)
			.parallel()
			.map { file -> file to file.retrieveBasicAttributes() }
			.map { (file, attr) ->
				var localEntry = ""
				val (classes, title) = run {
					val titles = mutableListOf<String>()
					val classes = mutableListOf<String>()
//						if (!readable) {
//							classes.add("dotted")
//							titles.add(bundle.getString("unreadable"))
//						}
					if (attr.isSymbolicLink) {
						classes.add("symlink")
						titles.add(bundle.getString("this_is_symlink"))
					}
					(if (classes.isNotEmpty()) "class=\"${classes.joinToString(" ")}\"" else "") to
							(if (titles.isNotEmpty()) "title=\"${titles.joinToString(", ")}\"" else "")
				}
				if (attr.isDirectory) {
					val stat = computeDirectoryStatistics(file)
//						if (readable)
					localEntry += "<tr><td><a $classes $title href=\"${file.name}/\">${file.name}</a>/</td>"
//						else append("<tr><td><u $classes $title>${file.name}</u>/</td>")
					localEntry += "<td>${truncateSizeHTML(stat.calculatedSize.sum())} ["
					val filesSum = stat.files.sum()
					val directoriesSum = stat.directories.sum()
					val entryCount = if (filesSum + directoriesSum > 0) buildList {
						if (filesSum > 0) add("${stat.files} ${bundle.getString("files").lowercase()}")
						if (directoriesSum > 0) add("$directoriesSum ${bundle.getString("folders").lowercase()}")
					}.joinToString(", ") else bundle.getString("empty").lowercase()
					val erroredSum = stat.errored.sum()
					val unreadableSum = stat.unreadable.sum()
					val loopsSum = stat.loops.sum()
					if (erroredSum + unreadableSum + loopsSum > 0) {
						localEntry += "<u class=\"dotted\" title=\""
						localEntry += buildList {
							if (erroredSum > 0)
								add("$erroredSum ${bundle.getString("tree_errors").lowercase()}")
							if (unreadableSum > 0)
								add("$unreadableSum ${bundle.getString("unreadable").lowercase()}")
							if (loopsSum > 0)
								add("$loopsSum ${bundle.getString("loops").lowercase()}")
						}.joinToString(", ")
						localEntry += "\">$entryCount</u>"
					} else localEntry += entryCount
					localEntry += "]</td>"
				} else {
//						if (readable)
					localEntry += "<tr><td><a $classes $title href=\"${file.name}\">${file.name}</a>"
//						else append("<tr><td><u $classes $title>${file.name}</u>")
					localEntry += "</td><td>${truncateSizeHTML(attr.size())}</td>"
				}
				val mod = Instant.ofEpochMilli(attr.lastModifiedTime().toMillis())
					.atZone(ZoneId.systemDefault())
					.format(dateTimeFormatter)
				localEntry += "<td>$mod</td></tr>"
				localEntry to attr
			}
			.sorted { (_, attrA), (_, attrB) ->
				val dir = attrB.isDirectory.compareTo(attrA.isDirectory)
				if (dir != 0) return@sorted dir
				attrB.lastModifiedTime().compareTo(attrA.lastModifiedTime())
			}
			.collect(Collectors.toList()).forEach { append(it.first) }
		append("<caption>")
		val parentInvariantPath = "${storeParent.invariantSeparatorsPathString}/"
		append(parentInvariantPath)
		val accessible = file.invariantSeparatorsPathString.substring(parentInvariantPath.length)
		var completeCaption = ""
		var backReferences = ""
		accessible.split('/').also {
			val markerList = buildList {
				var localPath = storeParent
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