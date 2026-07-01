package org.bread_experts_group.project_incubator.sim3a

@Suppress("ClassName")
sealed interface IOMapped {
	sealed interface Read<T> : IOMapped {
		fun read(): T

		interface `8` : Read<Byte>
		interface `16` : Read<Short>
		interface `32` : Read<Int>
	}

	sealed interface Write<T> : IOMapped {
		fun write(value: T)

		interface `8` : Write<Byte>
		interface `16` : Write<Short>
		interface `32` : Write<Int>
	}
}