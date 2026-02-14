package org.bread_experts_group.generic.io

class PrimitiveEndianIOLayout<O>(
	readImpl: org.bread_experts_group.generic.io.PrimitiveIOLayout<O>.(org.bread_experts_group.generic.io.BaseReadingIO) -> O,
	writeImpl: org.bread_experts_group.generic.io.PrimitiveIOLayout<O>.(org.bread_experts_group.generic.io.BaseWritingIO, O) -> Unit
) : org.bread_experts_group.generic.io.PrimitiveIOLayout<O>(readImpl, writeImpl) {
	var order: org.bread_experts_group.generic.io.IOEndian =
		_root_ide_package_.org.bread_experts_group.generic.io.IOEndian.NATIVE

	fun order(o: org.bread_experts_group.generic.io.IOEndian): PrimitiveEndianIOLayout<O> {
		val newLayout = PrimitiveEndianIOLayout(
			this.readImpl,
			this.writeImpl
		)
		newLayout.order = o
		return newLayout
	}

	override fun read(from: org.bread_experts_group.generic.io.BaseReadingIO): O {
		val save = from.order
		from.order = this.order
		val read = this.readImpl(from)
		from.order = save
		return read
	}

	override fun write(to: org.bread_experts_group.generic.io.BaseWritingIO, of: O) {
		writeImpl(to, of)
	}

	override fun padding(): PrimitiveEndianIOLayout<O> = TODO("LAMBDA")
	override fun withName(name: String): PrimitiveEndianIOLayout<O> {
		val newLayout = PrimitiveEndianIOLayout(
			this.readImpl,
			this.writeImpl
		)
		newLayout.name = name
		return newLayout
	}

	override fun nullable(): org.bread_experts_group.generic.io.IOLayout<O?> {
		TODO("Not yet implemented")
	}
}