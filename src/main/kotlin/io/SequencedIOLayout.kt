package org.bread_experts_group.io

data class SequencedIOLayout<O>(
	val n: Int,
	val layout: IOLayout<O>
) : IOLayout<Array<O>>() {
	override fun read(from: BaseReadingIO): Array<O> {
		val array = arrayOfNulls<Any>(n)
		repeat(n) { array[it] = layout.read(from) }
		@Suppress("UNCHECKED_CAST")
		return array as Array<O>
	}

	override fun write(to: BaseWritingIO, of: Array<O>) {
		TODO("Not yet implemented")
	}

	override fun padding(): SequencedIOLayout<O> {
		val layout = this.copy()
		layout.considerInIO = false
		return layout
	}

	override fun passedUpwards(): SequencedIOLayout<O> {
		val layout = this.copy()
		layout.passedUpwards = false
		return layout
	}

	companion object {
		fun SequencedIOLayout<Char>.isoLatin1(): PrimitiveIOLayout<String> = PrimitiveIOLayout(
			{ r -> r.get(this.n).toString(Charsets.ISO_8859_1) },
			{ w, i -> TODO("GAMMA") }
		)

		fun SequencedIOLayout<Byte>.array(): PrimitiveIOLayout<ByteArray> = PrimitiveIOLayout(
			{ r -> r.get(this.n) },
			{ w, i -> TODO("GAMMA") }
		)
	}
}