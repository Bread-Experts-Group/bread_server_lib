package org.bread_experts_group.coder.format.parse.gamemaker_win

import org.bread_experts_group.coder.CodingException
import org.bread_experts_group.coder.format.parse.CodingCompoundThrowable
import org.bread_experts_group.coder.format.parse.Parser
import org.bread_experts_group.coder.format.parse.gamemaker_win.chunk.*
import org.bread_experts_group.coder.format.parse.gamemaker_win.structure.*
import org.bread_experts_group.coder.format.parse.riff.RIFFParser
import org.bread_experts_group.stream.*
import java.io.FileInputStream

class GameMakerWINParser(
	from: FileInputStream
) : Parser<String, GameMakerWINChunk, FileInputStream>("GameMaker WIN File Format", from) {
	override fun responsibleStream(of: GameMakerWINChunk): FileInputStream = rawStream

	override fun readBase(compound: CodingCompoundThrowable): GameMakerWINChunk {
		val name = fqIn.readString(4)
		val size = fqIn.read32().le()
		val element = GameMakerWINChunk(name, rawStream.channel.position())
		element.length = size
		rawStream.channel.position(element.offset + size)
		return element
	}

	override fun refineBase(
		compound: CodingCompoundThrowable, of: GameMakerWINChunk,
		vararg parameters: Any
	): Pair<GameMakerWINChunk, CodingException?> {
		return rawStream.channel.resetPosition(of.offset) {
			val refined = super.refineBase(compound, of)
			refined.first.length = of.length
			refined
		}
	}

	private fun readString(at: Long): String = rawStream.channel.resetPosition(at) {
		rawStream.readString(rawStream.read32().le())
	}

	init {
		this.addParser("FORM") { _, chunk, _ ->
			GameMakerWINContainerChunk(
				chunk.tag,
				chunk.offset,
				GameMakerWINParser(rawStream).toList()
			)
		}
		this.addParser("STRG") { stream, chunk, _ ->
			val strings = stream.read32().le()
			GameMakerWINStringsChunk(chunk.offset, List(strings) {
				readString(stream.read32().le().toLong())
			})
		}
		this.addParser("TAGS") { stream, chunk, _ ->
			val version = stream.read32().le()
			if (version != 1) throw UnsupportedOperationException("TAGS version [$version]")
			GameMakerWINTagsChunk(chunk.offset, List(stream.read32().le()) {
				readString(stream.read32().le() - 4L)
			}, List(stream.read32().le()) {
				rawStream.channel.resetPosition(stream.read32().le().toLong()) {
					stream.read32().le() to List(stream.read32().le()) {
						readString(stream.read32().le() - 4L)
					}
				}
			}.toMap())
		}
		this.addParser("TXTR") { stream, chunk, _ ->
			val textures = stream.read32().le()
			GameMakerWINTexturesChunk(chunk.offset, List(textures) {
				rawStream.channel.resetPosition(stream.read32().le().toLong()) {
					GameMakerWINTexture(
						rawStream.channel.position(),
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
		this.addParser("EXTN") { stream, chunk, _ ->
			val extensions = stream.read32().le()
			GameMakerWINExtensionsChunk(chunk.offset, List(extensions) {
				rawStream.channel.resetPosition(stream.read32().le().toLong()) {
					GameMakerWINExtension(
						rawStream.channel.position(),
						readString(stream.read32().le() - 4L),
						readString(stream.read32().le() - 4L),
						readString(stream.read32().le() - 4L),
						readString(stream.read32().le() - 4L),
						stream.read32().le(),
						stream.read32().le(),
					)
				}
			})
		}
		this.addParser("OBJT") { stream, chunk, _ ->
			val objects = stream.read32().le()
			GameMakerWINObjectsChunk(chunk.offset, List(objects) {
				rawStream.channel.resetPosition(stream.read32().le().toLong()) {
					GameMakerWINObject(
						rawStream.channel.position()
					)
				}
			})
		}
		this.addParser("SHDR") { stream, chunk, _ ->
			val shaders = stream.read32().le()
			GameMakerWINShadersChunk(chunk.offset, List(shaders) {
				rawStream.channel.resetPosition(stream.read32().le().toLong()) {
					GameMakerWINShader(
						rawStream.channel.position()
					)
				}
			})
		}
		this.addParser("ROOM") { stream, chunk, _ ->
			val shaders = stream.read32().le()
			GameMakerWINRoomsChunk(chunk.offset, List(shaders) {
				rawStream.channel.resetPosition(stream.read32().le().toLong()) {
					GameMakerWINRoom(
						rawStream.channel.position()
					)
				}
			})
		}
		this.addParser("CODE") { stream, chunk, _ ->
			val shaders = stream.read32().le()
			GameMakerWINBytecodeChunk(chunk.offset, List(shaders) {
				rawStream.channel.resetPosition(stream.read32().le().toLong()) {
					GameMakerWINBytecode(
						rawStream.channel.position(),
						readString(stream.read32().le() - 4L),
						stream.read32().le().toLong(),
						stream.read16().le().toInt(),
						stream.read16().le().toInt(),
						rawStream.channel.position() + stream.read32().le()
					)
				}
			})
		}
		this.addParser("SOND") { stream, chunk, _ ->
			val sounds = stream.read32().le()
			GameMakerWINSoundsChunk(chunk.offset, List(sounds) {
				rawStream.channel.resetPosition(stream.read32().le().toLong()) {
					GameMakerWINSoundReference(
						readString(stream.read32().le() - 4L),
						stream.read32().le(),
						stream.read32().le().let {
							if (it == 0) ""
							else readString(stream.read32().le() - 4L)
						},
						readString(stream.read32().le() - 4L),
						stream.read32().le(),
						Float.fromBits(stream.read32().le()),
						Float.fromBits(stream.read32().le()),
						Float.fromBits(stream.read32().le())
					)
				}
			})
		}
		this.addParser("AUDO") { stream, chunk, _ ->
			val audios = stream.read32().le()
			GameMakerWINAudiosChunk(chunk.offset, List(audios) {
				rawStream.channel.resetPosition(stream.read32().le().toLong()) {
					GameMakerWINAudio(
						rawStream.channel.position(),
						RIFFParser(
							stream.readNBytes(stream.read32().le()).inputStream()
						).toList()
					)
				}
			})
		}
		this.addParser("TGIN") { stream, chunk, _ ->
			val version = stream.read32().le()
			if (version != 1) throw UnsupportedOperationException("TGIN version [$version]")
			val textureGroups = stream.read32().le()
			GameMakerWINTextureGroupsChunk(chunk.offset, List(textureGroups) {
				rawStream.channel.resetPosition(stream.read32().le().toLong()) {
					GameMakerWINTextureGroup(
						rawStream.channel.position(),
						readString(stream.read32().le() - 4L),
						readString(stream.read32().le() - 4L),
						readString(stream.read32().le() - 4L),
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
		this.addParser("SPRT") { stream, chunk, _ ->
			val sprites = stream.read32().le()
			GameMakerWINSpritesChunk(chunk.offset, List(sprites) {
				rawStream.channel.resetPosition(stream.read32().le().toLong()) {
					GameMakerWINSprite(
						rawStream.channel.position(),
						readString(stream.read32().le() - 4L),
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
		this.addParser("EMBI") { stream, chunk, _ ->
			val version = stream.read32().le()
			if (version != 1) throw UnsupportedOperationException("EMBI version [$version]")
			val images = stream.read32().le()
			GameMakerWINImagesChunk(chunk.offset, List(images) { _ ->
				GameMakerWINImageReference(
					readString(stream.read32().le() - 4L),
					stream.read32().le().toLong()
				)
			})
		}
		this.addParser("FEAT") { stream, chunk, _ ->
			val features = stream.read32().le()
			GameMakerWINFeaturesChunk(chunk.offset, List(features) {
				readString(stream.read32().le() - 4L)
			})
		}
		this.addParser("SCPT") { stream, chunk, _ ->
			val scripts = stream.read32().le()
			GameMakerWINScriptsChunk(chunk.offset, List(scripts) {
				rawStream.channel.resetPosition(stream.read32().le().toLong()) {
					GameMakerWINScriptReference(
						readString(stream.read32().le() - 4L),
						stream.read32().le()
					)
				}
			})
		}
		this.addParser("FUNC") { stream, chunk, _ ->
			val functions = stream.read32().le()
			GameMakerWINFunctionsChunk(chunk.offset, List(functions) { _ ->
				GameMakerWINFunctionReference(
					rawStream.channel.position(),
					readString(stream.read32().le() - 4L),
					stream.read32().le().toLong(),
					stream.read32().le().toLong()
				)
			})
		}
		this.addParser("VARI") { stream, chunk, _ ->
			stream.read32().le()
			stream.read32().le()
			val maxLocals = stream.read32().le()
			GameMakerWINVariablesChunk(chunk.offset, maxLocals, buildList {
				while (rawStream.channel.position() < (chunk.offset + chunk.length) - 12) {
					add(
						GameMakerWINVariableReference(
							rawStream.channel.position(),
							readString(stream.read32().le() - 4L),
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
		this.addParser("OPTN") { stream, chunk, _ ->
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
					readString(stream.read32().le() - 4L) to
							readString(stream.read32().le() - 4L)
				}.toMap()
			)
		}
//		this.addParser("AGRP") { stream, chunk ->
//			val groups = stream.read32().le()
//			GameMakerWINAudioGroupsChunk(chunk.offset, List(groups) { _ ->
//				rawStream.channel.resetPosition(stream.read32().le().toLong()) {
//					GameMakerWINAudioGroup(
//						rawStream.channel.position(),
//						readString(stream.read32().le() - 4L)
//					)
//				}
//			})
//		}
		this.addParser("FONT") { stream, chunk, _ ->
			val fonts = stream.read32().le()
			GameMakerWINFontsChunk(chunk.offset, List(fonts) { _ ->
				rawStream.channel.resetPosition(stream.read32().le().toLong()) {
					GameMakerWINFont(
						readString(stream.read32().le() - 4L),
						readString(stream.read32().le() - 4L),
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
							while (rawStream.channel.position() < last) {
								rawStream.channel.resetPosition(stream.read32().le().toLong()) {
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