package org.bread_experts_group.api.vfs

import java.nio.file.Path

class FileDataContext(
	val of: Path,
	val offset: ULong,
	val size: UInt
)