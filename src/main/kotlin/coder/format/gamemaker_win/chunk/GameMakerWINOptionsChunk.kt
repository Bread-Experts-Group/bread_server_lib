package org.bread_experts_group.coder.format.gamemaker_win.chunk

data class GameMakerWINOptionsChunk(
	override val offset: Long,
	val unkB: Int,
	val info: Long,
	val scale: Int,
	val windowColor: Int,
	val colorDepth: Int,
	val resolution: Int,
	val frequency: Int,
	val vertexSync: Int,
	val priority: Int,
	val backImageOffset: Int,
	val frontImageOffset: Int,
	val loadImageOffset: Int,
	val loadAlpha: Int,
	val constants: Map<String, String>
) : GameMakerWINChunk("OPTN", offset) {
	override fun toString(): String = super.toString() + "[$unkB, flags: [" +
			GameMakerWINOptions.entries.filter { it.flag and info > 0 }.joinToString(",") +
			"], scale: $scale, window color: " +
			"$windowColor, color depth: $colorDepth, resolution: $resolution, frequency: $frequency, " +
			"vertexSync: $vertexSync, priority: $priority, backImage@$backImageOffset, frontImage@$frontImageOffset, " +
			"loadImage@$loadImageOffset, loadAlpha: $loadAlpha, constants: [" +
			"${constants.entries.joinToString(", ") { "${it.key}: ${it.value}" }}]]"

	enum class GameMakerWINOptions(val flag: Long) {
		FULLSCREEN(0x0000000000000001),
		INTERPOLATE_PIXELS(0x0000000000000002),
		USE_NEW_AUDIO_FORMAT(0x0000000000000004),
		BORDERLESS_WINDOW(0x0000000000000008),
		SHOW_CURSOR(0x0000000000000010),
		RESIZABLE_WINDOW(0x0000000000000020),
		WINDOW_ON_TOP(0x0000000000000040),
		CHANGEABLE_RESOLUTION(0x0000000000000080),
		NO_BUTTONS(0x0000000000000100),
		SCREEN_KEY(0x0000000000000200),
		HELP_KEY(0x0000000000000400),
		QUIT_KEY(0x0000000000000800),
		SAVE_KEY(0x0000000000001000),
		SCREENSHOT_KEY(0x0000000000002000),
		CLOSE_SEC(0x0000000000004000),
		FREEZE(0x0000000000008000),
		SHOW_PROGRESS(0x0000000000010000),
		LOAD_TRANSPARENT(0x0000000000020000),
		SCALE_PROGRESS(0x0000000000040000),
		DISPLAY_ERRORS(0x0000000000080000),
		WRITE_ERRORS(0x0000000000100000),
		ABORT_ERRORS(0x0000000000200000),
		VARIABLE_ERRORS(0x0000000000400000),
		CREATION_EVENT_ORDER(0x0000000000800000),
		USE_FRONT_TOUCH(0x0000000001000000),
		USE_REAR_TOUCH(0x0000000002000000),
		USE_FAST_COLLISION(0x0000000004000000),
		FAST_COLLISION_COMPATIBILITY(0x0000000008000000),
		DISABLE_SANDBOX(0x0000000010000000),
		ENABLE_COPY_ON_WRITE(0x0000000020000000),
		LEGACY_JSON_PARSING(0x0000000040000000),
		LEGACY_NUMBER_CONVERSION(0x0000000800000000),
		LEGACY_OTHER_BEHAVIOR(0x0000000100000000),
		AUDIO_ERROR_BEHAVIOR(0x0000000200000000),
		ALLOW_INSTANCE_CHANGE(0x0000000400000000)
	}
}