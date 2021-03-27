package com.github.srain3

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable

object TimerBB : Listener {
    private lateinit var main: SEP
    fun timerBB(main0:SEP){main = main0 ; return}
    val bbtime = Bukkit.createBossBar(chatcolor("&9BuildTimer|&r00:00"),BarColor.BLUE,BarStyle.SOLID)

    fun buildtime(time:Int,pl:Plugin) {
        if (!main.timerswitch) {
            main.timerswitch = true
            var timecount = time
            val timesave = time * 1.0
            main.server.onlinePlayers.forEach { player ->
                bbtime.addPlayer(player)
            }
            object : BukkitRunnable() {
                override fun run() {
                    if (0 < timecount) {
                        val min: Int = timecount / 60
                        val sec = timecount % 60
                        var minmsg = min.toString()
                        var secmsg = sec.toString()
                        if (min < 10){ minmsg = "0$minmsg" }
                        if (sec < 10){ secmsg = "0$secmsg" }
                        bbtime.setTitle(chatcolor("&9BuildTimer|&r${minmsg}:${secmsg}"))
                        bbtime.progress = timecount / timesave
                        timecount--
                    } else {
                        main.server.onlinePlayers.forEach { player ->
                            player.sendTitle(chatcolor("&c建築時間終了"), chatcolor("&dEnd of BuildTime!!"), 10, 60, 10)
                        }
                        bbtime.isVisible = false
                        main.timerswitch = false
                        bbtime.removeAll()
                        cancel()
                    }
                }
            }.runTaskTimer(pl, 0, 20)
        }
    }

    @EventHandler
    fun onJoinTimer(e: PlayerJoinEvent) {
        bbtime.addPlayer(e.player)
    }

    private fun chatcolor(msg:String): String {
        return ChatColor.translateAlternateColorCodes('&',msg)
    }
}
