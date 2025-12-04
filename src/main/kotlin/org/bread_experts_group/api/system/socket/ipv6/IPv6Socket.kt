package org.bread_experts_group.api.system.socket.ipv6

import org.bread_experts_group.api.system.socket.BSLSocket
import org.bread_experts_group.api.system.socket.ipv6.accept.IPv6AcceptDataIdentifier

abstract class IPv6Socket : BSLSocket<InternetProtocolV6AddressData>(), IPv6AcceptDataIdentifier