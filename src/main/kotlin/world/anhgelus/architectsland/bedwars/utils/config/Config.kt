package world.anhgelus.architectsland.bedwars.utils.config

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

class Config(val main: Plugin, val name: String) {
    private val database: File = File(main.dataFolder, "$name.yml")
    private var databaseConfig: FileConfiguration? = null

    init {
        if (!database.exists()) {
            Files.createDirectories(Paths.get(main.dataFolder.toString()));
            database.createNewFile()
        }
    }

    fun fileConfig(): FileConfiguration {
        if (databaseConfig == null) {
            databaseConfig = YamlConfiguration.loadConfiguration(database)
        }
        return databaseConfig!!
    }

    fun save() {
        try {
            fileConfig().save(database)
        } catch (e: IOException) {
            main.logger.severe("Error while saving the configuration $name")
            e.printStackTrace()
        }
    }
}
