package org.bread_experts_group.coder.format.gamemaker_win

import org.bread_experts_group.coder.format.Parser
import org.bread_experts_group.coder.format.gamemaker_win.chunk.*
import org.bread_experts_group.coder.format.gamemaker_win.structure.*
import org.bread_experts_group.coder.format.riff.RIFFInputStream
import org.bread_experts_group.stream.*
import java.io.FileInputStream
import java.lang.Short
import kotlin.Float
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.UnsupportedOperationException
import kotlin.fromBits
import kotlin.let
import kotlin.to
import kotlin.toUShort

class GameMakerWINInputStream(
	from: FileInputStream
) : Parser<String, GameMakerWINChunk, FileInputStream>("GameMaker WIN File Format", from) {
	override fun responsibleStream(of: GameMakerWINChunk): FileInputStream = from

	override fun readBase(): GameMakerWINChunk {
		val name = this.readString(4)
		val size = Integer.reverseBytes(this.read32())
		val element = GameMakerWINChunk(name, from.channel.position())
		element.length = size
		from.channel.position(element.offset + size)
		return element
	}

	override fun refineBase(of: GameMakerWINChunk): GameMakerWINChunk = from.resetPosition(of.offset) {
		val refined = super.refineBase(of)
		refined.length = of.length
		refined
	}

	private fun FileInputStream.readString(at: Long): String = this.resetPosition(at) {
		this.readString(Integer.reverseBytes(this.read32()))
	}

	init {
		this.addParser("FORM") { stream, chunk ->
			GameMakerWINContainerChunk(
				chunk.tag,
				chunk.offset,
				GameMakerWINInputStream(stream).readAllParsed()
			)
		}
		this.addParser("STRG") { stream, chunk ->
			val strings = Integer.reverseBytes(stream.read32())
			GameMakerWINStringsChunk(chunk.offset, List(strings) {
				stream.readString(Integer.reverseBytes(stream.read32()).toLong())
			})
		}
		this.addParser("TAGS") { stream, chunk ->
			val version = Integer.reverseBytes(stream.read32())
			if (version != 1) throw UnsupportedOperationException("TAGS version [$version]")
			GameMakerWINTagsChunk(chunk.offset, List(Integer.reverseBytes(stream.read32())) {
				stream.readString(Integer.reverseBytes(stream.read32()) - 4L)
			}, List(Integer.reverseBytes(stream.read32())) {
				stream.resetPosition(Integer.reverseBytes(stream.read32()).toLong()) {
					Integer.reverseBytes(stream.read32()) to List(Integer.reverseBytes(stream.read32())) {
						stream.readString(Integer.reverseBytes(stream.read32()) - 4L)
					}
				}
			}.toMap())
		}
		this.addParser("TXTR") { stream, chunk ->
			val textures = Integer.reverseBytes(stream.read32())
			GameMakerWINTexturesChunk(chunk.offset, List(textures) {
				stream.resetPosition(Integer.reverseBytes(stream.read32()).toLong()) {
					GameMakerWINTexture(
						stream.channel.position(),
						Integer.reverseBytes(stream.read32()).toLong(),
						Integer.reverseBytes(stream.read32()).toLong(),
						Integer.reverseBytes(stream.read32()).toLong(),
						Integer.reverseBytes(stream.read32()),
						Integer.reverseBytes(stream.read32()),
						Integer.reverseBytes(stream.read32()),
						Integer.reverseBytes(stream.read32()).toLong()
					)
				}
			})
		}
		this.addParser("EXTN") { stream, chunk ->
			val extensions = Integer.reverseBytes(stream.read32())
			GameMakerWINExtensionsChunk(chunk.offset, List(extensions) {
				stream.resetPosition(Integer.reverseBytes(stream.read32()).toLong()) {
					GameMakerWINExtension(
						stream.channel.position(),
						stream.readString(Integer.reverseBytes(stream.read32()) - 4L),
						stream.readString(Integer.reverseBytes(stream.read32()) - 4L),
						stream.readString(Integer.reverseBytes(stream.read32()) - 4L),
						stream.readString(Integer.reverseBytes(stream.read32()) - 4L),
						Integer.reverseBytes(stream.read32()),
						Integer.reverseBytes(stream.read32()),
					)
				}
			})
		}
		this.addParser("OBJT") { stream, chunk ->
			val objects = Integer.reverseBytes(stream.read32())
			GameMakerWINObjectsChunk(chunk.offset, List(objects) {
				stream.resetPosition(Integer.reverseBytes(stream.read32()).toLong()) {
					GameMakerWINObject(
						stream.channel.position()
					)
				}
			})
		}
		this.addParser("SHDR") { stream, chunk ->
			val shaders = Integer.reverseBytes(stream.read32())
			GameMakerWINShadersChunk(chunk.offset, List(shaders) {
				stream.resetPosition(Integer.reverseBytes(stream.read32()).toLong()) {
					GameMakerWINShader(
						stream.channel.position()
					)
				}
			})
		}
		this.addParser("ROOM") { stream, chunk ->
			val shaders = Integer.reverseBytes(stream.read32())
			GameMakerWINRoomsChunk(chunk.offset, List(shaders) {
				stream.resetPosition(Integer.reverseBytes(stream.read32()).toLong()) {
					GameMakerWINRoom(
						stream.channel.position()
					)
				}
			})
		}
		this.addParser("CODE") { stream, chunk ->
			val shaders = Integer.reverseBytes(stream.read32())
			GameMakerWINBytecodeChunk(chunk.offset, List(shaders) {
				stream.resetPosition(Integer.reverseBytes(stream.read32()).toLong()) {
					GameMakerWINBytecode(
						stream.channel.position(),
						stream.readString(Integer.reverseBytes(stream.read32()) - 4L),
						Integer.reverseBytes(stream.read32()).toLong(),
						Short.reverseBytes(stream.read16()).toInt(),
						Short.reverseBytes(stream.read16()).toInt(),
						stream.channel.position() + Integer.reverseBytes(stream.read32())
					)
				}
			})
		}
		this.addParser("SOND") { stream, chunk ->
			val sounds = Integer.reverseBytes(stream.read32())
			GameMakerWINSoundsChunk(chunk.offset, List(sounds) {
				stream.resetPosition(Integer.reverseBytes(stream.read32()).toLong()) {
					GameMakerWINSoundReference(
						stream.readString(Integer.reverseBytes(stream.read32()) - 4L),
						Integer.reverseBytes(stream.read32()),
						Integer.reverseBytes(stream.read32()).let {
							if (it == 0) ""
							else stream.readString(Integer.reverseBytes(stream.read32()) - 4L)
						},
						stream.readString(Integer.reverseBytes(stream.read32()) - 4L),
						Integer.reverseBytes(stream.read32()),
						Float.fromBits(Integer.reverseBytes(stream.read32())),
						Float.fromBits(Integer.reverseBytes(stream.read32())),
						Float.fromBits(Integer.reverseBytes(stream.read32()))
					)
				}
			})
		}
		this.addParser("AUDO") { stream, chunk ->
			val audios = Integer.reverseBytes(stream.read32())
			GameMakerWINAudiosChunk(chunk.offset, List(audios) {
				stream.resetPosition(Integer.reverseBytes(stream.read32()).toLong()) {
					GameMakerWINAudio(
						stream.channel.position(),
						RIFFInputStream(
							stream.readNBytes(Integer.reverseBytes(stream.read32())).inputStream()
						).readAllParsed()
					)
				}
			})
		}
		this.addParser("TGIN") { stream, chunk ->
			val version = Integer.reverseBytes(stream.read32())
			if (version != 1) throw UnsupportedOperationException("TGIN version [$version]")
			val textureGroups = Integer.reverseBytes(stream.read32())
			GameMakerWINTextureGroupsChunk(chunk.offset, List(textureGroups) {
				stream.resetPosition(Integer.reverseBytes(stream.read32()).toLong()) {
					GameMakerWINTextureGroup(
						stream.channel.position(),
						stream.readString(Integer.reverseBytes(stream.read32()) - 4L),
						stream.readString(Integer.reverseBytes(stream.read32()) - 4L),
						stream.readString(Integer.reverseBytes(stream.read32()) - 4L),
						Integer.reverseBytes(stream.read32()),
						Integer.reverseBytes(stream.read32()).toLong(),
						Integer.reverseBytes(stream.read32()).toLong(),
						Integer.reverseBytes(stream.read32()).toLong(),
						Integer.reverseBytes(stream.read32()).toLong(),
						Integer.reverseBytes(stream.read32()).toLong()
					)
				}
			})
		}
		this.addParser("SPRT") { stream, chunk ->
			val sprites = Integer.reverseBytes(stream.read32())
			GameMakerWINSpritesChunk(chunk.offset, List(sprites) {
				stream.resetPosition(Integer.reverseBytes(stream.read32()).toLong()) {
					GameMakerWINSprite(
						stream.channel.position(),
						stream.readString(Integer.reverseBytes(stream.read32()) - 4L),
						Integer.reverseBytes(stream.read32()),
						Integer.reverseBytes(stream.read32()),
						Integer.reverseBytes(stream.read32()),
						Integer.reverseBytes(stream.read32()),
						Integer.reverseBytes(stream.read32()),
						Integer.reverseBytes(stream.read32()),
						Integer.reverseBytes(stream.read32()),
						Integer.reverseBytes(stream.read32()),
						Integer.reverseBytes(stream.read32()),
						Integer.reverseBytes(stream.read32()),
						Integer.reverseBytes(stream.read32()),
						Integer.reverseBytes(stream.read32()),
						Integer.reverseBytes(stream.read32())
					)
				}
			})
		}
		this.addParser("EMBI") { stream, chunk ->
			val version = Integer.reverseBytes(stream.read32())
			if (version != 1) throw UnsupportedOperationException("EMBI version [$version]")
			val images = Integer.reverseBytes(stream.read32())
			GameMakerWINImagesChunk(chunk.offset, List(images) { _ ->
				GameMakerWINImageReference(
					stream.readString(Integer.reverseBytes(stream.read32()) - 4L),
					Integer.reverseBytes(stream.read32()).toLong()
				)
			})
		}
		this.addParser("FEAT") { stream, chunk ->
			val features = Integer.reverseBytes(stream.read32())
			GameMakerWINFeaturesChunk(chunk.offset, List(features) {
				stream.readString(Integer.reverseBytes(stream.read32()) - 4L)
			})
		}
		this.addParser("SCPT") { stream, chunk ->
			val scripts = Integer.reverseBytes(stream.read32())
			GameMakerWINScriptsChunk(chunk.offset, List(scripts) {
				stream.resetPosition(Integer.reverseBytes(stream.read32()).toLong()) {
					GameMakerWINScriptReference(
						stream.readString(Integer.reverseBytes(stream.read32()) - 4L),
						Integer.reverseBytes(stream.read32())
					)
				}
			})
		}
		this.addParser("FUNC") { stream, chunk ->
			val functions = Integer.reverseBytes(stream.read32())
			GameMakerWINFunctionsChunk(chunk.offset, List(functions) { _ ->
				GameMakerWINFunctionReference(
					stream.channel.position(),
					stream.readString(Integer.reverseBytes(stream.read32()) - 4L),
					Integer.reverseBytes(stream.read32()).toLong(),
					Integer.reverseBytes(stream.read32()).toLong()
				)
			})
		}
		this.addParser("VARI") { stream, chunk ->
			Integer.reverseBytes(stream.read32())
			Integer.reverseBytes(stream.read32())
			val maxLocals = Integer.reverseBytes(stream.read32())
			GameMakerWINVariablesChunk(chunk.offset, maxLocals, buildList {
				while (stream.channel.position() < (chunk.offset + chunk.length) - 12) {
					add(
						GameMakerWINVariableReference(
							stream.channel.position(),
							stream.readString(Integer.reverseBytes(stream.read32()) - 4L),
							Integer.reverseBytes(stream.read32()),
							Integer.reverseBytes(stream.read32()),
							Integer.reverseBytes(stream.read32()).toLong(),
							Integer.reverseBytes(stream.read32()).toLong().let {
								if (it == (-1).toLong()) null
								else it
							}
						)
					)
				}
			})
		}
		this.addParser("OPTN") { stream, chunk ->
			val version = Integer.reverseBytes(stream.read32())
			if (version != Int.MIN_VALUE) throw UnsupportedOperationException("OPTN version [$version]")
			GameMakerWINOptionsChunk(
				chunk.offset,
				Integer.reverseBytes(stream.read32()),
				java.lang.Long.reverseBytes(stream.read64()),
				Integer.reverseBytes(stream.read32()),
				Integer.reverseBytes(stream.read32()),
				Integer.reverseBytes(stream.read32()),
				Integer.reverseBytes(stream.read32()),
				Integer.reverseBytes(stream.read32()),
				Integer.reverseBytes(stream.read32()),
				Integer.reverseBytes(stream.read32()),
				Integer.reverseBytes(stream.read32()),
				Integer.reverseBytes(stream.read32()),
				Integer.reverseBytes(stream.read32()),
				Integer.reverseBytes(stream.read32()),
				List(Integer.reverseBytes(stream.read32())) {
					stream.readString(Integer.reverseBytes(stream.read32()) - 4L) to
							stream.readString(Integer.reverseBytes(stream.read32()) - 4L)
				}.toMap()
			)
		}
//		this.addParser("AGRP") { stream, chunk ->
//			val groups = Integer.reverseBytes(stream.read32())
//			GameMakerWINAudioGroupsChunk(chunk.offset, List(groups) { _ ->
//				stream.resetPosition(Integer.reverseBytes(stream.read32()).toLong()) {
//					GameMakerWINAudioGroup(
//						stream.channel.position(),
//						stream.readString(Integer.reverseBytes(stream.read32()) - 4L)
//					)
//				}
//			})
//		}
		this.addParser("FONT") { stream, chunk ->
			val fonts = Integer.reverseBytes(stream.read32())
			GameMakerWINFontsChunk(chunk.offset, List(fonts) { _ ->
				stream.resetPosition(Integer.reverseBytes(stream.read32()).toLong()) {
					GameMakerWINFont(
						stream.readString(Integer.reverseBytes(stream.read32()) - 4L),
						stream.readString(Integer.reverseBytes(stream.read32()) - 4L),
						Integer.reverseBytes(stream.read32()),
						Integer.reverseBytes(stream.read32()),
						Integer.reverseBytes(stream.read32()),
						Short.reverseBytes(stream.read16()).toInt(),
						stream.read(),
						stream.read(),
						Integer.reverseBytes(stream.read32()),
						Integer.reverseBytes(stream.read32()).toLong(),
						Float.fromBits(Integer.reverseBytes(stream.read32())),
						Float.fromBits(Integer.reverseBytes(stream.read32())),
						Integer.reverseBytes(stream.read32()),
						Integer.reverseBytes(stream.read32()),
						Integer.reverseBytes(stream.read32()),
						Integer.reverseBytes(stream.read32()),
						buildList {
							val last = Integer.reverseBytes(stream.read32())
							while (stream.channel.position() < last) {
								stream.resetPosition(Integer.reverseBytes(stream.read32()).toLong()) {
									add(
										GameMakerWINFontGlyph(
											Short.reverseBytes(stream.read16()).toUShort(),
											Short.reverseBytes(stream.read16()),
											Short.reverseBytes(stream.read16()),
											Short.reverseBytes(stream.read16()),
											Short.reverseBytes(stream.read16()),
											Short.reverseBytes(stream.read16()),
											Short.reverseBytes(stream.read16())
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