package world.anhgelus.architectsland.bedwars.game.shop

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.material.Wool
import org.bukkit.potion.PotionEffectType
import world.anhgelus.architectsland.bedwars.team.Team
import world.anhgelus.architectsland.bedwars.utils.ColorHelper
import java.util.stream.Stream


enum class ShopItem(
    val item: ItemStack,
    val price: Price,
    val menu: Shop.GuiMenu,
    val place: Int,
) {
    // blocks
    WOOL(ItemStack(Material.WOOL, 16), Price(4), Shop.GuiMenu.BLOCK, 19),
    HARD_CLAY(ItemStack(Material.HARD_CLAY, 16), Price(0), Shop.GuiMenu.BLOCK, 20),
    GLASS(ItemStack(Material.GLASS, 4), Price(24), Shop.GuiMenu.BLOCK, 21),
    ENDER_STONE(ItemStack(Material.GLASS, 12), Price(16), Shop.GuiMenu.BLOCK, 22),
    LADDER(ItemStack(Material.LADDER, 8), Price(0), Shop.GuiMenu.BLOCK, 23),
    WOOD(ItemStack(Material.WOOD, 16), Price(0, 4), Shop.GuiMenu.BLOCK, 24),
    OBSIDIAN(ItemStack(Material.OBSIDIAN, 4), Price(0, 0, 0, 4), Shop.GuiMenu.BLOCK, 25),
    // combat
        // line 1
    STONE_SWORD(ItemStack(Material.STONE_SWORD), Price(10), Shop.GuiMenu.COMBAT, 19),
    IRON_SWORD(ItemStack(Material.IRON_SWORD), Price(0, 7), Shop.GuiMenu.COMBAT, 20),
    DIAMOND_SWORD(ItemStack(Material.DIAMOND_SWORD), Price(0, 0, 0, 4), Shop.GuiMenu.COMBAT, 21),
    KB_STICK(ItemStack(Material.STICK), Price(0), Shop.GuiMenu.COMBAT, 22),
        // line 2
    CHAINMAIL_ARMOR(ItemStack(Material.CHAINMAIL_BOOTS), Price(40), Shop.GuiMenu.COMBAT, 28),
    IRON_ARMOR(ItemStack(Material.IRON_BOOTS), Price(0, 12), Shop.GuiMenu.COMBAT, 29),
    DIAMOND_ARMOR(ItemStack(Material.DIAMOND_BOOTS), Price(0, 0, 0, 6), Shop.GuiMenu.COMBAT, 30),
        // line 3
    ARROW(ItemStack(Material.ARROW, 6), Price(0, 2), Shop.GuiMenu.COMBAT, 37),
    BOW_LVL_1(Creator.createBow(1, 0), Price(0), Shop.GuiMenu.COMBAT, 38),
    BOW_LVL_2(Creator.createBow(2, 0), Price(0), Shop.GuiMenu.COMBAT, 39),
    BOW_LVL_3(Creator.createBow(2, 1), Price(0), Shop.GuiMenu.COMBAT, 40),
    // boosts
    SPEED_POTION(Creator.createPotion(PotionEffectType.SPEED, 45, 0), Price(0), Shop.GuiMenu.BOOST, 19),
    JUMP_BOOST_POTION(Creator.createPotion(PotionEffectType.JUMP, 45, 0), Price(0), Shop.GuiMenu.BOOST, 20),
    INVISIBILITY_POTION(Creator.createPotion(PotionEffectType.INVISIBILITY, 45, 0), Price(0), Shop.GuiMenu.BOOST, 21),
    // misc
        // line 1
    GOLDEN_APPLE(ItemStack(Material.GOLDEN_APPLE), Price(0, 3), Shop.GuiMenu.MISC, 19),
    SNOW_BALL(ItemStack(Material.SNOW_BALL), Price(0), Shop.GuiMenu.MISC, 20),
    MONSTER_EGG(ItemStack(Material.MONSTER_EGG), Price(0), Shop.GuiMenu.MISC, 21),
    FIREBALL(ItemStack(Material.FIREBALL), Price(0), Shop.GuiMenu.MISC, 22),
    TNT(ItemStack(Material.TNT), Price(0, 4), Shop.GuiMenu.MISC, 23),
    ENDER_PEARL(ItemStack(Material.ENDER_PEARL), Price(0), Shop.GuiMenu.MISC, 24),
    WATER_BUCKET(ItemStack(Material.WATER_BUCKET), Price(0), Shop.GuiMenu.MISC, 25),
        // line 2
    BRIDGE_EGG(ItemStack(Material.EGG), Price(0, 0, 0, 2), Shop.GuiMenu.MISC, 28),
    MILK_BUCKET(ItemStack(Material.MILK_BUCKET), Price(0), Shop.GuiMenu.MISC, 29),
    SPONGE(ItemStack(Material.SPONGE, 4), Price(0), Shop.GuiMenu.MISC, 30);


    data class Price(val iron: Int, val gold: Int, val diamond: Int, val emerald: Int) {
        constructor(iron: Int) : this(iron, 0, 0, 0)
        constructor(iron: Int, gold: Int) : this(iron, gold, 0, 0)
    }

    fun type(): Material { return item.type }

    fun placeInInventory(inventory: Inventory, team: Team) {
        inventory.setItem(place, if (item.type == Material.WOOL) {
            updateWool(team)
        } else {
            item
        })
    }

    fun updateWool(team: Team): ItemStack {
        if (item.type != Material.WOOL) throw IllegalArgumentException("${item.type} is not a wool, u r dumb, right?")
        val item = item.clone()
        val data = item.data as Wool
        data.color = ColorHelper.chatColorToDyeColor[team.color]
        item.data = data
        return item
    }

    private object Creator {
        fun createBow(powerLvl: Int, punchLvl: Int): ItemStack {
            val item = ItemStack(Material.BOW)
            item.addEnchantment(Enchantment.ARROW_DAMAGE, powerLvl)
            item.addEnchantment(Enchantment.ARROW_KNOCKBACK, punchLvl)
            return item
        }

        fun createPotion(type: PotionEffectType, duration: Int, level: Int): ItemStack {
            val item = ItemStack(Material.POTION)
            val meta = item.itemMeta as PotionMeta
            meta.setMainEffect(type)
            item.itemMeta = meta
            return item
        }
    }

    companion object {
        fun itemsOf(menu: Shop.GuiMenu): Stream<ShopItem> {
            return entries.stream().filter { it.menu == menu }
        }

        fun itemsOf(type: Material): Stream<ShopItem> {
            return entries.stream().filter { it.type() == type }
        }
    }
}