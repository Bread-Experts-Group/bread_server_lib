package org.bread_experts_group.project_incubator.sim3a.aio

import java.nio.ByteBuffer

fun ByteArray.toArrayIO(): ArrayIO.ReadSeekable<Int> = ByteBuffer.wrap(this).toArrayIO()