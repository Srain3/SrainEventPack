package com.github.srain3.sep.treasure

import com.github.srain3.sep.Tools.color
import com.github.srain3.sep.Tools.pl
import com.github.srain3.sep.treasure.TreasureCommands.eventPlayerDataList
import com.github.srain3.sep.treasure.TreasureCommands.eventRadarLevel
import com.github.srain3.sep.treasure.TreasureCommands.eventSwitch
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.scheduler.BukkitRunnable

object TreasureRadarTimer {
    fun startTimer() {
        object : BukkitRunnable() {
            override fun run() {
                if (!eventSwitch) {
                    cancel()
                    return
                }
                if (eventRadarLevel == 0) {
                    eventPlayerDataList.forEach { data ->
                        data.bossBar.isVisible = false
                    }
                    return
                }
                eventPlayerDataList.forEach { data ->
                    val player = Bukkit.getPlayer(data.uuid) ?: return@forEach
                    data.bossBar.isVisible = true
                    data.bossBar.progress = 0.0
                    data.bossBar.setTitle("[&6お宝レーダー&r] &aLv.$eventRadarLevel".color())
                    val length = data.isTreasure(player.location) ?: return@forEach
                    var levelLength = eventRadarLevel - (length/(10.0/eventRadarLevel.toDouble())).toInt()
                    if (levelLength == 0) {
                        levelLength = 1
                    }
                    data.bossBar.progress = levelLength.toDouble() / eventRadarLevel.toDouble()

                    object : BukkitRunnable() {
                        override fun run() {
                            if (levelLength <= 0) {
                                cancel()
                                return
                            } else {
                                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, 1.5F, 1.35F)
                                levelLength -= 1
                            }
                        }
                    }.runTaskTimer(pl, 0, 10)
                }
                return
            }
        }.runTaskTimer(pl, 20, 40)
    }
}