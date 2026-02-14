package org.bread_experts_group.project_incubator.sim2

import org.bread_experts_group.generic.io.reader.DirectDataProvisioner
import java.nio.ByteOrder

class MemoryBus<T : Comparable<T>>(val minus: (T, T) -> T) : DirectDataProvisioner<T> {
	val extents = sortedMapOf<T, DirectDataProvisioner<T>>(Comparator { a, b -> b.compareTo(a) })
	fun getProvisioner(at: T): Pair<T, DirectDataProvisioner<T>> {
		val position = extents.keys.first { at >= it }
		return position to extents[position]!!
	}

	override fun writeS8(at: T, b: Byte) {
		val (position, provisioner) = getProvisioner(at)
		provisioner.writeS8(minus(at, position), b)
	}

	override fun writeS16(at: T, s: Short) {
		val (position, provisioner) = getProvisioner(at)
		provisioner.writeS16(minus(at, position), s)
	}

	override fun writeS32(at: T, i: Int) {
		val (position, provisioner) = getProvisioner(at)
		provisioner.writeS32(minus(at, position), i)
	}

	override fun writeS64(at: T, l: Long) {
		val (position, provisioner) = getProvisioner(at)
		provisioner.writeS64(minus(at, position), l)
	}

	override fun write(at: T, b: ByteArray, offset: Int, length: Int) {
		val (position, provisioner) = getProvisioner(at)
		provisioner.write(minus(at, position), b, offset, length)
	}

	override fun fill(at: T, n: T, v: Byte) {
		val (position, provisioner) = getProvisioner(at)
		provisioner.fill(minus(at, position), n, v)
	}

	override fun readS8(at: T): Byte {
		val (position, provisioner) = getProvisioner(at)
		return provisioner.readS8(minus(at, position))
	}

	override fun readS16(at: T): Short {
		val (position, provisioner) = getProvisioner(at)
		return provisioner.readS16(minus(at, position))
	}

	override fun readS32(at: T): Int {
		val (position, provisioner) = getProvisioner(at)
		return provisioner.readS32(minus(at, position))
	}

	override fun readS64(at: T): Long {
		val (position, provisioner) = getProvisioner(at)
		return provisioner.readS64(minus(at, position))
	}

	override fun readN(at: T, n: Int): ByteArray {
		val (position, provisioner) = getProvisioner(at)
		return provisioner.readN(minus(at, position), n)
	}

	override fun flush() {}
	override var order: ByteOrder
		get() = TODO("Not yet implemented")
		set(_) {}
}