Bread Experts Group
WebSocket Protocol Facilitation / Standard Proxy
===
Designed for use in experimental applications, such as SSH over the web.
Not an IANA designated protocol.

SEE ALSO: Secure Socket Tunneling Protocol

Link Overview
===
... initialization ...
> CONNECT <protocol> <hostname> <port>
< CONNECT <protocol> <hostname> <port> <local port>
... message loop ...
> MESSAGE <protocol> <local port> <data>
< MESSAGE <protocol> <local port> <data>
... termination ...
> DISCONNECT <protocol> <local port>
< DISCONNECT <protocol> <local port>

Packet Overview
===
Control Type | Size (1 byte)
CONNECT      = 0x00
MESSAGE      = 0x01
DISCONNECT   = 0x02

Protocol Type                 | Size (1 byte)
TRANSMISSION_CONTROL_PROTOCOL = 0x00
USER_DATAGRAM_PROTOCOL        = 0x01

CONNECT Message
 CONNECT           (1 byte)
 <Protocol>        (1 byte)
 <HostName Length> (4 bytes)
 <HostName>        (Variable)
 <Remote Port>     (2 bytes)
[<Local Port>      (2 bytes)]
                   (Base: 8/10 bytes)

MESSAGE message
 MESSAGE      (1 byte)
 <Protocol>   (1 byte)
 <Local Port> (2 bytes)
 <Data>       (Variable)
              (Base: 4 bytes)

DISCONNECT message
 DISCONNECT   (1 byte)
 <Protocol>   (1 byte)
 <Local Port> (2 bytes)
              (Base: 4 bytes)