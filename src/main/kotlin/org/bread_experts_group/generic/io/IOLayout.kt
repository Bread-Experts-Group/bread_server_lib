package org.bread_experts_group.generic.io

import org.bread_experts_group.generic.Mappable
import org.bread_experts_group.generic.Mappable.Companion.id
import org.bread_experts_group.generic.MappedEnumeration
import kotlin.enums.EnumEntries

abstract class IOLayout<O> {
	companion object {
		val BYTE: org.bread_experts_group.generic.io.PrimitiveIOLayout<Byte> =
			_root_ide_package_.org.bread_experts_group.generic.io.PrimitiveIOLayout(
				{ r -> r.i8() },
				{ w, i -> TODO("W") }
			)

		val UNSIGNED_BYTE: org.bread_experts_group.generic.io.PrimitiveIOLayout<UByte> =
			_root_ide_package_.org.bread_experts_group.generic.io.PrimitiveIOLayout(
				{ r ->
					this.name?.let { r.enter(it) }
					val v = r.u8()
					this.name?.let { r.exit() }
					v
				},
				{ w, i -> w.u8(i) }
			)

		val CHAR: org.bread_experts_group.generic.io.PrimitiveIOLayout<Char> =
			_root_ide_package_.org.bread_experts_group.generic.io.PrimitiveIOLayout(
				{ r -> TODO("INTRANET") },
				{ w, i -> TODO("W") }
			)

		val UNSIGNED_SHORT: org.bread_experts_group.generic.io.PrimitiveEndianIOLayout<UShort> =
			_root_ide_package_.org.bread_experts_group.generic.io.PrimitiveEndianIOLayout(
				{ r ->
					this.name?.let { r.enter(it) }
					val v = r.u16()
					this.name?.let { r.exit() }
					v
				},
				{ w, i -> w.u16(i) }
			)

		val UNSIGNED_INT: org.bread_experts_group.generic.io.PrimitiveEndianIOLayout<UInt> =
			_root_ide_package_.org.bread_experts_group.generic.io.PrimitiveEndianIOLayout(
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
		): org.bread_experts_group.generic.io.PrimitiveIOLayout<MappedEnumeration<T, E>> where E : Enum<E>, E : Mappable<E, T> =
			_root_ide_package_.org.bread_experts_group.generic.io.PrimitiveIOLayout(
				{ r -> enum.id(typeLayout.read(r)) },
				{ w, i -> TODO("W") }
			)
	}

	var considerInIO: Boolean = true
	var passedUpwards: Boolean = false
	var name: String? = null

	abstract fun read(from: org.bread_experts_group.generic.io.BaseReadingIO): O
	abstract fun write(to: org.bread_experts_group.generic.io.BaseWritingIO, of: O)
	abstract fun padding(): IOLayout<O>
	abstract fun passedUpwards(): IOLayout<O>
	abstract fun withName(name: String): IOLayout<O>
	abstract fun nullable(): IOLayout<O?>
}