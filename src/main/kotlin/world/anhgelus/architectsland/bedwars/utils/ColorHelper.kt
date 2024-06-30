package world.anhgelus.architectsland.bedwars.utils

import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.DyeColor

object ColorHelper {
    val chatColorToColor = mapOf<ChatColor, Color>(
        ChatColor.AQUA to Color.AQUA,
        ChatColor.YELLOW to Color.YELLOW,
        ChatColor.RED to Color.RED,
        ChatColor.GREEN to Color.GREEN,
        ChatColor.BLUE to Color.BLUE,
        ChatColor.GRAY to Color.GRAY,
        ChatColor.LIGHT_PURPLE to Color.FUCHSIA,
        ChatColor.DARK_PURPLE to Color.PURPLE,
    )

    val chatColorToDyeColor = mapOf<ChatColor, DyeColor>(
        ChatColor.AQUA to DyeColor.CYAN,
        ChatColor.YELLOW to DyeColor.YELLOW,
        ChatColor.RED to DyeColor.RED,
        ChatColor.GREEN to DyeColor.GREEN,
        ChatColor.BLUE to DyeColor.BLUE,
        ChatColor.GRAY to DyeColor.GRAY,
        ChatColor.LIGHT_PURPLE to DyeColor.PINK,
        ChatColor.DARK_PURPLE to DyeColor.PURPLE,
    )
}