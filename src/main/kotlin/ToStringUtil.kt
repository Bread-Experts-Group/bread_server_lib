package bread_experts_group

import java.time.format.DateTimeFormatter
import kotlin.collections.forEachIndexed
import kotlin.collections.joinToString
import kotlin.collections.slice
import kotlin.let
import kotlin.ranges.until
import kotlin.reflect.full.memberProperties
import kotlin.text.replace
import kotlin.text.split

var toStringVerbosity = -1
val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd [EEE] HH:mm:ss.SSS xxxx")

fun reflectedToString(obj: Any): String =
	if (toStringVerbosity < 0) {
		var initial = "${obj::class.simpleName}(\n"
		val properties = obj::class.memberProperties
		properties.forEachIndexed { i, property ->
			val value = property.call(obj)
			val valueStr = value?.let {
				if (value is SmartToString) value.toString()
				else "${value::class.simpleName}($value)"
			}?.replace("\n", "\n\t") ?: "null"
			initial += "\t${property.name}=$valueStr${if (i == (properties.size - 1)) "" else ",\n"}"
		}
		"$initial\n)"
	} else if (toStringVerbosity > 0) {
		if (obj is SmartToString) {
			obj.gist()
				.split('\n', ignoreCase = true)
				.let { if (it.size > toStringVerbosity) it.slice(0 until toStringVerbosity) else it }
				.joinToString("\n")
		} else obj.toString()
	} else ""

abstract class SmartToString {
	abstract fun gist(): String
	final override fun toString(): String = reflectedToString(this)
}