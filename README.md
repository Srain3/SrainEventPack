# SrainEventPack
 OyasaiServer向けEventPack
 現在はBuildBattleのみ実装済み
# OyasaiBuildBattleの使い方
 /oyasaibb start (min)
 このコマンドで建築時間を決めてタイマーを開始する
 (タイマーは２つ以上起動できない)
 
 /oyasaibb vote start
 このコマンドで投票機能を開始させて
 任意の看板の1行目に「vote」と記入すると
 クリックで投票できる看板が作れる。
 vote中は看板破壊されない。
 
 /oyasaibb vote stop
 このコマンドで投票を終了させて
 自動で投票数を集計、順位発表を行える。
 voteが終わったら看板が壊せるようになる。

 v0.1.1～
 /oyasaibb stop
 ビルドタイマーを終了させるコマンド。
# OyasaiTimerの使い方
 v0.1.1～の追加機能
 /otimer start (min)
 このコマンドでタイマーを起動する
 (２つ以上は起動できない)
 
 /otimer stop
 タイマーを終了させるコマンド。
