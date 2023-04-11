package com.github.srain3.sep.treasure

import com.github.srain3.sep.Tools.color
import com.github.srain3.sep.Tools.getYaml
import com.github.srain3.sep.Tools.saveYaml
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

object TreasureCommands: CommandExecutor {
    var settingSwitch = false
    var settingPlayer: Player? = null
    lateinit var settingFile: FileConfiguration

    val eventLocList = mutableListOf<Location>()
    var eventSwitch = false
    val eventPlayerDataList = mutableListOf<TreasurePlayerData>()

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (command.name != "treasure") return false
        if (args.isEmpty()) {
            // help出す
            helpSettingCMD(sender)
            helpEventCMD(sender)
            return true
        } else {
            when (args[0]) {
                "setting" -> {
                    if (args.size >= 2) {
                        when (args[1]) {
                            "start" -> {
                                if (sender is Player) {
                                    if (settingSwitch) {
                                        sender.sendMessage("[&6宝探しEvent&r] 既に設定モードが使われています".color())
                                    } else {
                                        settingSwitch = true
                                        settingPlayer = sender
                                        settingFile = "setting_kotlin_new.yml".getYaml()
                                        sender.sendMessage("[&6宝探しEvent&r] 設定モード開始(設定方法は宝にするHEADを右クリック)".color())
                                    }
                                } else {
                                    sender.sendMessage("[&6宝探しEvent&r] Playerからのみ受け付けるコマンドです".color())
                                }
                            }
                            "end" -> {
                                if (sender is Player) {
                                    if (settingPlayer?.uniqueId == sender.uniqueId) {
                                        settingSwitch = false
                                        settingPlayer = null
                                        "setting.yml".saveYaml(settingFile)
                                        sender.sendMessage("[&6宝探しEvent&r] 設定モード終了".color())
                                    } else {
                                        sender.sendMessage("[&6宝探しEvent&r] 設定モードに入ってから使用してください".color())
                                    }
                                } else {
                                    sender.sendMessage("[&6宝探しEvent&r] Playerからのみ受け付けるコマンドです".color())
                                }
                            }
                            "info" -> {
                                val locList = settingFile.getList("locList") ?: return true
                                sender.sendMessage("[&6宝探しEvent&r] ${locList.size}個の座標があります".color())
                                locList.forEach { loc ->
                                    if (loc is Location) {
                                        sender.sendMessage("[&6宝探しEvent&r] $loc".color())
                                    }
                                }
                            }
                            else -> {
                                helpSettingCMD(sender)
                            }
                        }
                    } else {
                        helpSettingCMD(sender)
                    }
                }
                "event" -> {
                    if (args.size >= 2) {
                        when (args[1]) {
                            "start" -> {
                                eventLocList.clear()
                                settingFile.getList("locList")?.forEach { loc ->
                                    if (loc is Location) {
                                        eventLocList.add(loc)
                                    }
                                }
                                eventSwitch = true
                                sender.server.onlinePlayers.forEach { player ->
                                    player.sendMessage("[&6宝探しEvent&r] Start!!!".color())
                                }
                            }
                            "stop" -> {
                                eventSwitch = false
                                eventPlayerDataList.clear()
                                sender.server.onlinePlayers.forEach { player ->
                                    player.sendMessage("[&6宝探しEvent&r] 終了しました".color())
                                }
                            }
                            "ranking" -> {
                                if (eventPlayerDataList.isEmpty()) {
                                    sender.sendMessage("[&6宝探しEvent&r] まだ順位がついていません".color())
                                    return true
                                }
                                val sortList = eventPlayerDataList.sortedBy { data ->
                                    data.clearTreasure()
                                }.reversed()

                                sender.sendMessage("[&6宝探しEvent&r] 現在の順位".color())
                                var maxRanking = 4
                                if (args.size >= 3) {
                                    if (args[2].toIntOrNull() != null) {
                                        maxRanking = (args[2].toInt() - 1)
                                        if (maxRanking < 0 ) {
                                            maxRanking = 0
                                        }
                                    }
                                }
                                sortList.forEachIndexed { index, data ->
                                    if (index > maxRanking) {
                                        return@forEachIndexed
                                    } else {
                                        sender.sendMessage("${index+1}位 ${data.clearTreasure()}/${data.maxTreasure()}: ${sender.server.getOfflinePlayer(data.uuid).name}")
                                    }
                                }
                            }
                            else -> {
                                helpEventCMD(sender)
                            }
                        }
                    } else {
                        helpEventCMD(sender)
                    }
                }
            }
            return true
        }
    }

    private fun helpSettingCMD(sender: CommandSender) {
        sender.sendMessage("[&6宝探しEvent&r] &6/treasure setting <start/end/info>".color())
        sender.sendMessage("[&6宝探しEvent&r] 宝の位置設定を<開始/終了/確認>する。設定方法は宝にするHEADを右クリック".color())
        return
    }

    private fun helpEventCMD(sender: CommandSender) {
        sender.sendMessage("[&6宝探しEvent&r] &6/treasure event <start/stop/ranking>".color())
        sender.sendMessage("[&6宝探しEvent&r] 宝探しイベントを<開始/終了/ランキング確認>する".color())
        sender.sendMessage("[&6宝探しEvent&r] <ランキング確認>のみ その後の引数で何位まで見るか指定可能↓".color())
        sender.sendMessage("[&6宝探しEvent&r] &6/treasure event ranking [1～]".color())
        return
    }
}