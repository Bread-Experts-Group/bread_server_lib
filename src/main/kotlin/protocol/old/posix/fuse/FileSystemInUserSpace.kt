package org.bread_experts_group.protocol.old.posix.fuse

import org.bread_experts_group.debugString
import org.bread_experts_group.logging.ColoredHandler
import java.io.File
import java.lang.AutoCloseable
import java.lang.foreign.*
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.nio.file.Files
import java.util.logging.Logger

class FileSystemInUserSpace(
	mountFile: File,
	callbacks: FUSECallbacks = FUSECallbacks()
) : AutoCloseable {
	private var isClosed = false
	private val localArena = Arena.ofAuto()
	private val fuseLogger: Logger = ColoredHandler.newLoggerResourced("protocol/posix/fuse")
	private val fuseSession: MemorySegment
	private val fuseSessionLoop: Thread

	init {
		if (mountFile.exists()) throw IllegalStateException("FUSE target must not exist!")
		Files.createDirectory(mountFile.toPath())
		val argumentsDataSegment = this.localArena.allocate(argumentsActualStructure)
		// TODO JDK 24
		argdHandle.set(argumentsDataSegment, 0L, this.localArena.allocateUtf8String("fuse_remote_microserver"))
		val argumentsSegment = this.localArena.allocate(fuseArgumentsStructure)
		argcHandle.set(argumentsSegment, 1)
		argvHandle.set(argumentsSegment, argumentsDataSegment)
		allocHandle.set(argumentsSegment, 1)

		val linker = Linker.nativeLinker()
		val callbacksDataSegment = this.localArena.allocate(fuseCallbacksStructure)
		val methodLookup = MethodHandles.lookup()
		callbacksInitHandle.set(
			callbacksDataSegment,
			linker.upcallStub(
				methodLookup.bind(
					callbacks, "init",
					MethodType.methodType(Void.TYPE, MemorySegment::class.java, MemorySegment::class.java)
				),
				FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.ADDRESS),
				this.localArena
			)
		)
		callbacksDestroyHandle.set(
			callbacksDataSegment,
			linker.upcallStub(
				methodLookup.bind(
					callbacks, "destroy",
					MethodType.methodType(Void.TYPE, MemorySegment::class.java)
				),
				FunctionDescriptor.ofVoid(ValueLayout.ADDRESS),
				this.localArena
			)
		)
		callbacksLookupHandle.set(
			callbacksDataSegment,
			linker.upcallStub(
				methodLookup.bind(
					callbacks, "lookup",
					MethodType.methodType(
						Void.TYPE,
						MemorySegment::class.java, Long::class.javaPrimitiveType, MemorySegment::class.java
					)
				),
				FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.JAVA_LONG, ValueLayout.ADDRESS),
				this.localArena
			)
		)
		callbacksMkdirHandle.set(
			callbacksDataSegment,
			linker.upcallStub(
				methodLookup.bind(
					callbacks, "mkdir",
					MethodType.methodType(
						Void.TYPE,
						MemorySegment::class.java, Long::class.javaPrimitiveType, MemorySegment::class.java,
						Int::class.javaPrimitiveType
					)
				),
				FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.JAVA_LONG, ValueLayout.ADDRESS, ValueLayout.JAVA_INT),
				this.localArena
			)
		)
		callbacksReaddirHandle.set(
			callbacksDataSegment,
			linker.upcallStub(
				methodLookup.bind(
					callbacks, "readdir",
					MethodType.methodType(
						Void.TYPE,
						MemorySegment::class.java,
						Long::class.javaPrimitiveType,
						Long::class.javaPrimitiveType,
						Long::class.javaPrimitiveType,
						MemorySegment::class.java
					)
				),
				FunctionDescriptor.ofVoid(
					ValueLayout.ADDRESS,
					ValueLayout.JAVA_LONG,
					ValueLayout.JAVA_LONG,
					ValueLayout.JAVA_LONG,
					ValueLayout.ADDRESS
				),
				this.localArena
			)
		)
		callbacksStatfsHandle.set(
			callbacksDataSegment,
			linker.upcallStub(
				methodLookup.bind(
					callbacks, "statfs",
					MethodType.methodType(
						Void.TYPE,
						MemorySegment::class.java,
						Long::class.javaPrimitiveType
					)
				),
				FunctionDescriptor.ofVoid(
					ValueLayout.ADDRESS,
					ValueLayout.JAVA_LONG
				),
				this.localArena
			)
		)

		fuseSession = nativeFuseNewSession.invokeExact(
			argumentsSegment,
			callbacksDataSegment,
			callbacksDataSegment.byteSize(),
			MemorySegment.NULL
		) as MemorySegment
		if (fuseSession.address() == 0L) throw IllegalStateException("Failure to create the fuse session!")
		fuseLogger.info { "Created fuse session: ${fuseSession.debugString()}" }

		val path = mountFile.canonicalPath
		// TODO JDK 24
		val pathSegment = this.localArena.allocateUtf8String(path)
		fuseLogger.info { "fuse_session_mount call for path \"$path\"" }
		val code = nativeFuseMount.invokeExact(fuseSession, pathSegment) as Int
		if (code == 0) fuseLogger.info { "fuse_session_mount completed successfully for the path \"$path\"" }
		else fuseLogger.severe { "fuse_session_mount failed for the path \"$path\", code: $code" }

		fuseSessionLoop = Thread.ofPlatform().name("FUSE Session Native Loop").start {
			val returnCode = nativeFuseSessionLoop.invokeExact(fuseSession) as Int
			fuseLogger.info { "fuse_session_loop return code: $returnCode" }
		}
	}


	override fun close() {
		if (isClosed) return
		isClosed = true
		nativeFuseSessionUnmount.invokeExact(fuseSession)
		nativeFuseSessionExit.invokeExact(fuseSession)
		fuseSessionLoop.join()
		nativeFuseSessionDestroy.invokeExact(fuseSession)
	}
}