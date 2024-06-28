package world.anhgelus.architectsland.bedwars.utils

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object TitleGenerator {

    data class Part(val content: String, val color: ChatColor)

    fun sendTitle(player: Player, title: Part, subtitle: Part?) {
        val server = Bukkit.getServer()
        val console = server.consoleSender
        server.dispatchCommand(console, "title ${player.displayName} times 20 40 20")
        server.dispatchCommand(
            console,
            "title ${player.displayName} title {\"text\":\"${title.content}\",\"color\":\"${title.color.name.lowercase()}\"}"
        )
        if (subtitle == null) return
        server.dispatchCommand(
            console,
            "title ${player.displayName} subtitle {\"text\":\"${subtitle.content}\",\"color\":\"${subtitle.color.name.lowercase()}\"}"
        )
    }

    fun sendTitle(player: Player, title: Part) {
        sendTitle(player, title, null)
    }
}