package org.bread_experts_group.api.system.socket.resolution

data class InternetProtocolV4AddressData(
	val data: ByteArray
) : ResolutionDataIdentifier {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false
		other as InternetProtocolV4AddressData
		return data.contentEquals(other.data)
	}

	override fun hashCode(): Int = 31 + data.contentHashCode()

	override fun toString(): String =
		"InternetProtocolV4AddressData(data=${data.joinToString(".") { it.toUByte().toString() }})"
}