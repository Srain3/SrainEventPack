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
                if (signmsg1 == chatcolor("[&9&lVote&r]")) { //クリックした看板はvoteではじまるか？
                    if (main.votenum[signmsg3] != null) { //nullじゃなければ実行
                        val playername = Regex(e.player.name)
                        if (!playername.containsMatchIn(main.votedplayer[0]!!)) { //vote済みのプレイヤーか判断、false(なし)なら入りtrue(あり)なら出る
                            val num = main.votenum[signmsg3]!! //vote主のプレイヤーから今の票を取得
                            val num1: Int = num.plus(1) //1票増やす
                            main.votenum[signmsg3] = num1 //vote主に反映する
                            val votedp = main.votedplayer[0] //voteしたプレイヤー名を記録
                            val votedp1 = votedp + e.player.name //プレイヤー名を追記
                            main.votedplayer[0] = votedp1 //保存
                            main.voteptop[e.player.name] = signmsg3
                            e.player.sendMessage("voteしました！")
                        } else { //vote済みのプレイヤーなら
                            if (main.voteptop[e.player.name] != signmsg3) {
                                val oldname = main.votenum[main.voteptop[e.player.name].toString()]!!
                                val oldname1 = oldname.minus(1)
                                main.votenum[main.voteptop[e.player.name].toString()] = oldname1
                                val newname = main.votenum[signmsg3]!!
                                val newname1 = newname.plus(1)
                                main.votenum[signmsg3] = newname1
                                main.voteptop[e.player.name] = signmsg3
                                e.player.sendMessage("voteを${signmsg3}に変更しました！")
                            } //同じ人へ投票なら
                            e.player.sendMessage("vote済みです")
                        }
                        e.isCancelled = true
                    } //nullの場合スルー
                } //vote以外の看板の場合スルー
            } //クリックが看板以外の場合スルー
        } //voteswitchがfalseの場合スルー
        return
    }

    fun votingtotal(){
        val sort0 = main.votenum.toList().sortedByDescending { it.second }.toMap() //投票数の多い順にソートした変数を保存
        val sortname = sort0.keys.toList()
        val votelistsize = main.votenum.size - 1
        var oldvotenum = -1
        var samevote = 0
        if (votelistsize >= 0) { //votenumに一人以上登録されてる場合
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
            main.votedplayer.clear() //votedplayerの中身を全消去
            main.votedplayer[0] = "" //votedplayerのKey[0]を空で作成
        } //votenumが一人も登録されてないならスルー
    }

    private fun chatcolor(msg:String): String {
        return ChatColor.translateAlternateColorCodes('&',msg)
    }
}