# Graphql Request のクエリを比較する

## 完全一致するJsonの場合、マッチする
* json<file:./fixtures/exact-match/setup.json>を受け取って200を返すスタブを登録する
* URL"/graphql"にボディ<file:./fixtures/exact-match/request.json>で、POSTリクエストを送る
* レスポンスステータスコードが"200"である

## 完全一致しないJsonの場合、マッチしない
* json<file:./fixtures/not-match/setup.json>を受け取って200を返すスタブを登録する
* URL"/graphql"にボディ<file:./fixtures/not-match/request.json>で、POSTリクエストを送る
* レスポンスステータスコードが"404"である

## 順番が異なるJsonの場合、マッチする
* json<file:./fixtures/order-match/setup.json>を受け取って200を返すスタブを登録する
* URL"/graphql"にボディ<file:./fixtures/order-match/request.json>で、POSTリクエストを送る
* レスポンスステータスコードが"200"である

## エイリアスを使用したJsonの場合、マッチする
* json<file:./fixtures/alias-match/setup.json>を受け取って200を返すスタブを登録する
* URL"/graphql"にボディ<file:./fixtures/alias-match/request.json>で、POSTリクエストを送る
* レスポンスステータスコードが"200"である

## 異なるエイリアスを使用したJsonの場合、マッチしない
* json<file:./fixtures/alias-not-match/setup.json>を受け取って200を返すスタブを登録する
* URL"/graphql"にボディ<file:./fixtures/alias-not-match/request.json>で、POSTリクエストを送る
* レスポンスステータスコードが"404"である

## 同じフラグメントを使用したJsonの場合、マッチする
* json<file:./fixtures/fragment-match/setup.json>を受け取って200を返すスタブを登録する
* URL"/graphql"にボディ<file:./fixtures/fragment-match/request.json>で、POSTリクエストを送る
* レスポンスステータスコードが"200"である

## 異なるフラグメント名で同じセットを使用したJsonの場合、マッチする
* json<file:./fixtures/fragment-name-match/setup.json>を受け取って200を返すスタブを登録する
* URL"/graphql"にボディ<file:./fixtures/fragment-name-match/request.json>で、POSTリクエストを送る
* レスポンスステータスコードが"200"である

## 同じフラグメント名で違うセットを使用したJsonの場合、マッチしない
* json<file:./fixtures/fragment-set-not-match/setup.json>を受け取って200を返すスタブを登録する
* URL"/graphql"にボディ<file:./fixtures/fragment-set-not-match/request.json>で、POSTリクエストを送る
* レスポンスステータスコードが"404"である

## 複数のフラグメントを含むクエリがマッチする
* json<file:./fixtures/multiple-fragments-match/setup.json>を受け取って200を返すスタブを登録する
* URL"/graphql"にボディ<file:./fixtures/multiple-fragments-match/request.json>で、POSTリクエストを送る
* レスポンスステータスコードが"200"である

## 複数のフラグメントを含むクエリでSetが違う場合マッチしない
* json<file:./fixtures/multiple-fragments-not-match/setup.json>を受け取って200を返すスタブを登録する
* URL"/graphql"にボディ<file:./fixtures/multiple-fragments-not-match/request.json>で、POSTリクエストを送る
* レスポンスステータスコードが"404"である

## 同じ引数を持つJsonの場合、マッチする
* json<file:./fixtures/argument-match/setup.json>を受け取って200を返すスタブを登録する
* URL"/graphql"にボディ<file:./fixtures/argument-match/request.json>で、POSTリクエストを送る
* レスポンスステータスコードが"200"である

## 異なる引数を持つJsonの場合、マッチしない
* json<file:./fixtures/argument-not-match/setup.json>を受け取って200を返すスタブを登録する
* URL"/graphql"にボディ<file:./fixtures/argument-not-match/request.json>で、POSTリクエストを送る
* レスポンスステータスコードが"404"である