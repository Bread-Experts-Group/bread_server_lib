package org.bread_experts_group.protocol.old.minecraft

import org.bread_experts_group.channel.LockedReadableChannel
import org.bread_experts_group.coder.Flaggable.Companion.from
import org.bread_experts_group.coder.Mappable.Companion.id
import org.bread_experts_group.coder.format.parse.CodingPartialResult
import org.bread_experts_group.coder.format.parse.nbt.NBTByteParser
import org.bread_experts_group.coder.format.parse.nbt.tag.NBTTag
import org.bread_experts_group.io.reader.ReadingByteBuffer
import org.bread_experts_group.numeric.geometry.Point3D
import org.bread_experts_group.numeric.geometry.Point3I
import org.bread_experts_group.protocol.old.minecraft.packet.MinecraftPacket
import org.bread_experts_group.protocol.old.minecraft.packet.MinecraftSide
import org.bread_experts_group.protocol.old.minecraft.packet.SidedIdentifier
import org.bread_experts_group.protocol.old.minecraft.packet.handshake.MinecraftHandshakeInitiatePacket
import org.bread_experts_group.protocol.old.minecraft.packet.handshake.MinecraftHandshakeNextState
import org.bread_experts_group.protocol.old.minecraft.packet.handshake.MinecraftHandshakePacketType
import org.bread_experts_group.protocol.old.minecraft.packet.login.MinecraftLoginPacketType
import org.bread_experts_group.protocol.old.minecraft.packet.login.MinecraftLoginStartPacket
import org.bread_experts_group.protocol.old.minecraft.packet.play.*
import org.bread_experts_group.protocol.old.minecraft.packet.status.MinecraftStatusPacketType
import org.bread_experts_group.protocol.old.minecraft.packet.status.MinecraftStatusPingPacket
import org.bread_experts_group.protocol.old.minecraft.packet.status.MinecraftStatusRequestPacket
import java.nio.ByteBuffer
import java.nio.channels.ReadableByteChannel
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.Semaphore

class MinecraftConnection(private val from: ReadableByteChannel) {
	val backlog: LinkedBlockingQueue<Result<MinecraftPacket>> = LinkedBlockingQueue<Result<MinecraftPacket>>()
	fun next(): Result<MinecraftPacket> = backlog.take()

	fun ReadingByteBuffer.boolean(): Boolean {
		refill(1)
		return this.i8() != 0.toByte()
	}

	fun ReadingByteBuffer.varN64(): Pair<Long, Int> {
		var base = 0L
		var position = 0
		var read = 0
		while (true) {
			val next = this.u8().toLong()
			read++
			base = base or ((next and 0b01111111) shl position)
			if (next and 0b10000000 == 0L) break
			position += 7
		}
		return base to read
	}

	fun ReadingByteBuffer.varN32(): Int = varN64().first.toInt()
	fun ReadingByteBuffer.string(n: Int = Int.MAX_VALUE): String {
		val length = varN32()
		if (length > n) throw IndexOutOfBoundsException("String too large; $length > $n")
		val string = ByteArray(length)
		get(string)
		return string.decodeToString()
	}

	fun ReadingByteBuffer.p3I(): Point3I {
		val packed = this.i64()
		return Point3I(
			(packed ushr 38).toInt(),
			((packed ushr 26) and 0xFFF).toInt(),
			(packed and 0xFFF).toInt()
		)
	}

	fun ReadingByteBuffer.slot(): MinecraftSlot {
		val itemID = this.i16()
		val itemCount: Byte?
		val itemDamage: Short?
		val nbt: List<CodingPartialResult<NBTTag>>?
		if (itemID != (-1).toShort()) {
			itemCount = this.i8()
			itemDamage = this.i16()
			nbt = NBTByteParser().setInputReading(this).toList()
		} else {
			itemCount = null
			itemDamage = null
			nbt = null
		}
		return MinecraftSlot(itemID, itemCount, itemDamage, nbt)
	}

	var connectionState = MinecraftConnectionState.HANDSHAKE
	var length = 0L

