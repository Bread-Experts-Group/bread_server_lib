package org.bread_experts_group.io

import org.bread_experts_group.coder.Mappable
import org.bread_experts_group.coder.Mappable.Companion.id
import org.bread_experts_group.coder.MappedEnumeration
import kotlin.enums.EnumEntries

abstract class IOLayout<O> {
	companion object {
		val BYTE: PrimitiveIOLayout<Byte> = PrimitiveIOLayout(
			{ r -> r.i8() },
			{ w, i -> TODO("W") }
		)

		val UNSIGNED_BYTE: PrimitiveIOLayout<UByte> = PrimitiveIOLayout(
			{ r -> r.u8() },
			{ w, i -> TODO("W") }
		)

		val CHAR: PrimitiveIOLayout<Char> = PrimitiveIOLayout(
			{ r -> TODO("INTRANET") },
			{ w, i -> TODO("W") }
		)

		val UNSIGNED_SHORT: PrimitiveEndianIOLayout<UShort> = PrimitiveEndianIOLayout(
			{ r -> r.u16() },
			{ w, i -> TODO("W") }
		)

		val UNSIGNED_INT: PrimitiveEndianIOLayout<UInt> = PrimitiveEndianIOLayout(
			{ r -> r.u32() },
			{ w, i -> TODO("W") }
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

	abstract fun read(from: BaseReadingIO): O
	abstract fun write(to: BaseWritingIO, of: O)
	abstract fun padding(): IOLayout<O>
	abstract fun passedUpwards(): IOLayout<O>
}