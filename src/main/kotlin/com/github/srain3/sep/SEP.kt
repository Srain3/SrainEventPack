package com.github.srain3.sep

import com.github.srain3.sep.Tools.getYaml
import com.github.srain3.sep.Tools.pl
import com.github.srain3.sep.treasure.TreasureClickEvent
import com.github.srain3.sep.treasure.TreasureCommandTab
import com.github.srain3.sep.treasure.TreasureCommands
import org.bukkit.plugin.java.JavaPlugin

class SEP : JavaPlugin() {
    override fun onEnable() {
        pl = this
        TreasureCommands.settingFile = "setting.yml".getYaml()
        server.pluginManager.registerEvents(TreasureClickEvent, this)
        getCommand("treasure")?.setExecutor(TreasureCommands)
        getCommand("treasure")?.tabCompleter = TreasureCommandTab
    }
}