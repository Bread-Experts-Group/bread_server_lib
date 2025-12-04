package org.bread_experts_group.api.system.socket.ipv4

import org.bread_experts_group.api.system.socket.BSLSocket
import org.bread_experts_group.api.system.socket.ipv4.accept.IPv4AcceptDataIdentifier

abstract class IPv4Socket : BSLSocket<InternetProtocolV4AddressData>(), IPv4AcceptDataIdentifier