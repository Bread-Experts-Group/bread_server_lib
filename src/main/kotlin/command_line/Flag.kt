package org.bread_experts_group.command_line

open class Flag<T>(
	flagName: String,
	val flagDescription: String,
	val repeatable: Boolean = false,
	val required: Int = 0,
	val default: T? = null,
	val conv: ((String) -> T) = {
		@Suppress("UNCHECKED_CAST")
		it as T
	}
) {
	val flagName: String = flagName.lowercase().replace('-', '_')

	init {
		if (required > 1 && !repeatable)
			throw ArgumentConstructionError("[$flagName] requires $required specifications, but isn't repeatable")
	}
}