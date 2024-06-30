package world.anhgelus.architectsland.bedwars.utils

import net.minecraft.server.v1_8_R3.IChatBaseComponent
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player

object TitleGenerator {

    data class Part(val content: String, val color: ChatColor)

    fun sendTitle(player: Player, title: Part, subtitle: Part?) {
        val server = Bukkit.getServer()
        val console = server.consoleSender

        var chatSerialize: IChatBaseComponent = ChatSerializer.a("{\"text\": \"${title.content}\", \"color\": \"${title.color.name.lowercase()}\"}")

        val sendedTitle: PacketPlayOutTitle = PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, chatSerialize)
        val length: PacketPlayOutTitle = PacketPlayOutTitle(5, 20 ,5)

        (player as CraftPlayer).handle.playerConnection.sendPacket(sendedTitle)
        player.handle.playerConnection.sendPacket(length)

        if (subtitle == null) return
        chatSerialize = ChatSerializer.a("\"{\\\"text\\\": \\\"${subtitle.content}\\\", \\\"color\\\": \\\"${subtitle.color.name.lowercase()}\\\"}\"")

        val sendedSubTitle: PacketPlayOutTitle = PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, chatSerialize)

        player.handle.playerConnection.sendPacket(sendedSubTitle)
    }

    fun sendTitle(player: Player, title: Part) {
        sendTitle(player, title, null)
    }
}