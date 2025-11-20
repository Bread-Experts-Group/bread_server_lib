Bread Server Library enables the use of system constructs that would otherwise require the use of
OS / architecture-specific native libraries; for example:

- Accessing device notification events (USB, Serial, Hard Drives, ...)
- Accessing system devices (e.g. serial ports)
- Accessing user or system dependent data, such as uptime or log-on time
- Controlling character devices for interactive consoles
- Using system graphics, such as OpenGL / DirectX, and opening windows
- Using system based cryptography or encrypted memory
- Using file features specific to a local system with supported feature reporting
- Hinting potential system optimizations (e.g. opening a file for random-access, sequential, write-through, or others)
- Efficient use of system I/O, such as when iterating over a directory

as well as other advanced features, such as:

- JVM based compiler plugins
    - JVM bytecode → EBC bytecode
- Computer architecture simulation (work in progress)
- Binary format reading / writing (work in progress)

Project Status
-
Bread Server Library is primarily tailored to the Windows operating system (Windows 11 24H2+), as that is what I
personally use. If you find the way a certain feature is implemetned
to be too tailored to Windows, let me know what you suggest would be more
common between it and your target system, or support a Pull
Request. If you would like a faster-paced mode of communication, join
the [Discord server](https://discord.gg/zgCbxuEvJv).