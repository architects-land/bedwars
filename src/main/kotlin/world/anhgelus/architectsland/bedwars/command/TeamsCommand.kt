package world.anhgelus.architectsland.bedwars.command

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import world.anhgelus.architectsland.bedwars.game.Game
import world.anhgelus.architectsland.bedwars.team.Team
import world.anhgelus.architectsland.bedwars.utils.config.ConfigAPI

object TeamsCommand : CommandExecutor, TabCompleter {
    override fun onCommand(
        sender: CommandSender?,
        command: Command?,
        label: String?,
        args: Array<out String>?
    ): Boolean {
        if (label != "teams" || args?.size != 3) return false

        val action = args[0]
        if (action != "add" && action != "remove") return false

        val team = Team.loadFromConfig(ConfigAPI.config(ConfigAPI.teamConfigFile).fileConfig(), args[1])
        if (team == null) {
            sender!!.sendMessage("Team ${args[1]} not found. Did you used /bedwars to set locations?")
            return true
        }

        val player = Bukkit.getPlayer(args[2])
        if (player == null) {
            sender!!.sendMessage("Player ${args[2]} not found")
            return true
        }
        // [add/remove] [teams] [player]
        when (action) {
            "add" -> {
                team.players.add(player)
                Game.instance.teams.add(team)
            }
            "remove" -> {
                team.players.remove(player)
                if (team.players.isEmpty()) {
                    Game.instance.teams.remove(team)
                }
            }
        }
        sender!!.sendMessage("${args[2]} $action to ${team.teamName}")
        return true
    }

    override fun onTabComplete(
        sender: CommandSender?,
        command: Command?,
        alias: String?,
        args: Array<out String>?
    ): MutableList<String>? {
        val list = mutableListOf<String>()
        when (args?.size) {
            1 -> {
                list.add("add")
                list.add("remove")
            }
            2 -> {
                Team.entries.forEach {
                    list.add(it.teamName)
                }
            }
            3 -> {
                return null;
            }
        }
        return list
    }
}