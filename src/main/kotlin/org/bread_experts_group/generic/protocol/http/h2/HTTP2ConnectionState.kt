package org.bread_experts_group.generic.protocol.http.h2

import java.util.concurrent.Semaphore
import java.util.concurrent.atomic.AtomicInteger

data class HTTP2ConnectionState(
	var flowControl: AtomicInteger
) {
	constructor(flowControl: Int) : this(AtomicInteger(flowControl))

	enum class State {
		CLOSED_REMOTE,
		CLOSED_LOCAL,
		OPEN
	}

	val flowControlSignal = Semaphore(1)

	var state: State = State.OPEN
	var padSkip = 0

	val dataSignal = Semaphore(0)
	var nextDataLength: Int = 0
}