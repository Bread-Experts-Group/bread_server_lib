package org.bread_experts_group.io

class PrimitiveEndianIOLayout<O>(
	readImpl: (BaseReadingIO) -> O,
	writeImpl: (BaseWritingIO, O) -> Unit
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

	override fun write(to: BaseWritingIO, of: O) = TODO("LAMBDA")
	override fun padding(): PrimitiveEndianIOLayout<O> = TODO("LAMBDA")
}