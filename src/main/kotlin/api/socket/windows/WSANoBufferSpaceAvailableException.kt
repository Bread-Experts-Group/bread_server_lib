package org.bread_experts_group.api.socket.windows

import org.bread_experts_group.api.socket.InternalSocketException

class WSANoBufferSpaceAvailableException : InternalSocketException("Insufficient buffer space.")