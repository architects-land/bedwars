package world.anhgelus.architectsland.bedwars.team

import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.material.Bed
import world.anhgelus.architectsland.bedwars.Bedwars
import world.anhgelus.architectsland.bedwars.utils.LocationHelper

enum class Team(
    val teamName: String,
    val color: ChatColor
) {
    RED("Red", ChatColor.RED),
    GREEN("Green", ChatColor.GREEN),
    BLUE("Blue", ChatColor.BLUE),
    YELLOW("Yellow", ChatColor.YELLOW),
    AQUA("Aqua", ChatColor.AQUA),
    GREY("Gray", ChatColor.GRAY),
    PINK("Pink", ChatColor.LIGHT_PURPLE),
    PURPLE("Purple", ChatColor.DARK_PURPLE);

    var hasBed: Boolean = true
        private set
    val players: MutableSet<Player> = mutableSetOf()

    var respawnLoc: Location? = null
    var bedLoc: Location? = null
    var generatorLoc: Location? = null

    var itemSellerLoc: Location? = null
    var upgradeSellerLoc: Location? = null

    val upgrade: TeamUpgrade = TeamUpgrade()

    fun lostBed() {
        hasBed = false
        players.forEach {
            @Suppress("DEPRECATION")
            it.sendTitle("${ChatColor.RED}Bed destroyed!", "")
        }
    }

    fun canRespawn(): Boolean {
        return hasBed
    }

    fun setInConfig(s: ConfigurationSection) {
        val section = s.getConfigurationSection(this.teamName.lowercase()) ?: generateSection(s)
        section.set("color", this.color.name)
        section.set("name", this.teamName)
        if (this.respawnLoc != null)
            LocationHelper.setInConfig(this.respawnLoc!!, section.getConfigurationSection("location.respawn"))
        if (this.bedLoc != null)
            LocationHelper.setInConfig(this.bedLoc!!, section.getConfigurationSection("location.bed"))
        if (this.generatorLoc != null)
            LocationHelper.setInConfig(this.generatorLoc!!, section.getConfigurationSection("location.generator"))
        if (this.itemSellerLoc != null)
            LocationHelper.setInConfig(this.itemSellerLoc!!, section.getConfigurationSection("location.seller.item"))
        if (this.upgradeSellerLoc != null)
            LocationHelper.setInConfig(this.upgradeSellerLoc!!, section.getConfigurationSection("location.seller.upgrade"))
    }

    private fun generateSection(s: ConfigurationSection): ConfigurationSection {
        val section = s.createSection(this.teamName.lowercase())
        section.createSection("location.respawn")
        section.createSection("location.bed")
        section.createSection("location.generator")
        section.createSection("location.seller.item")
        section.createSection("location.seller.upgrade")
        return section
    }

    private fun updateLocation(respawnLoc: Location, bedLoc: Location, generatorLoc: Location, itemSellerLoc: Location, upgradeSellerLoc: Location) {
        this.respawnLoc = respawnLoc
        this.bedLoc = bedLoc
        this.generatorLoc = generatorLoc
        this.itemSellerLoc = itemSellerLoc
        this.upgradeSellerLoc = upgradeSellerLoc
    }

    companion object {
        fun loadFromConfig(s: ConfigurationSection, name: String): Team? {
            if (!s.isConfigurationSection(name)) return null
            val section = s.getConfigurationSection(name.lowercase())

            val color = ChatColor.valueOf(section.getString("color")!!)
            val teamName = section.getString("name")
            // location
            val respawnLoc = LocationHelper.loadFromConfig(section.getConfigurationSection("location.respawn"))
            val bedLoc = LocationHelper.loadFromConfig(section.getConfigurationSection("location.bed"))
            val generatorLoc = LocationHelper.loadFromConfig(section.getConfigurationSection("location.generator"))
            val itemSellerLoc =
                LocationHelper.loadFromConfig(section.getConfigurationSection("location.seller.item"))
            val upgradeSellerLoc =
                LocationHelper.loadFromConfig(section.getConfigurationSection("location.seller.upgrade"))

            return try {
                val f = entries.first {
                    it.color == color && it.name.equals(teamName, true)
                }
                f.updateLocation(respawnLoc, bedLoc, generatorLoc, itemSellerLoc, upgradeSellerLoc)
                f
            } catch (e: NoSuchElementException) {
                e.printStackTrace()
                null
            }
        }

        fun getFromBedLocation(location: Location): Team? {
            return try {
                entries.first {
                    it.bedLoc == location
                }
            } catch (e: NoSuchElementException) {
                null
            }
        }
    }
}