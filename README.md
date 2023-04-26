# SrainEventPack
 OyasaiServer向けEventPack  
 現在は`宝探しイベント`のみ実装済み
## 宝探しイベントについて
### 宝の設定方法  
 0: 先に宝として設定したいHEADを設置しておく  
 1: コマンド`/treasure setting start`で設定モードを開始する(既存の設定は初期化されるので注意)  
 2: 宝のHEADを右クリックで登録していく  
 3: 全てのHEADを登録し終えたら`/treasure setting end`で設定モードを終了する  
 以上が宝の設定です。もし登録した場所を見返したい場合は`/treasure setting info`で見ることが可能です  
 後から設定を変更したい場合は`/treasure setting add`で1個追加、`/treasure settting remove`で1個消去できます。
### イベントの開催方法 
 1: コマンド`/treasure event start`でイベントを開始する  
 2: 参加者に探してもらう。全部見つけた人が出るとオンラインプレイヤー(とコンソール)へ通知が行きます  
 通知には全部見つけた人の名前と見つけた数が出ます  
 3: イベント途中のランキングを確認したい場合は`/treasure event ranking <NUM> [true]`で指定した順位まで見れます  
 `[true]`オプションはプレイヤー全員にランキングを見せる場合のみ必要です。  
 4: イベントを終了するコマンドは`/treasure event stop`です。これを行うと自動で5位までを全員へ表示して終了します