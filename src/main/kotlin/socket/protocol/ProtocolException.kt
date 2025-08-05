package org.bread_experts_group.socket.protocol

/**
 * An error has occurred while encoding, constructing, or decoding an internet protocol, such as [InternetProtocolV4].
 * @author Miko Elbrecht
 * @since 4.0.0
 */
abstract class ProtocolException(message: String) : Exception(message)