package org.bread_experts_group.project_incubator.maven2.data

interface LinkSpeed {
	interface Rx : LinkSpeed {
		val linkSpeedRx: Long
	}

	interface Tx : LinkSpeed {
		val linkSpeedTx: Long
	}
}