package org.bread_experts_group.io

open class PrimitiveIOLayout<O>(
	protected val readImpl: (BaseReadingIO) -> O,
	protected val writeImpl: (BaseWritingIO, O) -> Unit
) : IOLayout<O>() {
	override fun read(from: BaseReadingIO): O = readImpl(from)
	override fun write(to: BaseWritingIO, of: O) = writeImpl(to, of)
	fun sequence(n: Int): SequencedIOLayout<O> = SequencedIOLayout(n, this)
	override fun padding(): PrimitiveIOLayout<O> {
		val newLayout = PrimitiveIOLayout<O>(this.readImpl, this.writeImpl)
		newLayout.considerInIO = false
		return newLayout
	}

	override fun passedUpwards(): IOLayout<O> {
		val newLayout = PrimitiveIOLayout<O>(this.readImpl, this.writeImpl)
		newLayout.passedUpwards = true
		return newLayout
	}
}