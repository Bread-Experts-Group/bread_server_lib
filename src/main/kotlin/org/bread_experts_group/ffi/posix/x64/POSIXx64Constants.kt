package org.bread_experts_group.ffi.posix.x64

const val S_IRWXU = 448 // rwx --- ---
const val S_IRUSR = 256 // r-- --- ---
const val S_IWUSR = 128 // -w- --- ---
const val S_IXUSR = 64  // --x --- ---

const val S_IRWXG = 56 // --- rwx ---
const val S_IRGRP = 32 // --- r-- ---
const val S_IWGRP = 16 // --- -w- ---
const val S_IXGRP = 8  // --- --x ---

const val S_IRWXO = 7 // --- --- rwx
const val S_IROTH = 4 // --- --- r--
const val S_IWOTH = 2 // --- --- -w-
const val S_IXOTH = 1 // --- --- --x

const val S_ISUID = 2048 // exec: set user id
const val S_ISGID = 1024 // exec: set group id