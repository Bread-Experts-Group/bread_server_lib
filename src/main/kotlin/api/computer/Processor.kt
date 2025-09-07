package org.bread_experts_group.api.computer

/**
 * A processor for a Bread Mod computer.
 * @since 1.0.0
 * @see Computer
 * @author Miko Elbrecht
 */
interface Processor : SimulationSteppable {
	var computer: Computer
}