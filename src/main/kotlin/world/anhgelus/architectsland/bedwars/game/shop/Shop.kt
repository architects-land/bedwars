package world.anhgelus.architectsland.bedwars.game.shop

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.material.Wool
import world.anhgelus.architectsland.bedwars.game.Game
import world.anhgelus.architectsland.bedwars.team.Team
import world.anhgelus.architectsland.bedwars.team.TeamPlayer
import world.anhgelus.architectsland.bedwars.utils.ColorHelper

class Shop(val team: Team) {
    private val guiUpgrades: Inventory = Bukkit.createInventory(null, 9, Game.UPGRADES_SELLER)

    private object GuiUtils {
        fun createItem(material: Material, name: String, amount: Int): ItemStack {
            return setName(ItemStack(material, amount), name)
        }

        fun createItem(material: Material, amount: Int): ItemStack {
            return ItemStack(material, amount)
        }

        fun createItem(material: Material, name: String): ItemStack {
            return setName(ItemStack(material, 1), name)
        }

        fun createItem(material: Material): ItemStack {
            return ItemStack(material, 1)
        }

        fun setName(item: ItemStack, name:String): ItemStack {
            val meta = item.itemMeta
            meta.displayName = name
            item.itemMeta = meta

            return item
        }
    }

    enum class GuiMenu(val title: String, val color: ChatColor, val material: Material) {
        MAIN("", ChatColor.AQUA, Material.NETHER_STAR),
        BLOCK("Blocks", ChatColor.AQUA, Material.WOOL),
        COMBAT("Combats", ChatColor.AQUA, Material.DIAMOND_SWORD),
        MISC("Miscellaneous", ChatColor.AQUA, Material.TNT),
        BOOST("Boosts", ChatColor.AQUA, Material.GOLDEN_APPLE);

        fun generateName(): String {
            return "${color}$title"
        }

        fun setItem(inv: Inventory, place: Int) {
            inv.setItem(place, GuiUtils.createItem(material, generateName()))
        }
    }

    private fun generateWool(): ItemStack {
        val wool = ItemStack(Material.WOOL, 16)
        val data = wool.data as Wool
        data.color = ColorHelper.chatColorToDyeColor[team.color]
        wool.data = data
        return wool
    }

    fun displayGuiItems(menu: GuiMenu, player: Player) {
        val guiItems: Inventory = Bukkit.createInventory(null, 54, Game.ITEMS_SELLER + menu.title)

        guiItems.setItem(1, GuiUtils.createItem(Material.STAINED_GLASS_PANE))
        GuiMenu.MAIN.setItem(guiItems, 2)
        GuiMenu.BLOCK.setItem(guiItems, 3)
        GuiMenu.COMBAT.setItem(guiItems, 4)
        GuiMenu.MISC.setItem(guiItems, 5)
        GuiMenu.BOOST.setItem(guiItems, 6)
        guiItems.setItem(7, GuiUtils.createItem(Material.STAINED_GLASS_PANE))
        for (i in 9..17) {
            guiItems.setItem(i, GuiUtils.createItem(Material.STAINED_GLASS_PANE))
        }
        when (menu) {
            GuiMenu.MAIN -> {
                // line 1
                guiItems.setItem(19, ShopItem.WOOL.updateWool(team))
                guiItems.setItem(20, ShopItem.STONE_SWORD.item())
                guiItems.setItem(21, ShopItem.CHAINMAIL_ARMOR.item())
                guiItems.setItem(22, ShopItem.BRIDGE_EGG.item())
                guiItems.setItem(23, ShopItem.BOW_LVL_1.item())
                guiItems.setItem(24, ShopItem.INVISIBILITY_POTION.item())
                guiItems.setItem(25, ShopItem.TNT.item())
                // line 2
                guiItems.setItem(28, ShopItem.WOOD.item())
                guiItems.setItem(29, ShopItem.IRON_SWORD.item())
                guiItems.setItem(30, ShopItem.IRON_ARMOR.item())
                guiItems.setItem(31, GuiUtils.createItem(Material.SHEARS))
                guiItems.setItem(32, ShopItem.ARROW.item())
                guiItems.setItem(33, ShopItem.JUMP_BOOST_POTION.item())
                guiItems.setItem(34, ShopItem.WATER_BUCKET.item())
                // line 3
                guiItems.setItem(37, ShopItem.ENDER_STONE.item())
                guiItems.setItem(38, ShopItem.GLASS.item())
                guiItems.setItem(39, ShopItem.DIAMOND_ARMOR.item())
                guiItems.setItem(40, GuiUtils.createItem(Material.AIR))
                guiItems.setItem(41, ShopItem.FIREBALL.item())
                guiItems.setItem(42, ShopItem.GOLDEN_APPLE.item())
                guiItems.setItem(43, ShopItem.OBSIDIAN.item())
            }
            else -> ShopItem.itemsOf(menu).forEach { it.placeInInventory(guiItems, team) }
        }
        // open gui for player
        player.openInventory(guiItems)
    }

    fun displayGuiUpgrades(player: Player) {

        // open gui for player
        player.openInventory(guiUpgrades)
    }

    /* LISTENERS */

    object GuiItemsListener: Listener {
        @EventHandler
        fun onClick(event: InventoryClickEvent) {
            val player: Player = event.whoClicked as Player
            if(event.inventory.name.contains("container.")) return;
            event.isCancelled = true

            val team = TeamPlayer.fromPlayer(player)!!.team
            val shop = Shop(team)

            val item = event.currentItem

            if (event.slot in 2..6) {
                val menu = when(item.itemMeta.displayName) {
                    GuiMenu.MAIN.generateName() -> GuiMenu.MAIN
                    GuiMenu.BLOCK.generateName() -> GuiMenu.BLOCK
                    GuiMenu.COMBAT.generateName() -> GuiMenu.COMBAT
                    GuiMenu.MISC.generateName() -> GuiMenu.MISC
                    GuiMenu.BOOST.generateName() -> GuiMenu.BOOST
                    else -> throw NullPointerException("Impossible to find submenu")
                }
                shop.displayGuiItems(menu, player)
                return
            }

            when(item.type) {
                Material.WOOL -> buy(player, shop.generateWool())
                else -> {
                    val shopItem = ShopItem.from(item)
                    if (shopItem in ShopItem.SWORDS) {
                        buy(player, team.generateSword(item.type))
                        return
                    } else if (shopItem in ShopItem.ARMORS) {
                        if (shopItem.hasMoney(player)) {
                            team.generateArmor(item.type, player)
                        } else {
                            player.sendMessage("You don't have the money to buy it")
                        }
                        return
                    }
                    throw NullPointerException("Unknown item")
                }
            }
        }

        private fun buy(player: Player, item: ItemStack) {
            val shopItem = ShopItem.from(item)
            if (shopItem.hasMoney(player)) {
                player.inventory.addItem(item)
                return
            }
            player.sendMessage("You don't have the money to buy it")
        }
    }

    object GuiUpgradesListener: Listener {

    }
}