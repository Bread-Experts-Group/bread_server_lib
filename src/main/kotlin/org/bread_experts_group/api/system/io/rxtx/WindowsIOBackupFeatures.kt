package org.bread_experts_group.api.system.io.rxtx

import org.bread_experts_group.api.system.io.receive.IOReceiveFeatureIdentifier
import org.bread_experts_group.api.system.io.send.IOSendFeatureIdentifier

enum class WindowsIOBackupFeatures : IOReceiveFeatureIdentifier, IOSendFeatureIdentifier {
	BACKUP,
	ACL
}