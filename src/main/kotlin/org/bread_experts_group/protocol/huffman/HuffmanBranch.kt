package org.bread_experts_group.protocol.huffman

data class HuffmanBranch<T>(
	val zero: HuffmanNode<T>,
	val one: HuffmanNode<T>
) : HuffmanNode<T>