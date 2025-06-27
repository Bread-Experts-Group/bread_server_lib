package org.bread_experts_group.coder.format.gamemaker_win

import org.bread_experts_group.coder.format.Parser
import org.bread_experts_group.coder.format.gamemaker_win.chunk.*
import org.bread_experts_group.coder.format.gamemaker_win.structure.*
import org.bread_experts_group.coder.format.riff.RIFFParser
import org.bread_experts_group.stream.*
import java.io.FileInputStream

class GameMakerWINParser(
	from: FileInputStream
) : Parser<String, GameMakerWINChunk, FileInputStream>("GameMaker WIN File Format", from) {
	override fun responsibleStream(of: GameMakerWINChunk): FileInputStream = rawStream

	override fun readBase(): GameMakerWINChunk {
		val name = fqIn.readString(4)
		val size = fqIn.read32().le()
		val element = GameMakerWINChunk(name, rawStream.channel.position())
		element.length = size
		rawStream.channel.position(element.offset + size)
		return element
	}

	override fun refineBase(of: GameMakerWINChunk): GameMakerWINChunk = rawStream.channel.resetPosition(of.offset) {
		val refined = super.refineBase(of)
		refined.length = of.length
		refined
	}

	private fun FileInputStream.readString(at: Long): String = this.channel.resetPosition(at) {
		this@readString.readString(this@readString.read32().le())
	}

	init {
		this.addParser("FORM") { stream, chunk ->
			GameMakerWINContainerChunk(
				chunk.tag,
				chunk.offset,
				GameMakerWINParser(stream).readAllParsed()
			)
		}
		this.addParser("STRG") { stream, chunk ->
			val strings = stream.read32().le()
			GameMakerWINStringsChunk(chunk.offset, List(strings) {
				stream.readString(stream.read32().le().toLong())
			})
		}
		this.addParser("TAGS") { stream, chunk ->
			val version = stream.read32().le()
			if (version != 1) throw UnsupportedOperationException("TAGS version [$version]")
			GameMakerWINTagsChunk(chunk.offset, List(stream.read32().le()) {
				stream.readString(stream.read32().le() - 4L)
			}, List(stream.read32().le()) {
				stream.channel.resetPosition(stream.read32().le().toLong()) {
					stream.read32().le() to List(stream.read32().le()) {
						stream.readString(stream.read32().le() - 4L)
					}
				}
			}.toMap())
		}
		this.addParser("TXTR") { stream, chunk ->
			val textures = stream.read32().le()
			GameMakerWINTexturesChunk(chunk.offset, List(textures) {
				stream.channel.resetPosition(stream.read32().le().toLong()) {
					GameMakerWINTexture(
						stream.channel.position(),
						stream.read32().le().toLong(),
						stream.read32().le().toLong(),
						stream.read32().le().toLong(),
						stream.read32().le(),
						stream.read32().le(),
						stream.read32().le(),
						stream.read32().le().toLong()
					)
				}
			})
		}
		this.addParser("EXTN") { stream, chunk ->
			val extensions = stream.read32().le()
			GameMakerWINExtensionsChunk(chunk.offset, List(extensions) {
				stream.channel.resetPosition(stream.read32().le().toLong()) {
					GameMakerWINExtension(
						stream.channel.position(),
						stream.readString(stream.read32().le() - 4L),
						stream.readString(stream.read32().le() - 4L),
						stream.readString(stream.read32().le() - 4L),
						stream.readString(stream.read32().le() - 4L),
						stream.read32().le(),
						stream.read32().le(),
					)
				}
			})
		}
		this.addParser("OBJT") { stream, chunk ->
			val objects = stream.read32().le()
			GameMakerWINObjectsChunk(chunk.offset, List(objects) {
				stream.channel.resetPosition(stream.read32().le().toLong()) {
					GameMakerWINObject(
						stream.channel.position()
					)
				}
			})
		}
		this.addParser("SHDR") { stream, chunk ->
			val shaders = stream.read32().le()
			GameMakerWINShadersChunk(chunk.offset, List(shaders) {
				stream.channel.resetPosition(stream.read32().le().toLong()) {
					GameMakerWINShader(
						stream.channel.position()
					)
				}
			})
		}
		this.addParser("ROOM") { stream, chunk ->
			val shaders = stream.read32().le()
			GameMakerWINRoomsChunk(chunk.offset, List(shaders) {
				stream.channel.resetPosition(stream.read32().le().toLong()) {
					GameMakerWINRoom(
						stream.channel.position()
					)
				}
			})
		}
		this.addParser("CODE") { stream, chunk ->
			val shaders = stream.read32().le()
			GameMakerWINBytecodeChunk(chunk.offset, List(shaders) {
				stream.channel.resetPosition(stream.read32().le().toLong()) {
					GameMakerWINBytecode(
						stream.channel.position(),
						stream.readString(stream.read32().le() - 4L),
						stream.read32().le().toLong(),
						stream.read16().le().toInt(),
						stream.read16().le().toInt(),
						stream.channel.position() + stream.read32().le()
					)
				}
			})
		}
		this.addParser("SOND") { stream, chunk ->
			val sounds = stream.read32().le()
			GameMakerWINSoundsChunk(chunk.offset, List(sounds) {
				stream.channel.resetPosition(stream.read32().le().toLong()) {
					GameMakerWINSoundReference(
						stream.readString(stream.read32().le() - 4L),
						stream.read32().le(),
						stream.read32().le().let {
							if (it == 0) ""
							else stream.readString(stream.read32().le() - 4L)
						},
						stream.readString(stream.read32().le() - 4L),
						stream.read32().le(),
						Float.fromBits(stream.read32().le()),
						Float.fromBits(stream.read32().le()),
						Float.fromBits(stream.read32().le())
					)
				}
			})
		}
		this.addParser("AUDO") { stream, chunk ->
			val audios = stream.read32().le()
			GameMakerWINAudiosChunk(chunk.offset, List(audios) {
				stream.channel.resetPosition(stream.read32().le().toLong()) {
					GameMakerWINAudio(
						stream.channel.position(),
						RIFFParser(
							stream.readNBytes(stream.read32().le()).inputStream()
						).readAllParsed()
					)
				}
			})
		}
		this.addParser("TGIN") { stream, chunk ->
			val version = stream.read32().le()
			if (version != 1) throw UnsupportedOperationException("TGIN version [$version]")
			val textureGroups = stream.read32().le()
			GameMakerWINTextureGroupsChunk(chunk.offset, List(textureGroups) {
				stream.channel.resetPosition(stream.read32().le().toLong()) {
					GameMakerWINTextureGroup(
						stream.channel.position(),
						stream.readString(stream.read32().le() - 4L),
						stream.readString(stream.read32().le() - 4L),
						stream.readString(stream.read32().le() - 4L),
						stream.read32().le(),
						stream.read32().le().toLong(),
						stream.read32().le().toLong(),
						stream.read32().le().toLong(),
						stream.read32().le().toLong(),
						stream.read32().le().toLong()
					)
				}
			})
		}
		this.addParser("SPRT") { stream, chunk ->
			val sprites = stream.read32().le()
			GameMakerWINSpritesChunk(chunk.offset, List(sprites) {
				stream.channel.resetPosition(stream.read32().le().toLong()) {
					GameMakerWINSprite(
						stream.channel.position(),
						stream.readString(stream.read32().le() - 4L),
						stream.read32().le(),
						stream.read32().le(),
						stream.read32().le(),
						stream.read32().le(),
						stream.read32().le(),
						stream.read32().le(),
						stream.read32().le(),
						stream.read32().le(),
						stream.read32().le(),
						stream.read32().le(),
						stream.read32().le(),
						stream.read32().le(),
						stream.read32().le()
					)
				}
			})
		}
		this.addParser("EMBI") { stream, chunk ->
			val version = stream.read32().le()
			if (version != 1) throw UnsupportedOperationException("EMBI version [$version]")
			val images = stream.read32().le()
			GameMakerWINImagesChunk(chunk.offset, List(images) { _ ->
				GameMakerWINImageReference(
					stream.readString(stream.read32().le() - 4L),
					stream.read32().le().toLong()
				)
			})
		}
		this.addParser("FEAT") { stream, chunk ->
			val features = stream.read32().le()
			GameMakerWINFeaturesChunk(chunk.offset, List(features) {
				stream.readString(stream.read32().le() - 4L)
			})
		}
		this.addParser("SCPT") { stream, chunk ->
			val scripts = stream.read32().le()
			GameMakerWINScriptsChunk(chunk.offset, List(scripts) {
				stream.channel.resetPosition(stream.read32().le().toLong()) {
					GameMakerWINScriptReference(
						stream.readString(stream.read32().le() - 4L),
						stream.read32().le()
					)
				}
			})
		}
		this.addParser("FUNC") { stream, chunk ->
			val functions = stream.read32().le()
			GameMakerWINFunctionsChunk(chunk.offset, List(functions) { _ ->
				GameMakerWINFunctionReference(
					stream.channel.position(),
					stream.readString(stream.read32().le() - 4L),
					stream.read32().le().toLong(),
					stream.read32().le().toLong()
				)
			})
		}
		this.addParser("VARI") { stream, chunk ->
			stream.read32().le()
			stream.read32().le()
			val maxLocals = stream.read32().le()
			GameMakerWINVariablesChunk(chunk.offset, maxLocals, buildList {
				while (stream.channel.position() < (chunk.offset + chunk.length) - 12) {
					add(
						GameMakerWINVariableReference(
							stream.channel.position(),
							stream.readString(stream.read32().le() - 4L),
							stream.read32().le(),
							stream.read32().le(),
							stream.read32().le().toLong(),
							stream.read32().le().toLong().let {
								if (it == (-1).toLong()) null
								else it
							}
						)
					)
				}
			})
		}
		this.addParser("OPTN") { stream, chunk ->
			val version = stream.read32().le()
			if (version != Int.MIN_VALUE) throw UnsupportedOperationException("OPTN version [$version]")
			GameMakerWINOptionsChunk(
				chunk.offset,
				stream.read32().le(),
				stream.read64().le(),
				stream.read32().le(),
				stream.read32().le(),
				stream.read32().le(),
				stream.read32().le(),
				stream.read32().le(),
				stream.read32().le(),
				stream.read32().le(),
				stream.read32().le(),
				stream.read32().le(),
				stream.read32().le(),
				stream.read32().le(),
				List(stream.read32().le()) {
					stream.readString(stream.read32().le() - 4L) to
							stream.readString(stream.read32().le() - 4L)
				}.toMap()
			)
		}
//		this.addParser("AGRP") { stream, chunk ->
//			val groups = stream.read32().le()
//			GameMakerWINAudioGroupsChunk(chunk.offset, List(groups) { _ ->
//				stream.channel.resetPosition(stream.read32().le().toLong()) {
//					GameMakerWINAudioGroup(
//						stream.channel.position(),
//						stream.readString(stream.read32().le() - 4L)
//					)
//				}
//			})
//		}
		this.addParser("FONT") { stream, chunk ->
			val fonts = stream.read32().le()
			GameMakerWINFontsChunk(chunk.offset, List(fonts) { _ ->
				stream.channel.resetPosition(stream.read32().le().toLong()) {
					GameMakerWINFont(
						stream.readString(stream.read32().le() - 4L),
						stream.readString(stream.read32().le() - 4L),
						stream.read32().le(),
						stream.read32().le(),
						stream.read32().le(),
						stream.read16().le().toInt(),
						stream.read(),
						stream.read(),
						stream.read32().le(),
						stream.read32().le().toLong(),
						Float.fromBits(stream.read32().le()),
						Float.fromBits(stream.read32().le()),
						stream.read32().le(),
						stream.read32().le(),
						stream.read32().le(),
						stream.read32().le(),
						buildList {
							val last = stream.read32().le()
							while (stream.channel.position() < last) {
								stream.channel.resetPosition(stream.read32().le().toLong()) {
									add(
										GameMakerWINFontGlyph(
											stream.read16().le().toUShort(),
											stream.read16().le(),
											stream.read16().le(),
											stream.read16().le(),
											stream.read16().le(),
											stream.read16().le(),
											stream.read16().le()
										)
									)
								}
							}
						}
					)
				}
			})
		}
	}
}