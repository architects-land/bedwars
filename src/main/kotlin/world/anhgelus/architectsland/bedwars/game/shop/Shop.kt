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
                guiItems.setItem(19, generateWool())
                guiItems.setItem(20, GuiUtils.createItem(Material.STONE_SWORD))
                guiItems.setItem(21, GuiUtils.createItem(Material.CHAINMAIL_BOOTS))
                guiItems.setItem(22, GuiUtils.createItem(Material.EGG))
                guiItems.setItem(23, GuiUtils.createItem(Material.BOW))
                guiItems.setItem(24, GuiUtils.createItem(Material.POTION))
                guiItems.setItem(25, GuiUtils.createItem(Material.TNT))
                // line 2
                guiItems.setItem(28, GuiUtils.createItem(Material.WOOD, 16))
                guiItems.setItem(29, GuiUtils.createItem(Material.IRON_SWORD))
                guiItems.setItem(30, GuiUtils.createItem(Material.IRON_BOOTS))
                guiItems.setItem(31, GuiUtils.createItem(Material.SHEARS))
                guiItems.setItem(32, GuiUtils.createItem(Material.ARROW, 6))
                guiItems.setItem(33, GuiUtils.createItem(Material.POTION))
                guiItems.setItem(34, GuiUtils.createItem(Material.WATER_BUCKET))
                // line 3
                guiItems.setItem(37, GuiUtils.createItem(Material.ENDER_STONE, 12))
                guiItems.setItem(38, GuiUtils.createItem(Material.GLASS, 4))
                guiItems.setItem(39, GuiUtils.createItem(Material.DIAMOND_BOOTS))
                guiItems.setItem(40, GuiUtils.createItem(Material.AIR))
                guiItems.setItem(41, GuiUtils.createItem(Material.FIREBALL))
                guiItems.setItem(42, GuiUtils.createItem(Material.GOLDEN_APPLE))
                guiItems.setItem(43, GuiUtils.createItem(Material.OBSIDIAN))
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
                    if (item.type in SWORDS) {
                        buy(player, team.generateSword(item.type))
                        return
                    } else if (item.type in ARMORS) {
                        if (hasMoney(player, item.type)) {
                            team.generateArmor(item.type, player)
                        } else {
                            player.sendMessage("You don't have the money to buy it")
                        }
                        return
                    }
                    throw NullPointerException("Unknow item")
                }
            }
        }

        private fun buy(player: Player, item: ItemStack) {
            if (hasMoney(player, item.type)) {
                player.inventory.addItem(item)
                return
            }
            player.sendMessage("You don't have the money to buy it")
        }

        private fun hasMoney(player: Player, item: Material): Boolean {
            val pr = PRICES[item]!!
            val price = mapOf<Material, Int>(
                Material.IRON_INGOT to pr.iron,
                Material.GOLD_INGOT to pr.gold,
                Material.DIAMOND to pr.diamond,
                Material.EMERALD to pr.emerald,
            )
            player.inventory.forEach {
                if (it.type in CURRENCIES) {
                    val p = price[it.type]!!
                    if (it.amount < p) {
                        return false
                    }
                    it.amount -= p
                    return true
                }
            }
            return false
        }
    }

    object GuiUpgradesListener: Listener {

    }
    
    companion object {
        val CURRENCIES = listOf<Material>(Material.IRON_INGOT, Material.GOLD_INGOT, Material.DIAMOND, Material.EMERALD)
        val SWORDS = listOf<Material>(Material.IRON_SWORD, Material.STONE_SWORD, Material.DIAMOND_SWORD)
        val ARMORS = listOf<Material>(Material.IRON_BOOTS, Material.CHAINMAIL_BOOTS, Material.DIAMOND_BOOTS)

        val PRICES = mapOf<Material, ShopItem.Price>(

        )
    }
}