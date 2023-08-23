# Graphql Request のクエリと変数を比較する

## 変数が完全に一致する場合、マッチする
tags: remote
* json<file:./fixtures/variables-match/setup.json>を受け取って200を返すスタブを登録する
* URL"/graphql"にボディ<file:./fixtures/variables-match/request.json>で、POSTリクエストを送る
* レスポンスステータスコードが"200"である

## 変数が完全に一致しない場合、マッチしない
tags: remote
* json<file:./fixtures/variables-not-match/setup.json>を受け取って200を返すスタブを登録する
* URL"/graphql"にボディ<file:./fixtures/variables-not-match/request.json>で、POSTリクエストを送る
* レスポンスステータスコードが"404"である

## 変数のプロパティ順が異なっても構造が一致する場合、マッチする
tags: remote
* json<file:./fixtures/variables-complex-match/setup.json>を受け取って200を返すスタブを登録する
* URL"/graphql"にボディ<file:./fixtures/variables-complex-match/request.json>で、POSTリクエストを送る
* レスポンスステータスコードが"200"である

## 変数の配列の順が異なる場合、マッチしない
tags: remote
* json<file:./fixtures/variables-array-order-not-match/setup.json>を受け取って200を返すスタブを登録する
* URL"/graphql"にボディ<file:./fixtures/variables-array-order-not-match/request.json>で、POSTリクエストを送る
* レスポンスステータスコードが"404"である

## クエリと変数が一致する場合、マッチする
* クエリ<file:./fixtures/query-variables-match/setup-query.graphql>と変数<file:./fixtures/query-variables-match/setup-variables.json>を受け取って200を返すスタブを登録する
* URL"/graphql"にボディ<file:./fixtures/query-variables-match/request.json>で、POSTリクエストを送る
* レスポンスステータスコードが"200"である