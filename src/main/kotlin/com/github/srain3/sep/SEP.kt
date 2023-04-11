package com.github.srain3.sep

import com.github.srain3.sep.Tools.getYaml
import com.github.srain3.sep.Tools.pl
import com.github.srain3.sep.treasure.TreasureClickEvent
import com.github.srain3.sep.treasure.TreasureCommands
import com.github.srain3.sep.treasure.TreasureCommands.settingFile
import org.bukkit.plugin.java.JavaPlugin

class SEP : JavaPlugin() {
    override fun onEnable() {
        pl = this
        settingFile = "setting.yml".getYaml()
        server.pluginManager.registerEvents(TreasureClickEvent, this)
        getCommand("treasure")?.setExecutor(TreasureCommands)
    }
}