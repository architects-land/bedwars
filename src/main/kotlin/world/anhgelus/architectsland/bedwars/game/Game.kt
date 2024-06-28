package world.anhgelus.architectsland.bedwars.game

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Item
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import world.anhgelus.architectsland.bedwars.Bedwars
import world.anhgelus.architectsland.bedwars.team.Team

class Game(
    val teams: MutableSet<Team>,
) {
    init {
        instance = this
    }

    private var taskId: Int? = null

    private var t = 0

    fun start() {
        teams.forEach { team ->
            team.players.forEach { player ->
                player.teleport(team.respawnLoc)
            }
        }
        // start "eachSecond"
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Bedwars.instance, {
            eachSecond()
        }, 0L, 10L)
    }

    fun stop() {
        // stop task "eachSecond"
        Bukkit.getScheduler().cancelTask(taskId!!)
    }

    private fun eachSecond() {
        teams.forEach { team ->
            val loc = team.generatorLoc!!
            val nears = loc.world.getNearbyEntities(loc, 2.0, 2.0, 2.0)
            var c = 0
            nears.forEach {
                if (it.type == EntityType.DROPPED_ITEM) {
                    c += (it as Item).itemStack.amount
                }

            }
            if (c > 64) {
                return@forEach
            }
            val lvl = team.upgrade.forge.level
            when (lvl) {
                0 -> {
                    drop(loc, Material.IRON_INGOT)
                    if (t%4 == 3) {
                        drop(loc, Material.GOLD_INGOT)
                    }
                }
                1 -> {
                    drop(loc, Material.IRON_INGOT)
                    if (t%4 == 1) {
                        drop(loc, Material.IRON_INGOT)
                    } else if (t%4 == 3) {
                        drop(loc, Material.GOLD_INGOT)
                        drop(loc, Material.IRON_INGOT)
                    }
                    if (t%8 == 7) {
                        drop(loc, Material.GOLD_INGOT)
                    }
                }
                2 -> {
                    drop(loc, Material.IRON_INGOT, 2)
                    if (t%2 == 1) {
                        drop(loc, Material.GOLD_INGOT)
                    }
                    if (t%60 == 0) {
                        drop(loc, Material.EMERALD)
                    }
                }
                3 -> {
                    drop(loc, Material.IRON_INGOT, 4)
                    drop(loc, Material.GOLD_INGOT)
                    if (t%30 == 0) {
                        drop(loc, Material.EMERALD)
                    }
                }
            }
        }
        t++
    }

    private fun drop(loc: Location, material: Material) {
        drop(loc, material, 1)
    }

    private fun drop(loc: Location, material: Material, amount: Int) {
        val stack = ItemStack(material)
        stack.amount = amount
        loc.world.dropItem(loc, stack).velocity = Vector()
    }

    companion object {
        lateinit var instance: Game
            private set
    }
}