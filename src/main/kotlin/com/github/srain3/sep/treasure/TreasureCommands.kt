package com.github.srain3.sep.treasure

import com.github.srain3.sep.Tools.color
import com.github.srain3.sep.Tools.getYaml
import com.github.srain3.sep.Tools.pl
import com.github.srain3.sep.Tools.saveYaml
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.util.UUID

object TreasureCommands: CommandExecutor {
    var settingSwitch = false
    var settingPlayer: Player? = null
    lateinit var settingFile: FileConfiguration
    var settingModeName = ""

    val eventLocList = mutableListOf<Location>()
    var eventSwitch = false
    val eventPlayerDataList = mutableListOf<TreasurePlayerData>()
    var eventStartMillisTime = 0L
    var eventRadarLevel = 0

    private val treasureRadarBarKeyList = mutableListOf<NamespacedKey>()

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
                                        settingModeName = "start"
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
                                        settingModeName = ""
                                        sender.sendMessage("[&6宝探しEvent&r] 設定モード終了".color())
                                    } else {
                                        sender.sendMessage("[&6宝探しEvent&r] 設定モードに入ってから使用してください".color())
                                    }
                                } else {
                                    sender.sendMessage("[&6宝探しEvent&r] Playerからのみ受け付けるコマンドです".color())
                                }
                            }
                            "add" -> {
                                if (sender is Player) {
                                    if (settingSwitch) {
                                        sender.sendMessage("[&6宝探しEvent&r] 既に設定モードが使われています。ADDモードをキャンセルしました".color())
                                    } else {
                                        settingSwitch = true
                                        settingPlayer = sender
                                        settingFile = "setting.yml".getYaml()
                                        settingModeName = "add"
                                        sender.sendMessage("[&6宝探しEvent&r] ADDモード開始(追加方法は宝にするHEADを右クリック)".color())
                                    }
                                } else {
                                    sender.sendMessage("[&6宝探しEvent&r] Playerからのみ受け付けるコマンドです".color())
                                }
                            }
                            "remove" -> {
                                if (sender is Player) {
                                    if (settingSwitch) {
                                        sender.sendMessage("[&6宝探しEvent&r] 既に設定モードが使われています。REMOVEモードをキャンセルしました".color())
                                    } else {
                                        settingSwitch = true
                                        settingPlayer = sender
                                        settingFile = "setting.yml".getYaml()
                                        settingModeName = "remove"
                                        sender.sendMessage("[&6宝探しEvent&r] REMOVEモード開始(消去方法は対象のHEADを右クリック)".color())
                                    }
                                } else {
                                    sender.sendMessage("[&6宝探しEvent&r] Playerからのみ受け付けるコマンドです".color())
                                }
                            }
                            "info" -> {
                                val locList = settingFile.getList("locList") ?: return true
                                sender.sendMessage("[&6宝探しEvent&r] ${locList.size}個の座標があります".color())
                                if (sender is Player) {
                                    locList.forEachIndexed { index, loc ->
                                        if (loc is Location) {
                                            val message = TextComponent()
                                            when ((index + 1) % 2) {
                                                0 -> { // 偶数
                                                    message.text = "[&6宝探しEvent&r] &3${index + 1}. world:${loc.world?.name} | x:${loc.blockX} y:${loc.blockY} z:${loc.blockZ}".color()
                                                }

                                                1 -> { // 奇数
                                                    message.text = "[&6宝探しEvent&r] &b${index + 1}. world:${loc.world?.name} | x:${loc.blockX} y:${loc.blockY} z:${loc.blockZ}".color()
                                                }

                                                else -> { // error
                                                    message.text = "[error] when ((index+1) % 2)は0または1以外になりました"
                                                }
                                            }
                                            message.hoverEvent = HoverEvent(
                                                HoverEvent.Action.SHOW_TEXT,
                                                Text("Click to Teleport (${index + 1})")
                                            )
                                            message.clickEvent = ClickEvent(
                                                ClickEvent.Action.RUN_COMMAND,
                                                "/tp ${loc.blockX} ${loc.blockY} ${loc.blockZ}"
                                            )
                                            sender.spigot().sendMessage(message)
                                        }
                                    }
                                } else {
                                    locList.forEachIndexed { index, loc ->
                                        if (loc is Location) {
                                            val message: String = when ((index + 1) % 2) {
                                                0 -> { // 偶数
                                                    "[&6宝探しEvent&r] &3${index + 1}. world:${loc.world?.name} | x:${loc.blockX} y:${loc.blockY} z:${loc.blockZ}".color()
                                                }

                                                1 -> { // 奇数
                                                    "[&6宝探しEvent&r] &b${index + 1}. world:${loc.world?.name} | x:${loc.blockX} y:${loc.blockY} z:${loc.blockZ}".color()
                                                }

                                                else -> { // error
                                                    "[error] when ((index+1) % 2)は0または1以外になりました"
                                                }
                                            }
                                            sender.sendMessage(message)
                                        }
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
                                if (eventSwitch) {
                                    sender.sendMessage("[&6宝探しEvent&r] 既に開始しています".color())
                                    return true
                                }
                                eventLocList.clear()
                                eventPlayerDataList.clear()
                                settingFile.getList("locList")?.forEach { loc ->
                                    if (loc is Location) {
                                        eventLocList.add(loc)
                                    }
                                }
                                eventSwitch = true
                                eventStartMillisTime = Instant.now(Clock.tickMillis(ZoneId.systemDefault())).toEpochMilli()
                                sender.server.onlinePlayers.forEach { player ->
                                    player.sendMessage("[&6宝探しEvent&r] Start!!!".color())
                                }
                                TreasureRadarTimer.startTimer()
                            }
                            "stop" -> {
                                if (!eventSwitch) {
                                    sender.sendMessage("[&6宝探しEvent&r] 開始していません".color())
                                    return true
                                }
                                eventSwitch = false
                                sender.server.dispatchCommand(sender, "treasure event ranking 5 true")
                                eventPlayerDataList.forEach { data ->
                                    data.bossBar.removeAll()
                                }
                                treasureRadarBarKeyList.forEach { key ->
                                    Bukkit.removeBossBar(key)
                                }
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
                                val sortList = eventPlayerDataList.sortedWith(
                                    compareByDescending<TreasurePlayerData> {it.clearTreasure()}.thenBy { it.clearMillisTime }
                                )

                                var maxRanking = 4
                                var sendSwitch = false
                                if (args.size >= 3) {
                                    if (args[2].toIntOrNull() != null) {
                                        maxRanking = (args[2].toInt() - 1)
                                        if (maxRanking < 0 ) {
                                            maxRanking = 0
                                        }
                                    }
                                    if (args.size >= 4) {
                                        if (args[3] == "true") {
                                            sendSwitch = true
                                        }
                                    }
                                }
                                if (sendSwitch) {
                                    sender.server.broadcastMessage("[&6宝探しEvent&r] 現在の順位".color())
                                    sortList.forEachIndexed { index, data ->
                                        if (index > maxRanking) {
                                            return@forEachIndexed
                                        } else {
                                            sender.server.broadcastMessage(
                                                "${index + 1}位 ${data.clearTreasure()}/${data.maxTreasure()} Time=${data.clearTimeString()} : ${
                                                    sender.server.getOfflinePlayer(
                                                        data.uuid
                                                    ).name
                                                }"
                                            )
                                        }
                                    }
                                } else {
                                    sender.sendMessage("[&6宝探しEvent&r] 現在の順位".color())
                                    sortList.forEachIndexed { index, data ->
                                        if (index > maxRanking) {
                                            return@forEachIndexed
                                        } else {
                                            sender.sendMessage(
                                                "${index + 1}位 ${data.clearTreasure()}/${data.maxTreasure()} Time=${data.clearTimeString()} : ${
                                                    sender.server.getOfflinePlayer(
                                                        data.uuid
                                                    ).name
                                                }"
                                            )
                                        }
                                    }
                                }
                            }
                            "radar" -> {
                                if (args.size >= 3) {
                                    val level = args[2].toIntOrNull()
                                    if (level == null) {
                                        helpRadarCMD(sender)
                                    } else if (level > 3) {
                                        helpRadarCMD(sender)
                                    } else {
                                        eventRadarLevel = level
                                    }
                                } else {
                                    helpRadarCMD(sender)
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
        sender.sendMessage("[&6宝探しEvent&r] &6/treasure setting <start/end/info/add/remove>".color())
        sender.sendMessage("[&6宝探しEvent&r] 宝の位置設定を<開始/終了/確認/追加/消去>する。設定方法は宝にするHEADを右クリック".color())
        return
    }

    private fun helpEventCMD(sender: CommandSender) {
        sender.sendMessage("[&6宝探しEvent&r] &6/treasure event <start/stop/ranking/radar>".color())
        sender.sendMessage("[&6宝探しEvent&r] 宝探しイベントを<開始/終了/ランキング確認>する".color())
        sender.sendMessage("[&6宝探しEvent&r] <ランキング確認>のみ その後の引数で何位まで見るか、全員へ表示するかを指定可能↓".color())
        sender.sendMessage("[&6宝探しEvent&r] &6/treasure event ranking [1～] [true/false]".color())
        return
    }

    private fun helpRadarCMD(sender: CommandSender) {
        sender.sendMessage("[&6宝探しEvent&r] &6/treasure event radar <0～3>".color())
        sender.sendMessage("[&6宝探しEvent&r] <0～3>は段階で、0=off(機能停止)/1～3=±10blockを何段階で表示するか".color())
        sender.sendMessage("[&6宝探しEvent&r] 1の場合なら±10範囲内ということがわかる、3なら近ければ近いほど3段階で表示が変わる".color())
        return
    }

    fun radarBossBarKey(uuid: UUID): NamespacedKey {
        val key = NamespacedKey(pl, "treasure_radar-$uuid")
        treasureRadarBarKeyList.add(key)
        return key
    }
}