	init {
		Thread.ofVirtual().name("Minecraft Backlogger").start {
			val fromLock = Semaphore(1)
			val proxy = ReadingByteBuffer(
				from,
				ByteBuffer.allocateDirect(8192),
				::length
			)
			while (true) {
				fromLock.acquire()
				length = proxy.varN32().toLong()
				val id = proxy.varN32()
				fromLock.release()
				val add = when (connectionState) {
					MinecraftConnectionState.HANDSHAKE -> {
						val packetID = MinecraftHandshakePacketType.entries.id(id).enum!!
						when (packetID) {
							MinecraftHandshakePacketType.HANDSHAKE -> {
								val version = proxy.varN32()
								val server = proxy.string(255)
								val port = proxy.u16()
								val state = proxy.varN32()
								val nextState = MinecraftHandshakeNextState.entries.id(state).enum!!
								connectionState = when (nextState) {
									MinecraftHandshakeNextState.STATUS -> MinecraftConnectionState.STATUS
									MinecraftHandshakeNextState.LOGIN -> MinecraftConnectionState.LOGIN
								}
								MinecraftHandshakeInitiatePacket(
									version, server, port, nextState,
									LockedReadableChannel(proxy.buffer, fromLock, length)
								)
							}
						}
					}

					MinecraftConnectionState.STATUS -> {
						val packetID = MinecraftStatusPacketType.entries.id(id).enum!!
						when (packetID) {
							MinecraftStatusPacketType.REQUEST -> MinecraftStatusRequestPacket(
								LockedReadableChannel(proxy.buffer, fromLock, length)
							)

							MinecraftStatusPacketType.PING -> MinecraftStatusPingPacket(
								proxy.i64(),
								LockedReadableChannel(proxy.buffer, fromLock, length)
							)
						}
					}

					MinecraftConnectionState.LOGIN -> {
						val packetID = MinecraftLoginPacketType.entries.id(
							SidedIdentifier(MinecraftSide.TO_SERVER, id)
						).enum!!
						when (packetID) {
							MinecraftLoginPacketType.LOGIN_START -> {
								val username = proxy.string(16)
								MinecraftLoginStartPacket(
									username,
									LockedReadableChannel(proxy.buffer, fromLock, length)
								)
							}

							MinecraftLoginPacketType.LOGIN_SUCCESS -> throw UnsupportedOperationException()

							else -> throw UnsupportedOperationException()
						}
					}

					MinecraftConnectionState.PLAY -> {
						val packetID = MinecraftPlayPacketType.entries.id(
							SidedIdentifier(MinecraftSide.TO_SERVER, id)
						).enum!!
						when (packetID) {
							MinecraftPlayPacketType.TELEPORT_CONFIRM -> {
								val id = proxy.varN32()
								MinecraftPlayTeleportConfirmPacket(
									id,
									LockedReadableChannel(proxy.buffer, fromLock, length)
								)
							}

							MinecraftPlayPacketType.CHAT_MESSAGE_TS -> {
								val messageString = proxy.string(256)
								MinecraftPlayChatMessageToServerPacket(
									messageString,
									LockedReadableChannel(proxy.buffer, fromLock, length)
								)
							}

							MinecraftPlayPacketType.CLIENT_STATUS -> {
								val action = proxy.varN32()
								MinecraftPlayClientStatusPacket(
									MinecraftClientStatusAction.entries.id(action).enum!!,
									LockedReadableChannel(proxy.buffer, fromLock, length)
								)
							}

							MinecraftPlayPacketType.CLIENT_SETTINGS -> {
								val localeString = proxy.string(16)
								val viewDistance = proxy.i8()
								val chatMode = proxy.varN32()
								val chatColors = proxy.boolean()
								val displayedSkinParts = proxy.u8()
								val mainHand = proxy.varN32()
								MinecraftPlayClientSettingsPacket(
									Locale.of(localeString),
									viewDistance,
									MinecraftChatMode.entries.id(chatMode).enum!!,
									chatColors,
									MinecraftSkinPart.entries.from(displayedSkinParts),
									MinecraftMainHand.entries.id(mainHand).enum!!,
									LockedReadableChannel(proxy.buffer, fromLock, length)
								)
							}

							MinecraftPlayPacketType.CLICK_WINDOW -> {
								val window = proxy.u8()
								val slot = proxy.i16()
								val button = proxy.i8()
								val actionNumber = proxy.i16()
								val mode = proxy.varN32()
								val item = proxy.slot()
								MinecraftPlayClickWindowPacket(
									window, slot, button, actionNumber, mode, item,
									LockedReadableChannel(proxy.buffer, fromLock, length)
								)
							}

							MinecraftPlayPacketType.CLOSE_WINDOW_TS -> MinecraftPlayCloseWindowToServerPacket(
								proxy.i8(),
								LockedReadableChannel(proxy.buffer, fromLock, length - 1)
							)

							MinecraftPlayPacketType.PLUGIN_MESSAGE_TS -> {
								val channelString = proxy.string(20)
								MinecraftPlayPluginMessageToServerPacket(
									channelString,
									LockedReadableChannel(proxy.buffer, fromLock, length)
								)
							}

							MinecraftPlayPacketType.KEEP_ALIVE_TS -> MinecraftPlayKeepAliveReplyPacket(
								proxy.i64(),
								LockedReadableChannel(proxy.buffer, fromLock, length)
							)

							MinecraftPlayPacketType.PLAYER_POSITION -> MinecraftPlayPlayerPositionPacket(
								Point3D(proxy.f64(), proxy.f64(), proxy.f64()), proxy.boolean(),
								LockedReadableChannel(proxy.buffer, fromLock, length)
							)

							MinecraftPlayPacketType.PLAYER_POSITION_LOOK_TS -> {
								MinecraftPlayPlayerPositionLookToServerPacket(
									Point3D(proxy.f64(), proxy.f64(), proxy.f64()),
									proxy.f32(), proxy.f32(), proxy.boolean(),
									LockedReadableChannel(proxy.buffer, fromLock, length)
								)
							}

							MinecraftPlayPacketType.PLAYER_LOOK -> MinecraftPlayPlayerLookPacket(
								proxy.f32(), proxy.f32(), proxy.boolean(),
								LockedReadableChannel(proxy.buffer, fromLock, length)
							)

							MinecraftPlayPacketType.PLAYER_ABILITIES_TS -> MinecraftPlayPlayerAbilitiesToServerPacket(
								MinecraftPlayerAbility.entries.from(proxy.u8()),
								proxy.f32(), proxy.f32(),
								LockedReadableChannel(proxy.buffer, fromLock, length - 9)
							)

							MinecraftPlayPacketType.ITEM_ACTION -> {
								val action = proxy.varN32()
								val position = proxy.p3I()
								val face = proxy.i8()
								MinecraftPlayItemActionPacket(
									MinecraftItemAction.entries.id(action).enum!!,
									position,
									MinecraftFace.entries.id(face).enum!!,
									LockedReadableChannel(proxy.buffer, fromLock, length)
								)
							}

							MinecraftPlayPacketType.ENTITY_ACTION -> {
								val entity = proxy.varN32()
								val action = proxy.varN32()
								val boost = proxy.varN32()
								MinecraftPlayEntityActionPacket(
									entity, MinecraftEntityAction.entries.id(action).enum!!,
									boost,
									LockedReadableChannel(proxy.buffer, fromLock, length)
								)
							}

							MinecraftPlayPacketType.CRAFTING_BOOK_DATA -> {
								val type = proxy.varN32()
								when (MinecraftCraftingBookDataType.entries.id(type).enum!!) {
									MinecraftCraftingBookDataType.DISPLAYED_RECIPE ->
										MinecraftPlayCraftingBookDisplayPacket(
											proxy.i32(),
											LockedReadableChannel(proxy.buffer, fromLock, length)
										)

									MinecraftCraftingBookDataType.STATUS -> MinecraftPlayCraftingBookStatusPacket(
										proxy.boolean(),
										proxy.boolean(),
										LockedReadableChannel(proxy.buffer, fromLock, length)
									)
								}
							}

							MinecraftPlayPacketType.ADVANCEMENT_TAB -> {
								val type = proxy.varN32()
								when (MinecraftAdvancementTabAction.entries.id(type).enum!!) {
									MinecraftAdvancementTabAction.OPENED -> {
										val string = proxy.string(32767)
										MinecraftPlayAdvancementTabOpenedPacket(
											string,
											LockedReadableChannel(proxy.buffer, fromLock, length)
										)
									}

									MinecraftAdvancementTabAction.CLOSED -> MinecraftPlayAdvancementTabClosedPacket(
										LockedReadableChannel(proxy.buffer, fromLock, length)
									)
								}
							}

							MinecraftPlayPacketType.SLOT_CHANGED -> MinecraftPlaySlotChangedPacket(
								proxy.u16(),
								LockedReadableChannel(proxy.buffer, fromLock, length)
							)

							MinecraftPlayPacketType.CREATIVE_INVENTORY -> {
								val slot = proxy.u16()
								val item = proxy.slot()
								MinecraftPlayCreativeInventoryPacket(
									slot.toInt(), item,
									LockedReadableChannel(proxy.buffer, fromLock, length)
								)
							}

							MinecraftPlayPacketType.ANIMATION_TS -> {
								val hand = proxy.varN32()
								MinecraftPlayAnimationToServerPacket(
									MinecraftHand.entries.id(hand).enum!!,
									LockedReadableChannel(proxy.buffer, fromLock, length)
								)
							}

							MinecraftPlayPacketType.USE_ITEM -> {
								val hand = proxy.varN32()
								MinecraftPlayUseItemPacket(
									MinecraftHand.entries.id(hand).enum!!,
									LockedReadableChannel(proxy.buffer, fromLock, length)
								)
							}

							else -> throw UnsupportedOperationException()
						}
					}
				}
				backlog.add(Result.success(add))
			}
		}.uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { _, t ->
			backlog.add(Result.failure(t))
		}
	}
}