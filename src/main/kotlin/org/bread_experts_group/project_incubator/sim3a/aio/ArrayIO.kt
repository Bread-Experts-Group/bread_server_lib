package org.bread_experts_group.project_incubator.sim3a.aio

import java.nio.ByteOrder

sealed interface ArrayIO {
	var order: ByteOrder

	interface ReadExact<T> : ArrayIO {
		fun readUByte(at: T): UByte = readByte(at).toUByte()
		fun readUShort(at: T): UShort = readShort(at).toUShort()
		fun readUInt(at: T): UInt = readInt(at).toUInt()
		fun readULong(at: T): ULong = readLong(at).toULong()

		fun readByte(at: T): Byte
		fun readShort(at: T): Short
		fun readInt(at: T): Int
		fun readLong(at: T): Long

		fun readArray(at: T, n: Int): ByteArray = ByteArray(n).also { readInto(at, it) }
		fun readInto(at: T, array: ByteArray, offset: Int = 0, size: Int = array.size - offset)
	}

	interface WriteExact<T> : ArrayIO {
		fun writeUByte(at: T, v: UByte) = writeByte(at, v.toByte())
		fun writeUShort(at: T, v: UShort) = writeShort(at, v.toShort())
		fun writeUInt(at: T, v: UInt) = writeInt(at, v.toInt())
		fun writeULong(at: T, v: ULong) = writeLong(at, v.toLong())

		fun writeByte(at: T, v: Byte)
		fun writeShort(at: T, v: Short)
		fun writeInt(at: T, v: Int)
		fun writeLong(at: T, v: Long)
	}

	interface ReadSequential<T> : ArrayIO {
		val position: T

		fun readUByte(): UByte = readByte().toUByte()
		fun readUShort(): UShort = readShort().toUShort()
		fun readUInt(): UInt = readInt().toUInt()
		fun readULong(): ULong = readLong().toULong()

		fun readByte(): Byte
		fun readShort(): Short
		fun readInt(): Int
		fun readLong(): Long

		fun skip(n: T)
		fun readArray(n: Int): ByteArray = ByteArray(n).also { readInto(it) }
		fun readInto(array: ByteArray, offset: Int = 0, size: Int = array.size - offset) {
			for (i in offset until offset + size) array[i] = readByte()
		}
	}

	interface ReadSeekable<T> : ReadSequential<T>, ReadExact<T> {
		override var position: T
	}
}