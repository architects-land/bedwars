package world.anhgelus.architectsland.bedwars.game

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import world.anhgelus.architectsland.bedwars.team.Team
import world.anhgelus.architectsland.bedwars.team.TeamPlayer

class Shop(val team: Team) {
    private val guiItems: Inventory = Bukkit.createInventory(null, 54, Game.ITEMS_SELLER)
    private val guiUpgrades: Inventory = Bukkit.createInventory(null, 9, Game.UPGRADES_SELLER)

    private object GuiUtils {
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
        EXPLOSIVE("Explosives", ChatColor.AQUA, Material.TNT),
        BOOST("Boosts", ChatColor.AQUA, Material.GOLDEN_APPLE);

        fun generateName(): String {
            return "${color}$title"
        }

        fun setItem(inv: Inventory, place: Int) {
            inv.setItem(place, GuiUtils.createItem(material, generateName()))
        }
    }

    fun displayGuiItems(menu: GuiMenu, player: Player) {
        guiItems.setItem(1, GuiUtils.createItem(Material.STAINED_GLASS_PANE))
        GuiMenu.MAIN.setItem(guiItems, 2)
        GuiMenu.BLOCK.setItem(guiItems, 3)
        GuiMenu.COMBAT.setItem(guiItems, 4)
        GuiMenu.EXPLOSIVE.setItem(guiItems, 5)
        GuiMenu.BOOST.setItem(guiItems, 6)
        guiItems.setItem(7, GuiUtils.createItem(Material.STAINED_GLASS_PANE))
        for (i in 9..17) {
            guiItems.setItem(i, GuiUtils.createItem(Material.STAINED_GLASS_PANE))
        }
        when (menu) {
            GuiMenu.MAIN -> {

            }
            GuiMenu.BLOCK -> {
                guiItems.setItem(19, GuiUtils.createItem(Material.WOOL))
                guiItems.setItem(20, GuiUtils.createItem(Material.ENDER_STONE))
                guiItems.setItem(21, GuiUtils.createItem(Material.OBSIDIAN))
                guiItems.setItem(22, GuiUtils.createItem(Material.CLAY))
                guiItems.setItem(23, GuiUtils.createItem(Material.WOOD))
                guiItems.setItem(23, GuiUtils.createItem(Material.GLASS))
            }
            else -> throw IllegalArgumentException("Invalid menu")
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
            println(event.inventory.name)
            if(event.inventory.name.contains("container.")) return;
            event.isCancelled = true

            if (event.slot in 2..6) {
                val menu = when(event.currentItem.itemMeta.displayName) {
                    GuiMenu.MAIN.generateName() -> GuiMenu.MAIN
                    GuiMenu.BLOCK.generateName() -> GuiMenu.BLOCK
                    GuiMenu.COMBAT.generateName() -> GuiMenu.COMBAT
                    GuiMenu.EXPLOSIVE.generateName() -> GuiMenu.EXPLOSIVE
                    GuiMenu.BOOST.generateName() -> GuiMenu.BOOST
                    else -> throw NullPointerException("Impossible to find submenu")
                }
                Shop(TeamPlayer.fromPlayer(player)!!.team).displayGuiItems(menu, player)
                return
            }
        }
    }

    object GuiUpgradesListener: Listener {

    }
}