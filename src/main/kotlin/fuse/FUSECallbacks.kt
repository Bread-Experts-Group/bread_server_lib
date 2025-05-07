package org.bread_experts_group.fuse

import org.bread_experts_group.debugString
import org.bread_experts_group.getDowncall
import org.bread_experts_group.getLookup
import java.lang.foreign.*
import java.lang.invoke.MethodHandle
import java.util.logging.Logger

open class FUSECallbacks(linker: Linker = Linker.nativeLinker()) {
	private val localArena = Arena.ofAuto()
	private val fuseLookup: SymbolLookup = this.localArena.getLookup("libfuse3.so.3")
	private val logger: Logger = Logger.getLogger("FUSE Callbacks")

	private val fuseEntryParamStructure: StructLayout = MemoryLayout.structLayout(
		ValueLayout.JAVA_LONG.withName("ino"),
		ValueLayout.JAVA_LONG.withName("generation"),
		MemoryLayout.paddingLayout(144).withName("attr"),
		ValueLayout.JAVA_DOUBLE.withName("attr_timeout"),
		ValueLayout.JAVA_DOUBLE.withName("entry_timeout"),
	)
	private val inoHandle = this.fuseEntryParamStructure.varHandle(MemoryLayout.PathElement.groupElement("ino"))
	private val generationHandle = this.fuseEntryParamStructure.varHandle(MemoryLayout.PathElement.groupElement("generation"))
	private val attrTimeoutHandle = this.fuseEntryParamStructure.varHandle(MemoryLayout.PathElement.groupElement("attr_timeout"))
	private val entrTimeoutHandle = this.fuseEntryParamStructure.varHandle(MemoryLayout.PathElement.groupElement("entry_timeout"))

	private val nativeFuseReplyEntry: MethodHandle = fuseLookup.getDowncall(
		linker, "fuse_reply_entry", ValueLayout.JAVA_INT,
		ValueLayout.ADDRESS, ValueLayout.ADDRESS
	)

	protected fun replyEntry(
		req: MemorySegment,
		inode: Long, generation: Long, attributesLifetime: Double, entryLifetime: Double
	) {
		val param = this.localArena.allocate(fuseEntryParamStructure)
		inoHandle.set(param, inode)
		generationHandle.set(param, generation)
		attrTimeoutHandle.set(param, attributesLifetime)
		entrTimeoutHandle.set(param, entryLifetime)
		val returnCode = this.nativeFuseReplyEntry.invokeExact(req, param) as Int
		if (returnCode != 0) throw kotlin.IllegalStateException("fuse_reply_entry errno $returnCode")
	}

	private val nativeFuseReplyError: MethodHandle = fuseLookup.getDowncall(
		linker, "fuse_reply_err", ValueLayout.JAVA_INT,
		ValueLayout.ADDRESS, ValueLayout.JAVA_INT
	)

	protected fun replyError(
		req: MemorySegment,
		code: Int
	) {
		val returnCode = this.nativeFuseReplyError.invokeExact(req, code) as Int
		if (returnCode != 0) throw kotlin.IllegalStateException("fuse_reply_err errno $returnCode")
	}

	@Suppress("unused")
	open fun init(userdata: MemorySegment, connectionInfo: MemorySegment) {
		logger.info("[init] ${userdata.debugString()} / ${connectionInfo.debugString()}")
	}

	@Suppress("unused")
	open fun destroy(userdata: MemorySegment) {
		logger.info("[destroy] ${userdata.debugString()}")
	}

	@Suppress("unused")
	open fun lookup(handle: MemorySegment, inode: Long, name: MemorySegment) {
		logger.info("[lookup] ${handle.debugString()} / $inode / ${name.debugString()}")
		replyError(handle, 100)
	}

	@Suppress("unused")
	open fun mkdir(handle: MemorySegment, parentInode: Long, name: MemorySegment, mode: Int) {
		logger.info("[mkdir] ${handle.debugString()} / $parentInode / ${name.debugString()} / $mode")
		replyError(handle, 100)
	}

	@Suppress("unused")
	open fun readdir(handle: MemorySegment, inode: Long, size: Long, offset: Long, fileInfo: MemorySegment) {
		logger.info("[readdir] ${handle.debugString()} / $inode / $size / $offset / ${fileInfo.debugString()}")
		replyError(handle, 100)
	}

	@Suppress("unused")
	open fun statfs(handle: MemorySegment, inode: Long) {
		logger.info("[statfs] ${handle.debugString()} / $inode")
		replyError(handle, 100)
	}
}