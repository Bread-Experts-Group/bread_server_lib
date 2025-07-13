package org.bread_experts_group.coder.format.decode

interface Decoder<T> {
	fun next(): T
}