package org.bread_experts_group.socket.protocol

/**
 * An error has occurred during the decoding of an internet protocol, such as [InternetProtocolV4].
 * @author Miko Elbrecht
 * @since 4.0.0
 */
class ProtocolDecodingException(message: String) : ProtocolException(message)