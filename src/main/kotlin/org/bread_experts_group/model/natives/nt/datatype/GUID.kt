@file:Suppress("PropertyName")

package org.bread_experts_group.model.natives.nt.datatype

import org.bread_experts_group.model.natives.ArraySize
import org.bread_experts_group.model.natives.Datatype.Companion.invoke
import org.bread_experts_group.model.natives.NativeArray
import org.bread_experts_group.model.natives.Order
import org.bread_experts_group.model.natives.Structure
import org.bread_experts_group.model.natives.c.char_t
import org.bread_experts_group.model.natives.c.long_t
import org.bread_experts_group.model.natives.c.short_t
import java.lang.foreign.Linker
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.uuid.Uuid

abstract class GUID : Structure<GUID> {
	@Order(0)
	abstract var Data1: long_t

	@Order(1)
	abstract var Data2: short_t

	@Order(2)
	abstract var Data3: short_t

	@Order(3)
	abstract var Data4: @ArraySize(8) NativeArray<char_t>

	@OptIn(ExperimentalUnsignedTypes::class)
	override fun toString(): String = "{${Data1.toInt().toUInt().toHexString()}-" +
			"${Data2.toShort().toUShort().toHexString()}-${Data3.toShort().toUShort().toHexString()}-" +
			"${Data4.map { it.toByte().toUByte() }.toUByteArray().toHexString()}}"

	fun toUuid(): Uuid {
		val buffer = ByteBuffer.allocate(16)
		buffer.putInt(Data1.toInt())
		buffer.putShort(Data2.toShort())
		buffer.putShort(Data3.toShort())
		buffer.put(Data4.getSegment().asByteBuffer())
		return Uuid.fromByteArray(buffer.array())
	}

	companion object {
		fun fromUuid(uuid: Uuid, linker: Linker): GUID {
			val struct = Structure.getStructure<GUID>(linker)()
			val dst = struct.getSegment().asByteBuffer()
			dst.order(ByteOrder.LITTLE_ENDIAN)
			val src = ByteBuffer.wrap(uuid.toByteArray())
			dst.putInt(src.getInt())
			dst.putShort(src.getShort())
			dst.putShort(src.getShort())
			dst.put(src)
			return struct
		}
	}
}