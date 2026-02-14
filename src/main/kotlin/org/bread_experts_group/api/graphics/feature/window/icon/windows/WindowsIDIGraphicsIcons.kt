package org.bread_experts_group.api.graphics.feature.window.icon.windows

import org.bread_experts_group.api.graphics.feature.window.icon.GraphicsIcon

class WindowsIDIGraphicsIcons(val id: Int) : GraphicsIcon {
	companion object {
		val APPLICATION = WindowsIDIGraphicsIcons(32512)
		val HAND = WindowsIDIGraphicsIcons(32513)
		val ERROR = HAND
		val QUESTION = WindowsIDIGraphicsIcons(32514)
		val EXCLAMATION = WindowsIDIGraphicsIcons(32515)
		val WARNING = EXCLAMATION
		val ASTERISK = WindowsIDIGraphicsIcons(32516)
		val INFORMATION = ASTERISK
		val WINLOGO = WindowsIDIGraphicsIcons(32517)
		val SHIELD = WindowsIDIGraphicsIcons(32518)
	}
}