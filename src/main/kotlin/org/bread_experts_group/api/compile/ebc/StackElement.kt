package org.bread_experts_group.api.compile.ebc

sealed interface StackElement {
	enum class Primitive : StackElement, LocalVariableElement {
		TYPE_QUANTIFIED,
		REFERENCE,
		N64,
		LONG,
		DOUBLE,
		N32,
		INT,
		FLOAT
	}

	data class VariableReference(val ref: LocalVariableElement.Reference) : StackElement
}