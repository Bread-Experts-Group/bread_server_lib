package org.bread_experts_group.coder.format.mp3.frame.header

class MP3Bitrate(
	val index: Int,
	val versionID: MPEGAudioVersionID,
	val layerDescription: LayerDescription
) {
	val raw: Int = when (versionID) {
		MPEGAudioVersionID.VERSION_1 -> when (layerDescription) {
			LayerDescription.RESERVED -> -1
			LayerDescription.LAYER_1 -> when (index) {
				0 -> 0
				1 -> 32
				2 -> 64
				3 -> 96
				4 -> 128
				5 -> 160
				6 -> 192
				7 -> 224
				8 -> 256
				9 -> 288
				10 -> 320
				11 -> 352
				12 -> 384
				13 -> 416
				14 -> 448
				15 -> -1
				else -> -1
			}

			LayerDescription.LAYER_2 -> when (index) {
				0 -> 0
				1 -> 32
				2 -> 48
				3 -> 56
				4 -> 64
				5 -> 80
				6 -> 96
				7 -> 112
				8 -> 128
				9 -> 160
				10 -> 192
				11 -> 224
				12 -> 256
				13 -> 320
				14 -> 384
				15 -> -1
				else -> -1
			}

			LayerDescription.LAYER_3 -> when (index) {
				0 -> 0
				1 -> 32
				2 -> 40
				3 -> 48
				4 -> 56
				5 -> 64
				6 -> 80
				7 -> 96
				8 -> 112
				9 -> 128
				10 -> 160
				11 -> 192
				12 -> 224
				13 -> 256
				14 -> 320
				15 -> -1
				else -> -1
			}
		}

		MPEGAudioVersionID.VERSION_2 -> when (layerDescription) {
			LayerDescription.RESERVED -> -1
			LayerDescription.LAYER_1 -> when (index) {
				0 -> 0
				1 -> 32
				2 -> 48
				3 -> 56
				4 -> 64
				5 -> 80
				6 -> 96
				7 -> 112
				8 -> 128
				9 -> 144
				10 -> 160
				11 -> 176
				12 -> 192
				13 -> 224
				14 -> 256
				15 -> -1
				else -> -1
			}

			LayerDescription.LAYER_2, LayerDescription.LAYER_3 -> when (index) {
				0 -> 0
				1 -> 8
				2 -> 16
				3 -> 24
				4 -> 32
				5 -> 40
				6 -> 48
				7 -> 56
				8 -> 64
				9 -> 80
				10 -> 96
				11 -> 112
				12 -> 128
				13 -> 144
				14 -> 160
				15 -> -1
				else -> -1
			}
		}

		MPEGAudioVersionID.VERSION_2_5, MPEGAudioVersionID.RESERVED -> -1
	}

	override fun toString(): String = "$raw kbit/s"
}