package org.bread_experts_group.protocol.old.minecraft.server

import org.bread_experts_group.channel.skip
import org.bread_experts_group.logging.ColoredHandler
import org.bread_experts_group.numeric.geometry.Point3D
import org.bread_experts_group.protocol.old.minecraft.MinecraftConnection
import org.bread_experts_group.protocol.old.minecraft.MinecraftConnectionState
import org.bread_experts_group.protocol.old.minecraft.packet.MinecraftPacket
import org.bread_experts_group.protocol.old.minecraft.packet.login.MinecraftLoginDisconnectPacket
import org.bread_experts_group.protocol.old.minecraft.packet.login.MinecraftLoginStartPacket
import org.bread_experts_group.protocol.old.minecraft.packet.login.MinecraftLoginSuccessPacket
import org.bread_experts_group.protocol.old.minecraft.packet.play.*
import org.bread_experts_group.protocol.old.minecraft.packet.status.MinecraftStatusPingPacket
import org.bread_experts_group.protocol.old.minecraft.packet.status.MinecraftStatusPongPacket
import org.bread_experts_group.protocol.old.minecraft.packet.status.MinecraftStatusRequestPacket
import org.bread_experts_group.protocol.old.minecraft.packet.status.MinecraftStatusResponsePacket
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.ByteBuffer
import java.nio.channels.ByteChannel
import java.nio.channels.SeekableByteChannel
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class MinecraftStandardServer(
	val banner: MinecraftStatusResponsePacket
) {
	private val client: HttpClient = HttpClient.newHttpClient()
	private val players: MutableMap<UUID, MinecraftConnectedPlayer> = mutableMapOf()
	private val logger: Logger = ColoredHandler.newLogger("tmp")
	fun handle(channel: ByteChannel): Thread? {
		var localKeepAlive: Thread? = null
		val buffer = ByteBuffer.allocateDirect(8192)
		val packetMailbox = LinkedBlockingQueue<MinecraftPacket>()
		val localMailbox: Thread = Thread.ofVirtual().start {
			try {
				while (!Thread.currentThread().isInterrupted) {
					val next = packetMailbox.take()
					context(buffer, channel) { next.write() }
				}
			} catch (_: InterruptedException) {
			}
		}

		fun MinecraftPacket.submit() = packetMailbox.put(this)
		var uuid: UUID? = null
		lateinit var entry: MinecraftConnectedPlayer

		val connectionThread = Thread.ofVirtual().start {
			val localRandom = Random()
			val connection = MinecraftConnection(channel)
			while (localKeepAlive == null || localKeepAlive?.isInterrupted == false) {
				val next = connection.next().getOrThrow()
				when (next) {
					is MinecraftStatusRequestPacket -> {
						(banner.data as SeekableByteChannel).position(0)
						banner.submit()
					}

					is MinecraftStatusPingPacket -> MinecraftStatusPongPacket(next.long).submit()

					is MinecraftLoginStartPacket -> {
						val usernameBase = "https://api.mojang.com/users/profiles/minecraft/"
						val usernameResponse = client.send(
							HttpRequest.newBuilder()
								.uri(URI("$usernameBase${next.username}"))
								.build(),
							HttpResponse.BodyHandlers.ofInputStream()
						)
						val playerResult = MinecraftPlayerResult.parse(usernameResponse.body())
						if (players.containsKey(uuid)) {
							MinecraftLoginDisconnectPacket("[$uuid / ${playerResult.name}] is already in-game")
								.submit()
							break
						}
						val profileBase = "https://sessionserver.mojang.com/session/minecraft/profile/"
						val profileResponse = client.send(
							HttpRequest.newBuilder()
								.uri(URI("$profileBase${playerResult.id}?unsigned=false"))
								.build(),
							HttpResponse.BodyHandlers.ofInputStream()
						)
						val profileResult = MinecraftPlayerProfileResult.parse(profileResponse.body())
						entry = MinecraftConnectedPlayer(profileResult, packetMailbox)
						uuid = UUID.nameUUIDFromBytes(playerResult.id.hexToByteArray())
						players[uuid] = entry
						MinecraftLoginSuccessPacket(uuid, playerResult.name).submit()
						connection.connectionState = MinecraftConnectionState.PLAY
						val addPlayers = mutableMapOf<UUID, MinecraftPlayerListAddEntry>()
						val localEntry = entry.playerListEntry()
						players.forEach { (remoteUUID, remoteEntry) ->
							if (remoteUUID == uuid) addPlayers[remoteUUID] = localEntry
							else {
								addPlayers[remoteUUID] = remoteEntry.playerListEntry()
								remoteEntry.packetMailbox.add(
									MinecraftPlayPlayerListAddPacket(mapOf(uuid to localEntry))
								)
							}
							remoteEntry.packetMailbox.add(
								MinecraftPlayChatMessageToClientPacket(
									"${profileResult.name} has joined the game",
									MinecraftChatMessagePosition.SYSTEM_MESSAGE
								)
							)
						}
						MinecraftPlayPlayerListAddPacket(addPlayers).submit()
						localKeepAlive = Thread.ofVirtual().start {
							try {
								while (!Thread.currentThread().isInterrupted) {
									entry.lastPing = System.nanoTime().toDuration(DurationUnit.NANOSECONDS)
									MinecraftPlayKeepAlivePacket(localRandom.nextLong()).submit()
									Thread.sleep(5000)
								}
							} catch (_: InterruptedException) {
							}
						}
						MinecraftPlayJoinGamePacket(
							localRandom.nextInt(),
							MinecraftGameMode.CREATIVE,
							false,
							0,
							MinecraftDifficulty.PEACEFUL,
							"default"
						).submit()
						MinecraftPlayChunkDataPacket(
							0,
							0,
							true,
							1,
							byteArrayOf(0x00),
							emptyList()
						).submit()
						MinecraftPlayPlayerPositionLookToClientPacket(
							Point3D(0.0, 100.0, 0.0),
							0f, 0f, 0
						).submit()
					}

					is MinecraftPlayPluginMessageToServerPacket -> next.data.skip()

					is MinecraftPlayChatMessageToServerPacket -> players.forEach { (_, remoteEntry) ->
						remoteEntry.packetMailbox.add(
							MinecraftPlayChatMessageToClientPacket(
								"[${entry.profile.name}] ${next.message}",
								MinecraftChatMessagePosition.CHAT
							)
						)
					}

					is MinecraftPlayKeepAliveReplyPacket -> {
						if (uuid != null) {
							val time = (System.nanoTime().toDuration(DurationUnit.NANOSECONDS) - entry.lastPing)
							entry.lastPing = time
							players.forEach { (_, entry) ->
								entry.packetMailbox.add(
									MinecraftPlayPlayerListLatencyPacket(
										mapOf(uuid to time.toInt(DurationUnit.MILLISECONDS))
									)
								)
							}
						}
					}
				}
				next.data.skip()
			}
		}
		connectionThread.uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { _, e ->
			if (e !is IOException) logger.log(Level.SEVERE, e) { "Failure" }
			localKeepAlive?.interrupt()
			localMailbox.interrupt()
			if (uuid != null) {
				players.remove(uuid)
				players.forEach { (_, remoteEntry) ->
					remoteEntry.packetMailbox.add(MinecraftPlayPlayerListRemovePacket(listOf(uuid)))
					remoteEntry.packetMailbox.add(
						MinecraftPlayChatMessageToClientPacket(
							"${entry.profile.name} has left the game",
							MinecraftChatMessagePosition.SYSTEM_MESSAGE
						)
					)
				}
			}
		}
		return connectionThread
	}
}