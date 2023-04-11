package com.github.srain3.sep

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.IOException
import java.util.logging.Level

object Tools {
    lateinit var pl: SEP

    /**
     * Minecraftカラーコードへ(&を)変換
     */
    fun String.color(): String {
        return ChatColor.translateAlternateColorCodes('&', this)
    }

    /**
     * パス名にあるConfigファイルを(無ければ空で)渡してくれる
     */
    fun String.getYaml(): FileConfiguration {
        val file = File(pl.dataFolder,this)
        return YamlConfiguration.loadConfiguration(file)
    }

    /**
     * パス名のファイル存在を確認する
     * @return ファイルが有れば:true 無ければ:false
     */
    fun String.checkYaml(): Boolean {
        val file = File(pl.dataFolder,this)
        if (file.exists()) {
            return file.isFile
        }
        return false
    }

    /**
     * パス名の場所へConfigファイルを保存してくれる
     */
    fun String.saveYaml(config: FileConfiguration) {
        try {
            config.save(File(pl.dataFolder,this))
        } catch (ex: IOException) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save config", ex)
        }
        return
    }

    /**
     * パス名のConfigファイルを消去してくれる
     * @return 消去した場合:true 出来なかった場合:false
     */
    fun String.removeYaml(): Boolean {
        val file = File(pl.dataFolder,this)
        if (file.exists()) {//存在するファイルの場合
            file.delete()
            return true
        }
        return false
    }
}