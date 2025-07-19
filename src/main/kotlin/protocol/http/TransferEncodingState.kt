package org.bread_experts_group.protocol.http

enum class TransferEncodingState {
	NO_TRANSFER_ENCODING,
	INITIAL_CHUNK,
	REST_OF_DATA
}