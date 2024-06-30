package world.anhgelus.architectsland.bedwars.utils

import net.minecraft.server.v1_8_R3.NBTTagCompound
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftVillager
import org.bukkit.entity.EntityType

object Villager {

    fun Create(loc: Location?, name: String)
    {
        val tag: NBTTagCompound = NBTTagCompound();

        val seller = loc!!.world.spawnEntity(loc, EntityType.VILLAGER);
        seller.customName = name
        seller.isCustomNameVisible = true
        (seller as CraftVillager).handle.k(true) // NoAI
        seller.handle.e(tag) // add tag
        tag.setBoolean("Invulnerable", true);
        seller.handle.f(tag) //apply tag
        seller.handle.b(true) // silent
        seller.canPickupItems = false
    }

    fun Create(loc: Location)
    {
        Villager.Create(loc, "NPC")
    }
}