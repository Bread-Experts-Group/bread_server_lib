package org.bread_experts_group.ffi.posix.linux.x64

const val O_RDONLY = 0
const val O_WRONLY = 1
const val O_RDWR = 2

const val O_CREAT = 64
const val O_TRUNC = 512

const val SEEK_SET = 0
const val SEEK_CUR = 1
const val SEEK_END = 2

const val PF_INET = 2
const val PF_INET6 = 10

const val AF_INET = PF_INET
const val AF_INET6 = PF_INET6

const val SOCK_STREAM = 1
const val SOCK_DGRAM = 2
const val SOCK_RAW = 3
const val SOCK_RDM = 4
const val SOCK_SEQPACKET = 5
const val SOCK_DCCP = 6
const val SOCK_PACKET = 10

const val SOCK_CLOEXEC = 7502200
const val SOCK_NONBLOCK = 7640

const val IPPROTO_TCP = 6
const val IPPROTO_UDP = 17