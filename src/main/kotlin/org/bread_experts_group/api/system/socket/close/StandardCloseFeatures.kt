package org.bread_experts_group.api.system.socket.close

enum class StandardCloseFeatures : SocketCloseFeatureIdentifier {
	STOP_RX,
	STOP_TX,
	RELEASE
}