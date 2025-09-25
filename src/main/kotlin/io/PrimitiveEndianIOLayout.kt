package org.bread_experts_group.io

class PrimitiveEndianIOLayout<O>(
	readImpl: PrimitiveIOLayout<O>.(BaseReadingIO) -> O,
	writeImpl: PrimitiveIOLayout<O>.(BaseWritingIO, O) -> Unit
) : PrimitiveIOLayout<O>(readImpl, writeImpl) {
	var order: IOEndian = IOEndian.NATIVE
	fun order(o: IOEndian): PrimitiveEndianIOLayout<O> {
		val newLayout = PrimitiveEndianIOLayout(
			this.readImpl,
			this.writeImpl
		)
		newLayout.order = o
		return newLayout
	}

	override fun read(from: BaseReadingIO): O {
		val save = from.order
		from.order = this.order
		val read = this.readImpl(from)
		from.order = save
		return read
	}

	override fun write(to: BaseWritingIO, of: O) {
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

	override fun nullable(): IOLayout<O?> {
		TODO("Not yet implemented")
	}
}