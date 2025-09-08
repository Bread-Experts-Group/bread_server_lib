package org.bread_experts_group.api

interface PreInitializableClosable : AutoCloseable {
	fun open()
}