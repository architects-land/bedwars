package world.anhgelus.architectsland.bedwars.game

import net.minecraft.server.v1_8_R3.NBTTagCompound
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftVillager
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Item
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Villager
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import world.anhgelus.architectsland.bedwars.Bedwars
import world.anhgelus.architectsland.bedwars.team.Team
import java.lang.reflect.Field

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

            //@TODO Create own entity type for npc

            val tag: NBTTagCompound = NBTTagCompound();

            val seller = team.itemSellerLoc!!.world.spawnEntity(team.itemSellerLoc, EntityType.VILLAGER);
            seller.customName = "Pomme"
            seller.isCustomNameVisible = true
            (seller as CraftVillager).handle.k(true) // NoAI
            seller.handle.e(tag) // add tag
            tag.setBoolean("Invulnerable", true);
            seller.handle.f(tag) //apply tag
            seller.handle.b(true) // silent
            seller.canPickupItems = false
            seller.

        }
        // start "eachSecond"
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Bedwars.instance, {
            eachSecond()
        }, 0L, 10L)
    }

    fun stop() {
        // stop task "eachSecond"
        teams.forEach { team ->
            team.itemSellerLoc!!.world.getNearbyEntities(team.itemSellerLoc, 1.0, 1.0, 1.0).forEach { entity -> (entity as LivingEntity).health = 0.0}
        }

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