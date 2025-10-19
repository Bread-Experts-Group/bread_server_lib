package org.bread_experts_group.api.compile.ebc

data class EBCCompilationOutput(
	val code: ByteArray,
	val initializedData: ByteArray
) {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as EBCCompilationOutput

		if (!code.contentEquals(other.code)) return false
		if (!initializedData.contentEquals(other.initializedData)) return false

		return true
	}

	override fun hashCode(): Int {
		var result = code.contentHashCode()
		result = 31 * result + initializedData.contentHashCode()
		return result
	}

}
