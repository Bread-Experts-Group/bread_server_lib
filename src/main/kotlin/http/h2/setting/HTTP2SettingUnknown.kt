package bread_experts_group.http.h2.setting

import java.io.InputStream

class HTTP2SettingUnknown : HTTP2Setting(HTTP2SettingIdentifier.OTHER) {
	companion object {
		fun read(stream: InputStream): HTTP2SettingUnknown {
			stream.skip(4)
			return HTTP2SettingUnknown()
		}
	}
}