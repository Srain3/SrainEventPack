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

object Timer : Listener {
    private lateinit var main: SEP
    fun timerBB(main0:SEP){main = main0 ; return}
    val bbtime = Bukkit.createBossBar(chatcolor("&9BuildTimer|&r00:00"),BarColor.BLUE,BarStyle.SOLID)
    var timecount = 0
    val ftime = Bukkit.createBossBar(chatcolor("00:00"),BarColor.BLUE,BarStyle.SOLID)
    var ftimecount = 0

    fun buildtime(time:Int,pl:Plugin) {
        if (!main.timerswitch) {
            main.timerswitch = true
            timecount = time
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
                            player.sendTitle(chatcolor("&c建築時間終了"), chatcolor("&dfinish!!"), 10, 60, 10)
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

    fun freetimer(time:Int,pl:Plugin) {
        ftimecount = time
        val ftimesave = time * 1.0
        main.server.onlinePlayers.forEach { player ->
            ftime.addPlayer(player)
        }
        object : BukkitRunnable() {
            override fun run() {
                if (0 < ftimecount) {
                    val min: Int = ftimecount / 60
                    val sec = ftimecount % 60
                    var minmsg = min.toString()
                    var secmsg = sec.toString()
                    if (min < 10){ minmsg = "0$minmsg" }
                    if (sec < 10){ secmsg = "0$secmsg" }
                    ftime.setTitle(chatcolor("${minmsg}:${secmsg}"))
                    ftime.progress = ftimecount / ftimesave
                    ftimecount--
                } else {
                    main.server.onlinePlayers.forEach { player ->
                        player.sendTitle(chatcolor("&c終了!"), chatcolor("&dfinish!!"), 10, 60, 10)
                    }
                    main.ftimerswitch = false
                    ftime.isVisible = false
                    ftime.removeAll()
                    cancel()
                }
            }
        }.runTaskTimer(pl, 0, 20)
    }

    @EventHandler
    fun onJoinTimer(e: PlayerJoinEvent) {
        bbtime.addPlayer(e.player)
        ftime.addPlayer(e.player)
    }

    private fun chatcolor(msg:String): String {
        return ChatColor.translateAlternateColorCodes('&',msg)
    }
}
