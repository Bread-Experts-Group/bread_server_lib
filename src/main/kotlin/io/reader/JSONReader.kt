package org.bread_experts_group.io.reader

import org.bread_experts_group.coder.fixed.json.JSONElement
import org.bread_experts_group.coder.fixed.json.JSONElement.Companion.json
import org.bread_experts_group.coder.fixed.json.JSONNumber
import org.bread_experts_group.coder.fixed.json.JSONString
import org.bread_experts_group.io.BaseReadingIO
import org.bread_experts_group.io.IOEndian
import java.io.InputStream
import java.util.*
import kotlin.reflect.jvm.jvmName

class JSONReader(input: InputStream) : BaseReadingIO {
	override var order: IOEndian = IOEndian.BOTH_LE_BE
	override var pass: Array<Any?>? = null

	val raw = json(input)
	private val position: ArrayDeque<JSONElement> = ArrayDeque<JSONElement>()
	override fun enter(name: Any) {
		val enter = when (name) {
			is String -> (position.lastOrNull() ?: raw).asObject { entries.getValue(name) }
			is Int -> (position.lastOrNull() ?: raw).asArray { entries[name] }
			else -> throw UnsupportedOperationException(name::class.jvmName)
		}
		position.addLast(enter)
	}

	override fun exit() {
		position.removeLast()
	}

	override fun get(n: Int): ByteArray = when (val at = position.last()) {
		is JSONString -> at.value.toByteArray()
		else -> TODO(at!!::class.jvmName)
	}

	override fun i8(): Byte = when (val at = position.last()) {
		else -> TODO(at!!::class.jvmName)
	}

	override fun i16(): Short = when (val at = position.last()) {
		else -> TODO(at!!::class.jvmName)
	}

	override fun i32(): Int = when (val at = position.last()) {
		else -> TODO(at!!::class.jvmName)
	}

	override fun u8(): UByte = when (val at = position.last()) {
		else -> TODO(at!!::class.jvmName)
	}

	override fun u16(): UShort = when (val at = position.last()) {
		else -> TODO(at!!::class.jvmName)
	}

	override fun u32(): UInt = when (val at = position.last()) {
		is JSONNumber -> at.value.toInt().toUInt()
		else -> TODO(at!!::class.jvmName)
	}

	override fun invalidateData() {
		TODO("Not yet implemented")
	}
}