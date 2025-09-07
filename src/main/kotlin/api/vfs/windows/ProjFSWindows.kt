package org.bread_experts_group.api.vfs.windows

import org.bread_experts_group.api.vfs.*
import org.bread_experts_group.ffi.windows.*
import java.lang.foreign.*
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.nio.file.LinkOption
import java.nio.file.Path
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import kotlin.io.path.Path

private typealias EnumSession = Pair<Iterator<Pair<String, VirtualEntry>>, MemorySegment?>

class ProjFSWindows : VirtualFileSystemProvider(arrayOf("Windows 11")) {
	private val arena: Arena = Arena.ofAuto()
	private val linker: Linker = Linker.nativeLinker()
	private val virtualizationInstanceID: MemorySegment = arena.allocate(win32GUID)
	private val namespaceVirtualizationContext: MemorySegment = arena.allocate(ValueLayout.ADDRESS)

	init {
		nativeCoCreateGuid.invokeExact(virtualizationInstanceID) as Int
	}

	private var root: Path? = null
	override fun setRoot(path: Path) {
		val realPath = path.toRealPath(LinkOption.NOFOLLOW_LINKS)
		decodeCOMError(
			arena,
			nativePrjMarkDirectoryAsPlaceholder.invokeExact(
				stringToPCWSTR(arena, realPath.toString()),
				MemorySegment.NULL,
				MemorySegment.NULL,
				virtualizationInstanceID
			) as Int
		)
		root = realPath
	}

	private fun Instant.toWindowsTime(): Long = this.plusSeconds(11644473600).toEpochMilli() * 10000
	private fun VirtualEntry.toBasic(w: MemorySegment) {
		prjFileBasicInfoCreationTime.set(w, this.creationTime.toWindowsTime())
		prjFileBasicInfoAccessTime.set(w, this.accessTime.toWindowsTime())
		prjFileBasicInfoWriteTime.set(w, this.writeTime.toWindowsTime())
		prjFileBasicInfoChangeTime.set(w, this.changeTime.toWindowsTime())
		when (this) {
			is VirtualFile -> prjFileBasicInfoFileSize.set(w, this.size.toLong())
			is VirtualDirectory -> prjFileBasicInfoIsDirectory.set(w, true)
		}
	}

	private val enumerations = ConcurrentHashMap<WindowsGUID, EnumSession>()
	private fun upcPRJaSTARTaDIRECTORYaENUMERATIONaCB(
		callbackData: MemorySegment,
		enumerationGUID: MemorySegment
	): Int {
		return 0
	}

	private fun upcPRJaENDaDIRECTORYaENUMERATIONaCB(
		callbackData: MemorySegment,
		enumerationGUID: MemorySegment
	): Int {
		val enumeration = segmentToGUID(enumerationGUID.reinterpret(win32GUID.byteSize()), 0)
		enumerations.remove(enumeration)
		return 0
	}

	private fun upcPRJaGETaDIRECTORYaENUMERATIONaCB(
		callbackData: MemorySegment,
		enumerationGUID: MemorySegment,
		searchExpression: MemorySegment,
		dirEntryBufferHandle: MemorySegment
	): Int {
		val callbackData = callbackData.reinterpret(prjCallbackData.byteSize())
		val filePathName = (prjCallbackDataFilePathName.get(callbackData) as MemorySegment)
			.reinterpret(Long.MAX_VALUE)
		val flags = prjCallbackDataFlags.get(callbackData) as Int
		val enumeration = segmentToGUID(enumerationGUID.reinterpret(win32GUID.byteSize()), 0)
		if (flags and 1 == 1 || !enumerations.containsKey(enumeration)) {
			val enumerated = enumerateDirectory(
				DirectoryEnumerationContext(Path(wPCWSTRToString(filePathName)))
			).iterator()
			enumerations[enumeration] = enumerated to if (searchExpression != MemorySegment.NULL)
				searchExpression.reinterpret(Long.MAX_VALUE)
			else null
		}
		val (iterator, match) = enumerations.getValue(enumeration)
		while (iterator.hasNext()) {
			val (name, entry) = iterator.next()
			val path = stringToPCWSTR(arena, name)
			if (match == null || nativePrjFileNameMatch.invokeExact(path, match) as Boolean) {
				val state = arena.allocate(ValueLayout.JAVA_INT)
				val fullPath = stringToPCWSTR(arena, root!!.resolve(name).toString())
				val stateStatus = nativePrjGetOnDiskFileState.invokeExact(fullPath, state) as Int
				if (stateStatus == 0) continue
				else if (stateStatus != 0x80070002.toInt()) decodeCOMError(arena, stateStatus)
				val basic = arena.allocate(prjFileBasicInfo)
				entry.toBasic(basic)
				decodeCOMError(
					arena,
					nativePrjFillDirEntryBuffer.invokeExact(path, basic, dirEntryBufferHandle) as Int
				)
				if (flags and 2 == 2) break
			}
		}
		return 0
	}

