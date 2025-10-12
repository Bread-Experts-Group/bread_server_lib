package org.bread_experts_group.io

import org.bread_experts_group.Mappable
import org.bread_experts_group.Mappable.Companion.id
import org.bread_experts_group.MappedEnumeration
import kotlin.enums.EnumEntries

abstract class IOLayout<O> {
	companion object {
		val BYTE: PrimitiveIOLayout<Byte> = PrimitiveIOLayout(
			{ r -> r.i8() },
			{ w, i -> TODO("W") }
		)

		val UNSIGNED_BYTE: PrimitiveIOLayout<UByte> = PrimitiveIOLayout(
			{ r ->
				this.name?.let { r.enter(it) }
				val v = r.u8()
				this.name?.let { r.exit() }
				v
			},
			{ w, i -> w.u8(i) }
		)

		val CHAR: PrimitiveIOLayout<Char> = PrimitiveIOLayout(
			{ r -> TODO("INTRANET") },
			{ w, i -> TODO("W") }
		)

		val UNSIGNED_SHORT: PrimitiveEndianIOLayout<UShort> = PrimitiveEndianIOLayout(
			{ r ->
				this.name?.let { r.enter(it) }
				val v = r.u16()
				this.name?.let { r.exit() }
				v
			},
			{ w, i -> w.u16(i) }
		)

		val UNSIGNED_INT: PrimitiveEndianIOLayout<UInt> = PrimitiveEndianIOLayout(
			{ r ->
				this.name?.let { r.enter(it) }
				val v = r.u32()
				this.name?.let { r.exit() }
				v
			},
			{ w, i -> w.u32(i) }
		)

		inline fun <reified E, T> enum(
			enum: EnumEntries<E>,
			typeLayout: IOLayout<T>
		): PrimitiveIOLayout<MappedEnumeration<T, E>> where E : Enum<E>, E : Mappable<E, T> = PrimitiveIOLayout(
			{ r -> enum.id(typeLayout.read(r)) },
			{ w, i -> TODO("W") }
		)
	}

	var considerInIO: Boolean = true
	var passedUpwards: Boolean = false
	var name: String? = null

	abstract fun read(from: BaseReadingIO): O
	abstract fun write(to: BaseWritingIO, of: O)
	abstract fun padding(): IOLayout<O>
	abstract fun passedUpwards(): IOLayout<O>
	abstract fun withName(name: String): IOLayout<O>
	abstract fun nullable(): IOLayout<O?>
}