package org.bread_experts_group.fuse

import org.bread_experts_group.debugString
import org.bread_experts_group.getDowncall
import org.bread_experts_group.getDowncallVoid
import org.bread_experts_group.getLookup
import java.io.File
import java.lang.AutoCloseable
import java.lang.foreign.*
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.nio.file.Files
import java.util.logging.Logger

class FileSystemInUserSpace(
	mountFile: File,
	private val linker: Linker = Linker.nativeLinker(),
	callbacks: FUSECallbacks = FUSECallbacks()
) : AutoCloseable {
	private var isClosed = false
	private val localArena = Arena.ofAuto()
	private val fuseLookup: SymbolLookup = this.localArena.getLookup("libfuse3.so.3")
	private val fuseLogger: Logger = Logger.getLogger("FUSE")
	private val fuseSession: MemorySegment
	private val fuseSessionLoop: Thread

	init {
		if (mountFile.exists()) throw IllegalStateException("FUSE target must not exist!")
		Files.createDirectory(mountFile.toPath())
		val fuseArgumentsStructure: StructLayout = MemoryLayout.structLayout(
			ValueLayout.JAVA_INT.withName("argc"),
			MemoryLayout.paddingLayout(4),
			ValueLayout.ADDRESS.withName("argv"),
			ValueLayout.JAVA_LONG.withName("allocated"),
			MemoryLayout.paddingLayout(4)
		)
		val argumentsActualStructure = MemoryLayout.sequenceLayout(1, AddressLayout.ADDRESS)
		val argdHandle = argumentsActualStructure.varHandle(MemoryLayout.PathElement.sequenceElement())
		val argcHandle = fuseArgumentsStructure.varHandle(MemoryLayout.PathElement.groupElement("argc"))
		val argvHandle = fuseArgumentsStructure.varHandle(MemoryLayout.PathElement.groupElement("argv"))
		val allocHandle = fuseArgumentsStructure.varHandle(MemoryLayout.PathElement.groupElement("allocated"))

		val argumentsDataSegment = this.localArena.allocate(argumentsActualStructure)
		argdHandle.set(argumentsDataSegment, 0L, this.localArena.allocateUtf8String("fuse_remote_microserver"))
		val argumentsSegment = this.localArena.allocate(fuseArgumentsStructure)
		argcHandle.set(argumentsSegment, 1)
		argvHandle.set(argumentsSegment, argumentsDataSegment)
		allocHandle.set(argumentsSegment, 1)

		val fuseCallbacksStructure: StructLayout = MemoryLayout.structLayout(
			ValueLayout.ADDRESS.withName("init"),
			ValueLayout.ADDRESS.withName("destroy"),
			ValueLayout.ADDRESS.withName("lookup"),
			ValueLayout.ADDRESS.withName("mkdir"),
			ValueLayout.ADDRESS.withName("readdir"),
			ValueLayout.ADDRESS.withName("statfs"),
		)
		val callbacksInitHandle = fuseCallbacksStructure.varHandle(MemoryLayout.PathElement.groupElement("init"))
		val callbacksDestroyHandle = fuseCallbacksStructure.varHandle(MemoryLayout.PathElement.groupElement("destroy"))
		val callbacksLookupHandle = fuseCallbacksStructure.varHandle(MemoryLayout.PathElement.groupElement("lookup"))
		val callbacksMkdirHandle = fuseCallbacksStructure.varHandle(MemoryLayout.PathElement.groupElement("mkdir"))
		val callbacksReaddirHandle = fuseCallbacksStructure.varHandle(MemoryLayout.PathElement.groupElement("readdir"))
		val callbacksStatfsHandle = fuseCallbacksStructure.varHandle(MemoryLayout.PathElement.groupElement("statfs"))
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

		val nativeFuseNewSession: MethodHandle = fuseLookup.getDowncall(
			linker, "fuse_session_new", ValueLayout.ADDRESS,
			ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_LONG, ValueLayout.ADDRESS
		)

		fuseSession = nativeFuseNewSession.invokeExact(
			argumentsSegment,
			callbacksDataSegment,
			callbacksDataSegment.byteSize(),
			MemorySegment.NULL
		) as MemorySegment
		if (fuseSession.address() == 0L) throw IllegalStateException("Failure to create the fuse session!")
		fuseLogger.info { "Created fuse session: ${fuseSession.debugString()}" }

		val nativeFuseMount: MethodHandle = fuseLookup.getDowncall(
			linker, "fuse_session_mount", ValueLayout.JAVA_INT,
			ValueLayout.ADDRESS, ValueLayout.ADDRESS
		)

		val path = mountFile.canonicalPath
		val pathSegment = this.localArena.allocateUtf8String(path)
		fuseLogger.info { "fuse_session_mount call for path \"$path\"" }
		val code = nativeFuseMount.invokeExact(fuseSession, pathSegment) as Int
		if (code == 0) fuseLogger.info { "fuse_session_mount completed successfully for the path \"$path\"" }
		else fuseLogger.severe { "fuse_session_mount failed for the path \"$path\", code: $code" }

		val nativeFuseSessionLoop: MethodHandle = fuseLookup.getDowncall(
			linker, "fuse_session_loop", ValueLayout.JAVA_INT,
			ValueLayout.ADDRESS
		)
		fuseSessionLoop = Thread.ofPlatform().name("FUSE Session Native Loop").start {
			val returnCode = nativeFuseSessionLoop.invokeExact(fuseSession) as Int
			fuseLogger.info { "fuse_session_loop return code: $returnCode" }
		}
	}


	override fun close() {
		if (isClosed) return
		isClosed = true
		fuseLookup.getDowncallVoid(
			this.linker, "fuse_session_unmount", ValueLayout.ADDRESS
		).invokeExact(fuseSession)
		fuseLookup.getDowncallVoid(
			this.linker, "fuse_session_exit", ValueLayout.ADDRESS
		).invokeExact(fuseSession)
		fuseSessionLoop.join()
		fuseLookup.getDowncallVoid(
			this.linker, "fuse_session_destroy", ValueLayout.ADDRESS
		).invokeExact(fuseSession)
	}
}