	private fun upcPRJaGETaPLACEHOLDERaINFOaCB(
		callbackData: MemorySegment
	): Int {
		val callbackData = callbackData.reinterpret(prjCallbackData.byteSize())
		val context = prjCallbackDataNamespaceVirtualizationContext.get(callbackData) as MemorySegment
		val filePathName = (prjCallbackDataFilePathName.get(callbackData) as MemorySegment)
			.reinterpret(Long.MAX_VALUE)
		val entry = placeholderInformation(
			PlaceholderInformationContext(Path(wPCWSTRToString(filePathName)))
		) ?: return win32ToHResult(2)
		val placeholder = arena.allocate(prjPlaceholderInfo)
		val basic = prjPlaceholderInfoFileBasicInfo.invokeExact(placeholder) as MemorySegment
		entry.toBasic(basic)
		decodeCOMError(
			arena,
			nativePrjWritePlaceholderInfo.invokeExact(
				context, filePathName,
				placeholder, placeholder.byteSize().toInt()
			) as Int
		)
		return 0
	}

	private fun upcPRJaGETaFILEaDATAaCB(
		callbackData: MemorySegment,
		byteOffset: Long,
		length: Int,
	): Int {
		val callbackData = callbackData.reinterpret(prjCallbackData.byteSize())
		val context = prjCallbackDataNamespaceVirtualizationContext.get(callbackData) as MemorySegment
		val filePathName = (prjCallbackDataFilePathName.get(callbackData) as MemorySegment)
			.reinterpret(Long.MAX_VALUE)
		val dataStreamID = prjCallbackDataDataStreamID.invokeExact(callbackData) as MemorySegment
		val buffer = fileData(
			FileDataContext(Path(wPCWSTRToString(filePathName)), byteOffset.toULong(), length.toUInt())
		) ?: return 0
		val remaining = buffer.remaining()
		if (remaining == 0) return 0
		val allocated = nativePrjAllocateAlignedBuffer(context, remaining) as MemorySegment
		if (allocated == MemorySegment.NULL) return win32ToHResult(8)
		allocated.reinterpret(remaining.toLong()).asByteBuffer().put(buffer)
		decodeCOMError(
			arena,
			nativePrjWriteFileData.invokeExact(
				context,
				dataStreamID,
				allocated,
				byteOffset,
				remaining
			) as Int
		)
		nativePrjFreeAlignedBuffer.invokeExact(allocated)
		return 0
	}

	private fun upcPRJaNOTIFICATIONaCB(
		callbackData: MemorySegment,
		isDirectory: Boolean,
		notification: Int,
		destinationFileName: MemorySegment,
		operationParameters: MemorySegment,
	): Int {
		println(
			"Not $isDirectory, $notification"
		)
		return win32ToHResult(5)
	}

	override fun remove(
		path: Path,
		readOnly: Boolean, dirtyMetadata: Boolean, dirtyData: Boolean
	) {
		var flags = 0x00000004
		if (dirtyMetadata) flags = flags or 0x00000001
		if (dirtyData) flags = flags or 0x00000002
		if (readOnly) flags = flags or 0x00000020
		decodeCOMError(
			arena,
			nativePrjDeleteFile.invokeExact(
				namespaceVirtualizationContext.get(ValueLayout.ADDRESS, 0),
				stringToPCWSTR(arena, path.toString()),
				flags,
				MemorySegment.NULL
			) as Int
		)
	}

