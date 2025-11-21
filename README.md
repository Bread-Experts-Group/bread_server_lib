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
personally use. If you find the way a certain feature is implemented
to be too tailored to Windows, let me know what you suggest would be more
common between it and your target system, or support a Pull
Request. If you would like a faster-paced mode of communication, join
the [Discord server](https://discord.gg/zgCbxuEvJv).

Project Contributions
-
You are free to contribute to Bread Server Library as you wish, but we will turn down PRs that make use of
"external libraries." These are:

- Libraries pulled in through Gradle, not limited to:
    - `implementation`
    - `compileOnly`
    - `runtimeOnly`
    - Plugins
    - ...except for Kotlin/Dokka and built-in Gradle plugins like `signing`
- Compiled native libraries
    - Must never be packaged along with Bread Server Library
    - Reasonably expected to be present on some user's system, either apart of the kernel or some very widely used
      project
    - JNI is never allowed
- Packaged JARs
    - Never allowed
- Externally written code
    - You may not copy code from others verbatim, especially if you do not understand what it does internally.
      In the case you're about to add code you didn't write, or lightly derived, make sure you cite a source of where it
      came from.

Additionally, if you are to write a feature that is to be integrated into Bread Server Library's main
repository, use the latest APIs for the system/library it is for. "Compatibility" features must be separate,
unless Miko changes their mind.

Finally, press [here](https://bayachao.com/devil-connection/) for a game you should play.