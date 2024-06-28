package world.anhgelus.architectsland.bedwars.command

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import world.anhgelus.architectsland.bedwars.team.Team
import world.anhgelus.architectsland.bedwars.utils.config.ConfigAPI

object BedwarsCommand : CommandExecutor, TabCompleter {

    val locations = listOf(
        "respawn","bed","generator","items-seller","upgrades-seller"
    )

    override fun onCommand(
        sender: CommandSender?,
        command: Command?,
        label: String?,
        args: Array<out String>?
    ): Boolean {
        if (label != "bedwars" || args!!.size != 2 || sender !is Player) return false

        val team = Team.loadFromConfig(ConfigAPI.config(ConfigAPI.teamConfigFile).fileConfig(), args[0])
            ?: try {
            Team.entries.first {
                it.name.equals(args[0], true)
            }
        } catch (e: NoSuchElementException) {
            sender.sendMessage("Team ${args[0]} not found")
            return true
        }

        val location = args[1]
        if (location !in locations) {
            return false
        }

        when (location) {
            "respawn" -> {
                team.respawnLoc = sender.location
            }
            "bed" -> {
                team.bedLoc = sender.location
            }
            "generator" -> {
                team.generatorLoc = sender.location
                team.generatorLoc!!.y += 0.3
            }
            "items-seller" -> {
                team.itemSellerLoc = sender.location
            }
            "upgrades-seller" -> {
                team.upgradeSellerLoc = sender.location
            }
        }

        val config = ConfigAPI.config("teams")
        team.setInConfig(config.fileConfig())
        config.save()

        sender.sendMessage("Location updated.")
        return true
    }

    override fun onTabComplete(
        sender: CommandSender?,
        command: Command?,
        alias: String?,
        args: Array<out String>?
    ): MutableList<String> {
        val list = mutableListOf<String>()
        when (args?.size) {
            1 -> {
                Team.entries.forEach {
                    list.add(it.teamName)
                }
            }
            2 -> {
                locations.forEach {
                    list.add(it)
                }
            }
        }
        return list
    }
}