	override fun start() {
		val callbacks = arena.allocate(prjCallbacks)
		val methodHandles = MethodHandles.lookup()
		prjCallbacksSDEC.set(
			callbacks,
			linker.upcallStub(
				methodHandles.findSpecial(
					this::class.java, "upcPRJaSTARTaDIRECTORYaENUMERATIONaCB",
					MethodType.methodType(
						Int::class.java,
						MemorySegment::class.java, MemorySegment::class.java
					), this::class.java
				).bindTo(this),
				FunctionDescriptor.of(
					ValueLayout.JAVA_INT,
					ValueLayout.ADDRESS, ValueLayout.ADDRESS
				),
				arena
			)
		)
		prjCallbacksEDEC.set(
			callbacks,
			linker.upcallStub(
				methodHandles.findSpecial(
					this::class.java, "upcPRJaENDaDIRECTORYaENUMERATIONaCB",
					MethodType.methodType(
						Int::class.java,
						MemorySegment::class.java, MemorySegment::class.java
					), this::class.java
				).bindTo(this),
				FunctionDescriptor.of(
					ValueLayout.JAVA_INT,
					ValueLayout.ADDRESS, ValueLayout.ADDRESS
				),
				arena
			)
		)
		prjCallbacksGDEC.set(
			callbacks,
			linker.upcallStub(
				methodHandles.findSpecial(
					this::class.java, "upcPRJaGETaDIRECTORYaENUMERATIONaCB",
					MethodType.methodType(
						Int::class.java,
						MemorySegment::class.java, MemorySegment::class.java,
						MemorySegment::class.java, MemorySegment::class.java
					), this::class.java
				).bindTo(this),
				FunctionDescriptor.of(
					ValueLayout.JAVA_INT,
					ValueLayout.ADDRESS, ValueLayout.ADDRESS,
					ValueLayout.ADDRESS, ValueLayout.ADDRESS
				),
				arena
			)
		)
		prjCallbacksGPIC.set(
			callbacks,
			linker.upcallStub(
				methodHandles.findSpecial(
					this::class.java, "upcPRJaGETaPLACEHOLDERaINFOaCB",
					MethodType.methodType(
						Int::class.java,
						MemorySegment::class.java
					), this::class.java
				).bindTo(this),
				FunctionDescriptor.of(
					ValueLayout.JAVA_INT,
					ValueLayout.ADDRESS
				),
				arena
			)
		)
		prjCallbacksGFDC.set(
			callbacks,
			linker.upcallStub(
				methodHandles.findSpecial(
					this::class.java, "upcPRJaGETaFILEaDATAaCB",
					MethodType.methodType(
						Int::class.java,
						MemorySegment::class.java, Long::class.java, Int::class.java
					), this::class.java
				).bindTo(this),
				FunctionDescriptor.of(
					ValueLayout.JAVA_INT,
					ValueLayout.ADDRESS, ValueLayout.JAVA_LONG, ValueLayout.JAVA_INT
				),
				arena
			)
		)
		prjCallbacksNC.set(
			callbacks,
			linker.upcallStub(
				methodHandles.findSpecial(
					this::class.java, "upcPRJaNOTIFICATIONaCB",
					MethodType.methodType(
						Int::class.java,
						MemorySegment::class.java, Boolean::class.java, Int::class.java,
						MemorySegment::class.java, MemorySegment::class.java
					), this::class.java
				).bindTo(this),
				FunctionDescriptor.of(
					ValueLayout.JAVA_INT,
					ValueLayout.ADDRESS, ValueLayout.JAVA_BOOLEAN, ValueLayout.JAVA_INT,
					ValueLayout.ADDRESS, ValueLayout.ADDRESS
				),
				arena
			)
		)
		val mappings = arena.allocateArray(prjNotificationMapping, 1)
		prjNotificationMappingNotificationBitMask.set(mappings, 0x00000000) // TODO: Notifications
		prjNotificationMappingNotificationRoot.set(mappings, stringToPCWSTR(arena, ""))
		val startVirtualizingOptions = arena.allocate(prjStartVirtualizingOptions)
		prjStartVirtualizingOptionsNotificationMappings.set(startVirtualizingOptions, mappings)
		prjStartVirtualizingOptionsNotificationMappingsCount.set(startVirtualizingOptions, 1)
		decodeCOMError(
			arena,
			nativePrjStartVirtualizing.invokeExact(
				stringToPCWSTR(arena, root!!.toString()),
				callbacks,
				MemorySegment.NULL,
				startVirtualizingOptions,
				namespaceVirtualizationContext
			) as Int
		)
	}

	override fun stop() {
		nativePrjStopVirtualizing.invokeExact(
			namespaceVirtualizationContext.get(ValueLayout.ADDRESS, 0)
		)
	}
}