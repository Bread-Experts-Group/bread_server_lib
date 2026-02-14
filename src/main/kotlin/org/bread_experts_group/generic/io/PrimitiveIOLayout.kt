package org.bread_experts_group.generic.io

open class PrimitiveIOLayout<O>(
	protected val readImpl: PrimitiveIOLayout<O>.(org.bread_experts_group.generic.io.BaseReadingIO) -> O,
	protected val writeImpl: PrimitiveIOLayout<O>.(org.bread_experts_group.generic.io.BaseWritingIO, O) -> Unit
) : org.bread_experts_group.generic.io.IOLayout<O>() {
	override fun read(from: org.bread_experts_group.generic.io.BaseReadingIO): O = readImpl(from)
	override fun write(to: org.bread_experts_group.generic.io.BaseWritingIO, of: O) = writeImpl(to, of)
	fun sequence(n: Int): org.bread_experts_group.generic.io.SequencedIOLayout<O> =
		_root_ide_package_.org.bread_experts_group.generic.io.SequencedIOLayout(n, this)

	override fun padding(): PrimitiveIOLayout<O> {
		val layout = PrimitiveIOLayout<O>(this.readImpl, this.writeImpl)
		layout.considerInIO = false
		layout.name = this.name
		layout.passedUpwards = this.passedUpwards
		return layout
	}

	override fun passedUpwards(): PrimitiveIOLayout<O> {
		val layout = PrimitiveIOLayout<O>(this.readImpl, this.writeImpl)
		layout.passedUpwards = true
		layout.name = this.name
		layout.considerInIO = this.considerInIO
		return layout
	}

	override fun withName(name: String): PrimitiveIOLayout<O> {
		val layout = PrimitiveIOLayout<O>(this.readImpl, this.writeImpl)
		layout.name = name
		layout.considerInIO = this.considerInIO
		layout.passedUpwards = this.passedUpwards
		return layout
	}

	@Suppress("RemoveExplicitTypeArguments")
	override fun nullable(): org.bread_experts_group.generic.io.IOLayout<O?> {
		val mainImpl = this.readImpl
		val layout = PrimitiveIOLayout<O?>(
			{ r ->
				try {
					mainImpl(r)
				} catch (_: Exception) {
					null
				}
			},
			{ w, v -> this.writeImpl(w, v) }
		)
		layout.name = this.name
		layout.considerInIO = this.considerInIO
		layout.passedUpwards = this.passedUpwards
		return layout
	}
}