package com.github.srain3.sep.treasure

import com.github.srain3.sep.Tools.color
import com.github.srain3.sep.treasure.TreasureCommands.eventLocList
import com.github.srain3.sep.treasure.TreasureCommands.eventPlayerDataList
import com.github.srain3.sep.treasure.TreasureCommands.eventSwitch
import com.github.srain3.sep.treasure.TreasureCommands.settingFile
import com.github.srain3.sep.treasure.TreasureCommands.settingPlayer
import com.github.srain3.sep.treasure.TreasureCommands.settingSwitch
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

object TreasureClickEvent : Listener {
    private val typeHEAD = Regex(""".*_HEAD""")

    @EventHandler
    fun clickEvent(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_BLOCK) return
        if (!typeHEAD.matches(event.clickedBlock?.type?.name?: "null")) return
        if (event.hand != EquipmentSlot.HAND) return

        val clickLoc = event.clickedBlock?.location ?: return

        if (settingSwitch) {
            if (settingPlayer?.uniqueId != event.player.uniqueId) return
            //event.player.sendMessage("[&6宝探しDebug&r] $clickLoc".color())
            val locList = mutableListOf<Location>()
            settingFile.getList("locList")?.forEach { loc ->
                if (loc is Location) {
                    if (loc == clickLoc) {
                        event.player.sendMessage("[&6宝探しEvent&r] 既に設定済みの座標です".color())
                        return
                    } else {
                        locList.add(loc)
                    }
                }
            }
            locList.add(clickLoc)
            settingFile.set("locList", locList)
            event.player.sendMessage("[&6宝探しEvent&r] ${locList.size}個目の座標を登録しました".color())
        } else if (eventSwitch) {
            var hitSwitch = false
            eventPlayerDataList.forEach { data ->
                if (data.uuid == event.player.uniqueId) {
                    hitSwitch = true
                    eventLocList.forEach locFor@{ loc ->
                        if (loc == clickLoc) {
                            if (data.clearLocList.contains(clickLoc)) {
                                event.player.sendMessage("[&6宝探しEvent&r] 発見済みです".color())
                                return@forEach
                            } else {
                                data.clearLocList.add(clickLoc)
                                event.player.sendMessage("[&6宝探しEvent&r] ${data.clearTreasure()}個目を見つけました! 残り${data.maxTreasure()-data.clearTreasure()}個です".color())
                                event.player.playSound(clickLoc, Sound.BLOCK_NOTE_BLOCK_HARP, 1.5F, 1F)
                                if (data.clearTreasure() >= data.maxTreasure()) {
                                    event.player.server.onlinePlayers.forEach { player ->
                                        player.sendMessage("[&6宝探しEvent&r] ${event.player.name}さんが${data.maxTreasure()}個全て見つけました!".color())
                                        player.playSound(player.location, Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 1.5F, 1F)
                                    }
                                    event.player.server.logger.info("[宝探しEvent] ${event.player.name}さんが${data.maxTreasure()}個全て見つけました!")
                                }
                                return@forEach
                            }
                        }
                    }
                    return@forEach
                }
            }
            if (!hitSwitch) {
                val data = TreasurePlayerData(event.player.uniqueId, eventLocList)
                eventPlayerDataList.add(data)
                eventLocList.forEach { loc ->
                    if (loc == clickLoc) {
                        data.clearLocList.add(clickLoc)
                        event.player.sendMessage("[&6宝探しEvent&r] ${data.clearTreasure()}個目を見つけました! 残り${data.maxTreasure()-data.clearTreasure()}個です".color())
                        event.player.playSound(clickLoc, Sound.BLOCK_NOTE_BLOCK_HARP, 1.5F, 1F)
                        if (data.clearTreasure() >= data.maxTreasure()) {
                            event.player.server.onlinePlayers.forEach { player ->
                                player.sendMessage("[&6宝探しEvent&r] ${event.player.name}さんが${data.maxTreasure()}個全て見つけました!".color())
                                player.playSound(player.location, Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 1.5F, 1F)
                            }
                            event.player.server.logger.info("[宝探しEvent] ${event.player.name}さんが${data.maxTreasure()}個全て見つけました!")
                        }
                        return@forEach
                    }
                }
            }
        }

        return
    }
}