package org.bread_experts_group.fuse

import org.bread_experts_group.debugString
import org.bread_experts_group.logging.ColoredHandler
import org.bread_experts_group.posix.POSIXStat
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.util.logging.Logger

open class FUSECallbacks {
	private val localArena = Arena.ofAuto()
	private val logger: Logger = ColoredHandler.newLoggerResourced("fuse_callbacks")

	protected fun replyBuffer(req: MemorySegment, buf: MemorySegment) {
		val returnCode = nativeFuseReplyEntry.invokeExact(req, buf, buf.byteSize()) as Int
		if (returnCode != 0) throw kotlin.IllegalStateException("fuse_reply_buf errno $returnCode")
	}

	protected fun replyEntry(
		req: MemorySegment,
		inode: Long, generation: Long, attributesLifetime: Double, entryLifetime: Double
	) {
		val param = this.localArena.allocate(fuseEntryParamStructure)
		inoHandle.set(param, inode)
		generationHandle.set(param, generation)
		attrTimeoutHandle.set(param, attributesLifetime)
		entrTimeoutHandle.set(param, entryLifetime)
		val returnCode = nativeFuseReplyEntry.invokeExact(req, param) as Int
		if (returnCode != 0) throw kotlin.IllegalStateException("fuse_reply_entry errno $returnCode")
	}

	protected fun replyError(
		req: MemorySegment,
		code: Int
	) {
		val returnCode = nativeFuseReplyError.invokeExact(req, code) as Int
		if (returnCode != 0) throw kotlin.IllegalStateException("fuse_reply_err errno $returnCode")
	}

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

	/**
	 * @return Directory entry data via [replyBuffer] or an error via [replyError].
	 * @see FUSEDirectoryEntries
	 */
	@Suppress("unused")
	open fun readdir(handle: MemorySegment, inode: Long, size: Long, offset: Long, fileInfo: MemorySegment) {
		logger.info("[readdir] ${handle.debugString()} / $inode / $size / $offset / ${fileInfo.debugString()}")
		val entries = FUSEDirectoryEntries()
		entries.add(FUSEDirectoryEntry("fakefile.troll", POSIXStat()))
		logger.info("[readdir] ${entries.toBuffer(this.localArena, handle)}")
		replyError(handle, 100)
	}

	@Suppress("unused")
	open fun statfs(handle: MemorySegment, inode: Long) {
		logger.info("[statfs] ${handle.debugString()} / $inode")
		replyError(handle, 100)
	}
}