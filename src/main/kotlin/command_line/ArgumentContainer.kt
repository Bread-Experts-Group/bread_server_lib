package org.bread_experts_group.command_line

class ArgumentContainer(val of: Map<String, Any>) {
	inline fun <reified T> get(name: String): T? = of[name] as T?
	inline fun <reified T> getRequired(name: String): T = of[name] as T
	inline fun <reified T> get(flag: Flag<T>) = this.get<T>(flag.flagName)
	inline fun <reified T> getRequired(flag: Flag<T>) = this.get<T>(flag)

	inline fun <reified T> gets(name: String): List<T>? {
		@Suppress("UNCHECKED_CAST") val list = of[name] as List<T>?
		name as T?
		return list
	}

	inline fun <reified T> getsRequired(name: String): List<T> {
		@Suppress("UNCHECKED_CAST") val list = of[name] as List<T>
		name as T
		return list
	}

	inline fun <reified T> gets(flag: Flag<T>) = this.gets<T>(flag.flagName)
	inline fun <reified T> getsRequired(flag: Flag<T>) = this.gets<T>(flag)
}