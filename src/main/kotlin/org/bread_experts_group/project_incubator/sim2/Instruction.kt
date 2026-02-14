package org.bread_experts_group.project_incubator.sim2

interface Instruction {
	fun execute(processor80386: Processor80386)
}