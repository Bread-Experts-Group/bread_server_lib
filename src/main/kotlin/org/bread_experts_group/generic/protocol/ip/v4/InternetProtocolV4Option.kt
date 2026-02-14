package org.bread_experts_group.generic.protocol.ip.v4

import org.bread_experts_group.generic.MappedEnumeration

data class InternetProtocolV4Option(
	val type: MappedEnumeration<UByte, InternetProtocolV4OptionType>,
	val data: ByteArray
) {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as InternetProtocolV4Option

		if (type != other.type) return false
		if (!data.contentEquals(other.data)) return false

		return true
	}

	override fun hashCode(): Int {
		var result = type?.hashCode() ?: 0
		result = 31 * result + data.contentHashCode()
		return result
	}
}