# Graphql Request のクエリを比較する

## 完全一致するクエリの場合、マッチする
* クエリ<file:./fixtures/exact-match/setup.json>を受け取って200を返すスタブを登録する
* URL"/graphql"にボディ<file:./fixtures/exact-match/request.json>で、POSTリクエストを送る
* レスポンスステータスコードが"200"である

## 完全一致しないクエリの場合、マッチしない
* クエリ<file:./fixtures/not-match/setup.json>を受け取って200を返すスタブを登録する
* URL"/graphql"にボディ<file:./fixtures/not-match/request.json>で、POSTリクエストを送る
* レスポンスステータスコードが"404"である

## 順番が異なるクエリの場合、マッチする
* クエリ<file:./fixtures/order-match/setup.json>を受け取って200を返すスタブを登録する
* URL"/graphql"にボディ<file:./fixtures/order-match/request.json>で、POSTリクエストを送る
* レスポンスステータスコードが"200"である

## エイリアスを使用したクエリの場合、マッチする
* クエリ<file:./fixtures/alias-match/setup.json>を受け取って200を返すスタブを登録する
* URL"/graphql"にボディ<file:./fixtures/alias-match/request.json>で、POSTリクエストを送る
* レスポンスステータスコードが"200"である

## 異なるエイリアスを使用したクエリの場合、マッチしない
* クエリ<file:./fixtures/alias-not-match/setup.json>を受け取って200を返すスタブを登録する
* URL"/graphql"にボディ<file:./fixtures/alias-not-match/request.json>で、POSTリクエストを送る
* レスポンスステータスコードが"404"である

## 同じフラグメントを使用したクエリの場合、マッチする
* クエリ<file:./fixtures/fragment-match/setup.json>を受け取って200を返すスタブを登録する
* URL"/graphql"にボディ<file:./fixtures/fragment-match/request.json>で、POSTリクエストを送る
* レスポンスステータスコードが"200"である

## 異なるフラグメント名で同じセットを使用したクエリの場合、マッチする
tags: unimplemented
* クエリ<file:./fixtures/fragment-name-match/setup.json>を受け取って200を返すスタブを登録する
* URL"/graphql"にボディ<file:./fixtures/fragment-name-match/request.json>で、POSTリクエストを送る
* レスポンスステータスコードが"200"である

## 同じ引数を持つクエリの場合、マッチする
* クエリ<file:./fixtures/argument-match/setup.json>を受け取って200を返すスタブを登録する
* URL"/graphql"にボディ<file:./fixtures/argument-match/request.json>で、POSTリクエストを送る
* レスポンスステータスコードが"200"である

## 異なる引数を持つクエリの場合、マッチしない
* クエリ<file:./fixtures/argument-not-match/setup.json>を受け取って200を返すスタブを登録する
* URL"/graphql"にボディ<file:./fixtures/argument-not-match/request.json>で、POSTリクエストを送る
* レスポンスステータスコードが"404"である