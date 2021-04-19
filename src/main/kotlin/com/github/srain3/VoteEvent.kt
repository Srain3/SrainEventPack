package com.github.srain3

import org.bukkit.ChatColor
import org.bukkit.block.Sign
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.SignChangeEvent
import org.bukkit.event.player.PlayerInteractEvent

object VoteEvent : Listener {
    private val typesign = """.*_SIGN$""".toRegex()

    private lateinit var main: SEP //この中でMain(SEP)クラスを使うための(空の)変数設定
    fun voteEv(main0: SEP){ //Mainから読み出されてMainクラスを参照する箱
        main = main0 //mainにSEPを入れる
        return
    }

    @EventHandler(priority = EventPriority.NORMAL) //投票用の看板作成
    fun onSCE(e: SignChangeEvent){ //看板の内容変更Event
        if (main.voteswitch) { //voteswitchがtrueの場合
            val text1 = e.getLine(0) //一行目の文字取得
            val player = e.player.name //設置したプレイヤー名取得
            if (text1 == "vote") { //一行目がvoteの場合
                if (main.votenum[e.player.name] == null) { // 登録されてない場合登録作業
                    main.votenum[player] = 0 //votenum(投票箱)にプレイヤー登録
                }
                e.setLine(2, player) //看板3行目に自動でプレイヤー名記載
                e.setLine(0, chatcolor("[&9&lVote&r]"))
            }//一行目がvoteじゃない場合スルー
        }//voteswitchがfalseの場合スルー
        return
    }

    @EventHandler(priority = EventPriority.NORMAL)
    fun onSignClick(e: PlayerInteractEvent){
        if (main.voteswitch) { //voteswitchがtureの場合
            if (e.clickedBlock?.type?.name?.let { typesign.matches(it) } == true) { //クリックされたのが看板かどうか
                val signmsg: Sign = e.clickedBlock!!.state as Sign
                val signmsg1 = signmsg.getLine(0)
                val signmsg3 = signmsg.getLine(2)
                if (signmsg1 == chatcolor("[&9&lVote&r]") && main.votenum[signmsg3] != null) {
                    //クリックした看板はvoteで始まりvotenumに登録済みプレイヤーか？
                    e.isCancelled = true
                    if (main.voteptop[e.player.name] != null) { //すでに投票している人なら
                        if (main.voteptop[e.player.name] == signmsg3) { //同じ人に票を入れたら
                            e.player.sendMessage("投票済みです")
                            return
                        } else { //違う人に表を入れたら
                            main.votenum[main.voteptop[e.player.name]!!] = main.votenum[main.voteptop[e.player.name]]?.minus(1)!!
                            main.votenum[signmsg3] = main.votenum[signmsg3]?.plus(1)!!
                            main.voteptop[e.player.name] = signmsg3
                            e.player.sendMessage("${signmsg3}に変更しました!")
                            return
                        }
                    } //未投票なら
                    main.votenum[signmsg3] = main.votenum[signmsg3]?.plus(1)!!
                    main.voteptop[e.player.name] = signmsg3
                    e.player.sendMessage("投票しました!")
                    return
                } //vote以外や登録されていない看板の場合スルー
            } //クリックが看板以外の場合スルー
        } //voteswitchがfalseの場合スルー
        return
    }

    fun votingtotal(){
        val votelistsize = main.votenum.size - 1
        if (votelistsize >= 0) { //votenumに一人以上登録されてる場合
            val sort0 = main.votenum.toList().sortedByDescending { it.second }.toMap() //投票数の多い順にソートした変数を保存
            val sortname = sort0.keys.toList()
            var oldvotenum = -1
            var samevote = 0
            for (i in 0..votelistsize) { //順位(投票結果)をサーバー全員に送信
                if (sort0[sortname[i]] == oldvotenum) {
                    samevote += 1
                } else {
                    samevote = 0
                }
                main.server.onlinePlayers.forEach { player ->
                    player.sendMessage("${i+1-samevote}位:${sort0[sortname[i]]}票 ${sortname[i]}")
                }
                oldvotenum = sort0[sortname[i]]!!
            }
            main.votenum.clear() //votenumの中身を全消去
        } //votenumが一人も登録されてないならスルー
    }

    private fun chatcolor(msg:String): String {
        return ChatColor.translateAlternateColorCodes('&',msg)
    }
}