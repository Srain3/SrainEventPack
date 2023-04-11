package com.github.srain3.sep.treasure

import org.bukkit.Location
import java.util.UUID

data class TreasurePlayerData(
    val uuid: UUID,
    val answerLocList: MutableList<Location>,
    val clearLocList: MutableList<Location> = mutableListOf()
) {
    /**
     * 答えのLocに一致するかどうか
     * @return 一致しない場合false/一致して未発見の場合true/一致して発見済みの場合null
     */
    fun checkLocation(loc: Location): Boolean? {
        return if (answerLocList.contains(loc)) {
            if (clearLocList.contains(loc)) {
                null
            } else {
                true
            }
        } else {
            false
        }
    }

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
        return answerLocList.size
    }
}
