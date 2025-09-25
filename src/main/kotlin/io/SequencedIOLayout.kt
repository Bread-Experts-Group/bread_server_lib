package org.bread_experts_group.io

open class SequencedIOLayout<O>(
	val n: Int,
	val layout: IOLayout<O>
) : IOLayout<List<O>>() {
	override fun read(from: BaseReadingIO): List<O> {
		this.name?.let { from.enter(it) }
		@Suppress("UNCHECKED_CAST")
		val list = mutableListOf<O>()
		if (n == -1) {
			try {
				while (true) {
					from.enter(list.size)
					list.add(layout.read(from))
					from.exit()
				}
			} catch (e: ArrayIndexOutOfBoundsException) {
			}
		} else {
			repeat(n) {
				from.enter(it)
				list.add(it, layout.read(from))
				from.exit()
			}
		}
		this.name?.let { from.exit() }
		return list
	}

	override fun write(to: BaseWritingIO, of: List<O>) {
		TODO("Not yet implemented")
	}

	override fun padding(): SequencedIOLayout<O> {
		val layout = SequencedIOLayout(n, layout)
		layout.considerInIO = false
		layout.passedUpwards = this.passedUpwards
		layout.name = this.name
		return layout
	}

	override fun passedUpwards(): SequencedIOLayout<O> {
		val layout = SequencedIOLayout(n, layout)
		layout.passedUpwards = false
		layout.name = this.name
		layout.considerInIO = this.considerInIO
		return layout
	}

	override fun withName(name: String): SequencedIOLayout<O> {
		val layout = SequencedIOLayout(n, layout)
		layout.name = name
		layout.passedUpwards = this.passedUpwards
		layout.considerInIO = this.considerInIO
		return layout
	}

	companion object {
		fun SequencedIOLayout<Char>.isoLatin1(): PrimitiveIOLayout<String> = PrimitiveIOLayout(
			{ r ->
				this.name?.let { r.enter(it) }
				val v = r.get(this@isoLatin1.n).toString(Charsets.ISO_8859_1)
				this.name?.let { r.exit() }
				v
			},
			{ w, i ->
				w.put(i.toByteArray(Charsets.ISO_8859_1))
			}
		)

		val ISO_LATIN_1_UNBOUNDED = CHAR.sequence(-1).isoLatin1()

		fun SequencedIOLayout<Byte>.array(): PrimitiveIOLayout<ByteArray> = PrimitiveIOLayout(
			{ r ->
				this.name?.let { r.enter(it) }
				val v = r.get(this@array.n)
				this.name?.let { r.exit() }
				v
			},
			{ w, i -> TODO("GAMMA") }
		)
	}

	override fun nullable(): IOLayout<List<O>?> {
		@Suppress("UNCHECKED_CAST")
		val layout = object : IOLayout<List<O>?>() {
			override fun read(from: BaseReadingIO): List<O>? = try {
				this@SequencedIOLayout.read(from)
			} catch (_: NoSuchElementException) {
				null
			}

			override fun write(to: BaseWritingIO, of: List<O>?) {
				TODO("Not yet implemented")
			}

			override fun nullable(): IOLayout<List<O>?> {
				TODO("Not yet implemented")
			}

			override fun padding(): IOLayout<List<O>?> {
				TODO("Not yet implemented")
			}

			override fun passedUpwards(): IOLayout<List<O>?> {
				TODO("Not yet implemented")
			}

			override fun withName(name: String): IOLayout<List<O>?> {
				TODO("Not yet implemented")
			}
		}
		layout.name = this.name
		layout.considerInIO = this.considerInIO
		layout.passedUpwards = this.passedUpwards
		@Suppress("UNCHECKED_CAST")
		return layout as IOLayout<List<O>?>
	}
}