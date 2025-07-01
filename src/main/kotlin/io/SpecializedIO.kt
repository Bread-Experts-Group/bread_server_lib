package org.bread_experts_group.io

import java.lang.foreign.Arena
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileTime
import kotlin.io.path.absolutePathString
import kotlin.io.path.readAttributes

private val operatingSystem = System.getProperty("os.name")
private val attributeRetriever: (Path) -> BasicFileAttributes = when {
	operatingSystem.contains("Windows") -> { path: Path ->
		val arena = Arena.ofConfined()
		val container = arena.allocate(win32FileAttributeData)
		val status = getFileAttributesExW.invokeExact(
			arena.allocateFrom(path.absolutePathString(), Charsets.UTF_16LE),
			0, // GetFileExInfoStandard
			container
		) as Int
		if (status == 0) throw IllegalStateException("Failure. Need error logging...")
		val attributesRaw = (win32FileAttributesVar.get(container, 0) as Int).toLong()
		val creationTime = win32FileTimeToJava(container, win32CreationLowTimeVar, win32CreationHighTimeVar)
		val accessTime = win32FileTimeToJava(container, win32AccessLowTimeVar, win32AccessHighTimeVar)
		val writeTime = win32FileTimeToJava(container, win32WriteLowTimeVar, win32WriteHighTimeVar)
		val size = hlMerge(container, win32SizeLowVar, win32SizeHighVar)
			.coerceAtMost(Long.MAX_VALUE.toULong()).toLong()
		arena.close()
		object : BasicFileAttributes {
			override fun lastModifiedTime(): FileTime = writeTime
			override fun lastAccessTime(): FileTime = accessTime
			override fun creationTime(): FileTime = creationTime

			override fun size(): Long = size
			override fun isOther(): Boolean = false
			override fun isRegularFile(): Boolean = !isDirectory && !isSymbolicLink

			override fun isDirectory(): Boolean =
				attributesRaw and WindowsFileAttributes.FILE_ATTRIBUTE_DIRECTORY.position != 0L

			override fun isSymbolicLink(): Boolean =
				attributesRaw and WindowsFileAttributes.FILE_ATTRIBUTE_REPARSE_POINT.position != 0L

			override fun fileKey(): Any? = path
		}
	}

	else -> { path: Path -> path.readAttributes() }
}

fun Path.retrieveBasicAttributes(): BasicFileAttributes = attributeRetriever(this)