package com.github.srain3

import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.context.ContextManager
import net.luckperms.api.query.QueryOptions
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class SEP : JavaPlugin() {
    var votenum = mutableMapOf<String, Int>()
    var votedplayer = mutableMapOf<Int, String>()
    var voteptop = mutableMapOf<String, String>()
    var voteswitch = false
    var timerswitch = false

    companion object {
        lateinit var plugin: JavaPlugin
    }

    override fun onEnable() {
        plugin = this
        server.pluginManager.registerEvents(VoteEvent,this)
        server.pluginManager.registerEvents(TimerBB, this)
        VoteEvent.voteEv(this)
        TimerBB.timerBB(this)
        votedplayer[0] = ""
        TimerBB.bbtime.isVisible = false
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String>? { //tab補完機能
        if (args.size == 1){ //引数が1個の場合
            return if (args[0].isEmpty()) {
                Arrays.asList("vote","start")
            } else {
                //入力されている文字列と先頭一致
                when (args[0].isNotEmpty()) {
                    "vote".startsWith(args[0]) -> Arrays.asList("vote")
                    "start".startsWith(args[0]) -> Arrays.asList("start")
                    else -> {
                        //JavaPlugin#onTabComplete()を呼び出す
                        super.onTabComplete(sender, command, alias, args)
                    }
                }
            }
        }
        if (args.size == 2){ //引数が2個の場合
            return when (args[0]){
                "vote" -> Arrays.asList("start", "stop")
                "start" -> Arrays.asList("[min]")
                else -> {
                    super.onTabComplete(sender, command, alias, args)
                }
            }
        }
        return null
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) { // コマンド送信元はPlayerか？
            // コマンド送信元がPlayerの場合
            val luckperms = LuckPermsProvider.get()
            fun hasPerm(p: Player, permission: String): Boolean {
                if (!p.isOnline) throw IllegalArgumentException("Player is Offile")
                val user = luckperms.userManager.getUser(p.uniqueId)!!
                val contextManager: ContextManager = luckperms.contextManager
                val contextSet = contextManager.getContext(user).orElseGet { contextManager.staticContext }
                val permissionData = user.cachedData.getPermissionData(QueryOptions.contextual(contextSet))
                return permissionData.checkPermission(permission).asBoolean()
            }
            if (label == "oyasaibb" && hasPerm(sender, command.permission.toString())) { //コマンド/oyasaibbだった場合
                if (args.size == 2 && args[0] == "vote") { //引数が2つ、更に/oyasaibb voteの場合
                    if (args[1] == "start") { //コマンド/oyasaibb vote startの場合
                        voteswitch = true
                        server.onlinePlayers.forEach { player ->
                            player.sendMessage("おやさいBuildBattleの投票が開始しました!")
                        }
                        return true
                    }
                    if (args[1] == "stop") { //コマンド/oyasaibb vote stopの場合
                        voteswitch = false
                        server.onlinePlayers.forEach { player ->
                            player.sendMessage("投票結果!!")
                        }
                        VoteEvent.votingtotal()
                        return true
                    } //上記以外の場合
                    sender.sendMessage("/oyasaibb vote [start/stop]")
                    return true
                } //コマンド/oyasaibb vote以外の場合
                if (args.size == 2 && args[0] == "start") { //コマンド/oyasaibb startだった場合
                    if (!timerswitch) {
                        if (args[1].toIntOrNull() != null && args[1].toInt() > 0) { //引数2個目が数字の0以上の場合
                            TimerBB.bbtime.isVisible = true
                            TimerBB.buildtime(args[1].toInt() * 60, plugin)
                            return true
                        } //数字以外の場合
                        sender.sendMessage("/oyasaibb start [ビルドタイム(分)]")
                        return true
                    } //timerが起動中の場合
                    sender.sendMessage("タイマー起動中です。")
                    return true
                }
            }
            return true
        } //コマンド送信者がPlayer以外の場合
        sender.sendMessage("[SEP]ゲーム内のプレイヤーのみ可能です")
        return true
    }
}