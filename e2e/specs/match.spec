# Graphql Request のクエリを比較する

## 完全一致するクエリの場合、マッチする
* クエリ<file:./fixtures/exact-match/setup.json>を受け取って200を返すスタブを登録する
* URL"/graphql"にボディ<file:./fixtures/exact-match/request.json>で、POSTリクエストを送る
* レスポンスステータスコードが"200"である

## 完全一致しないクエリの場合、マッチしない
* クエリ<file:./fixtures/not-match/setup.json>を受け取って200を返すスタブを登録する
* URL"/graphql"にボディ<file:./fixtures/not-match/request.json>で、POSTリクエストを送る
* レスポンスステータスコードが"404"である
