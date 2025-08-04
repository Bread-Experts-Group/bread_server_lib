package org.bread_experts_group.socket.windows

import org.bread_experts_group.socket.OperatingSystemException

class WSANoBufferSpaceAvailableException : OperatingSystemException("Insufficient buffer space.")