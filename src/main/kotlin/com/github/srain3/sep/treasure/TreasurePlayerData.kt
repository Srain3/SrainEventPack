package com.github.srain3.sep.treasure

import com.github.srain3.sep.treasure.TreasureCommands.eventLocList
import org.bukkit.Location
import org.bukkit.boss.BossBar
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

data class TreasurePlayerData(
    val uuid: UUID,
    val bossBar: BossBar,
    val searchLocList: MutableList<Location>,
    val clearLocList: MutableList<Location> = mutableListOf(),
    var clearMillisTime: Long = 0L
) {
    /**
     * 見つけた宝の数
     */
    fun clearTreasure(): Int {
        return clearLocList.size
    }

    /**
     * 見つけるべき宝の数
     */
    fun maxTreasure(): Int {
        return eventLocList.size
    }

    /**
     * イベント開始から発見までのミリ秒保存
     */
    fun clearTimeMillis(startMillisTime: Long) {
        clearMillisTime = Instant.now().minusMillis(startMillisTime).toEpochMilli()
    }

    /**
     * 発見までのミリ秒を人間にわかりやすい表記で返す
     */
    fun clearTimeString(): String {
        return LocalDateTime.ofInstant(
            Instant.ofEpochMilli(clearMillisTime),
            ZoneOffset.ofHours(0)
        ).format(DateTimeFormatter.ISO_LOCAL_TIME)
    }

    /**
     * Locの周囲±10block以内に未発見の宝が一つ以上ある場合該当のLocまでの距離を返す、なければnullを返す
     */
    fun isTreasure(nowLoc: Location): Double? {
        searchLocList.forEach { sLoc ->
            val length = nowLoc.toVector().distance(sLoc.toVector())
            if (length.isNaN()) return@forEach
            if (length in -10.0..10.0) return length
        }
        return null
    }
}
