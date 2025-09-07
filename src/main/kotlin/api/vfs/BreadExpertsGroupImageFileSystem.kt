package org.bread_experts_group.api.vfs

import java.math.BigInteger
import java.nio.ByteBuffer
import java.nio.file.*
import java.nio.file.attribute.UserPrincipalLookupService
import java.nio.file.spi.FileSystemProvider

class BreadExpertsGroupImageFileSystem(
	private val provider: BreadExpertsGroupFileSystemProvider,
	image: Path
) : FileSystem() {
	private var buffer = ByteBuffer.allocate(512)
	private val imageChannel = Files.newByteChannel(
		image,
		StandardOpenOption.CREATE,
		StandardOpenOption.READ, StandardOpenOption.WRITE
	)

	fun ByteBuffer.readDI(signed: Boolean): BigInteger {
		var holder = this.get().toInt() and 0xFF
		val negative = if (signed) {
			val negative = holder and 0x80 > 0
			holder = holder and 0x7F
			negative
		} else false
		var length = holder and 0b111
		var position = 0
		var accumulator = (if (signed) {
			position += 4
			(holder shr 3) and 0b1111
		} else {
			position += 5
			holder shr 3
		}).toBigInteger()
		var extended = length == 0b111
		while (length > 0) {
			holder = this.get().toInt() and 0xFF
			length--
			if (length == 0 && extended) {
				accumulator = accumulator or ((holder and 0x7F).toBigInteger() shl position)
				position += 7
				if (holder and 0x80 > 0) {
					holder = this.get().toInt() and 0xFF
					accumulator = accumulator or ((holder shr 3).toBigInteger() shl position)
					position += 5
					length = holder and 0b111
					extended = length == 0b111
				}
			} else {
				accumulator = accumulator or (holder.toBigInteger() shl position)
				position += 8
			}
		}
		return if (negative) -accumulator else accumulator
	}

	private val low4 = BigInteger.valueOf(0b1111)
	private val low5 = BigInteger.valueOf(0b11111)
	fun ByteBuffer.putDI(signed: Boolean, v: BigInteger) {
		var write = 0
		var v = v
		if (v < BigInteger.ZERO) {
			if (!signed) throw ArithmeticException("BigInteger must not be negative for UDI")
			else {
				write = 0x80
				v = -v
			}
		}
		if (signed) {
			write = write or ((v and low4).toInt() shl 3)
			v = v shr 4
		} else {
			write = (v and low5).toInt() shl 3
			v = v shr 5
		}
		val data = ByteArray(7)
		var zeroState: Boolean = v == BigInteger.ZERO
		while (true) {
			for (i in 0..6) {
				if (zeroState) {
					write = write or i
					break
				}
				if (i == 6) {
					var preset = v.toInt() and 0x7F
					v = v shr 7
					zeroState = v == BigInteger.ZERO
					if (!zeroState) preset = preset or 0x80
					data[i] = preset.toByte()
					write = write or 7
					break
				} else {
					data[i] = (v.toInt() and 0xFF).toByte()
					v = v shr 8
				}
				zeroState = v == BigInteger.ZERO
			}
			put(write.toByte())
			put(data, 0, write and 0b111)
			if (zeroState) break
			write = (v and low5).toInt() shl 3
			v = v shr 5
		}
	}

	init {
		buffer.clear()
		buffer.putDI(false, BigInteger.TWO.pow(999))
		buffer.flip()
		println(buffer.limit())
		println(buffer.readDI(false))
		TODO("!")
//		imageChannel.read(buffer)
//		buffer.flip()
//		println(buffer)
	}

	override fun provider(): FileSystemProvider = this.provider
	override fun close() = imageChannel.close()
	override fun isOpen(): Boolean = imageChannel.isOpen
	override fun isReadOnly(): Boolean = false
	override fun getSeparator(): String = "/"

	override fun getRootDirectories(): Iterable<Path> {
		TODO("Not yet implemented")
	}

	override fun getFileStores(): Iterable<FileStore> {
		TODO("Not yet implemented")
	}

	override fun supportedFileAttributeViews(): Set<String> {
		TODO("Not yet implemented")
	}

	override fun getPath(first: String, vararg more: String): Path = BreadExpertsGroupFileSystemPath(this)

	override fun getPathMatcher(syntaxAndPattern: String): PathMatcher {
		TODO("Not yet implemented")
	}

	override fun getUserPrincipalLookupService(): UserPrincipalLookupService {
		TODO("Not yet implemented")
	}

	override fun newWatchService(): WatchService {
		TODO("Not yet implemented")
	}
}