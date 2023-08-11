# Graphql Wiremock Extension - Graphql Body Matcher
GraphqlBodyMatcherはWireMockの拡張で、Graphqlのリクエストが意味的に一致しているかを検証します。これは、WireMockのカスタムリクエストマッチャーを利用して実現しています。

空白などの処理に加え、クエリのソートを行い正規化します。
graphqlのパースには`graphql-java`を利用しています。

また、クエリと同時に変数（variables）も比較します。変数のJsonの比較には`org.json.JSONObject.similar`を利用しています。ただし、配列の順番は一致していなければなりません。

以下の二つのクエリは一致するとみなされます。

```graphql
{
    hero {
        name
        friends {
            name
            age
        }
    }
}
```
```graphql
{
    hero {
        friends {
            age
            name
        }
        name
    }
}
```
以下の二つのクエリは一致しません。

```graphql
{
    hero {
        name
        friends {
            name
            age
        }
    }
}
```
```graphql
{
    hero {
        name
        friends {
            name
        }
    }
}
```

同様に、以下の二つの変数は一致するとみなされます。
（`org.json.JsonObject.similar`の挙動に基づきます）

```json
{
  "id": 1,
  "name": "John Doe"
}
```

```json
{
  "name": "John Doe",
  "id": 1
}
```

しかし、以下の二つの変数は一致しません（配列の順序が異なるため）。

```json
{
  "ids": [1, 2, 3]
}
```
```json
{
  "ids": [3, 2, 1]
}
```

## Usage
### Gradle

```groovy
repositories {
    mavenCentral()
}

dependencies {
    testImplementation 'io.github.nilwurtz:wiremock-graphql-extension:0.5.0'
}
```

### Maven

```xml
<dependency>
    <groupId>io.github.nilwurtz</groupId>
    <artifactId>wiremock-graphql-extension</artifactId>
    <version>0.5.0</version>
    <scope>test</scope>
</dependency>
```


## Example

```kotlin
import io.github.nilwurtz.GraphqlBodyMatcher

WireMock.stubFor(
    WireMock.post(WireMock.urlEqualTo("/graphql"))
        .andMatching(GraphqlBodyMatcher.withRequestJson("""{"query": "{ hero { name }}"}"""))
        .willReturn(WireMock.ok())
)
```

Jsonボディ内 `query` キーにGraphQLクエリが存在することを期待しています。

変数がある場合、Jsonボディ内 `variables` キーに変数が存在することを期待しています。

```kotlin
val expectedQuery = """
    query HeroInfo($id: Int) {
        hero(id: $id) {
            name
        }
    }
""".trimIndent()

val expectedVariables = """
    {
        "id": 1
    }
""".trimIndent()

val expectedJson = """
    {
        "query": "$expectedQuery",
        "variables": $expectedVariables
    }
""".trimIndent()

WireMock.stubFor(
    WireMock.post(WireMock.urlEqualTo("/graphql"))
        .andMatching(GraphqlBodyMatcher.withRequestJson(expectedJson))
        .willReturn(WireMock.ok())
)
```

`withRequestQueryAndVariables` メソッドも利用できます。

```kotlin
import io.github.nilwurtz.GraphqlBodyMatcher

WireMock.stubFor(
    WireMock.post(WireMock.urlEqualTo("/graphql"))
        .andMatching(GraphqlBodyMatcher.withRequestQueryAndVariables("{ hero { name }}"))
        .willReturn(WireMock.ok())
)
```

```kotlin
val expectedQuery = """
    query HeroInfo($id: Int) {
        hero(id: $id) {
            name
        }
    }
""".trimIndent()

val expectedVariables = """
    {
        "id": 1
    }
""".trimIndent()

WireMock.stubFor(
    WireMock.post(WireMock.urlEqualTo("/graphql"))
        .andMatching(GraphqlBodyMatcher.withRequestQueryAndVariables(expectedQuery, expectedVariables))
        .willReturn(WireMock.ok())
)
```


## With Remote Wiremock Server

docker のようなリモートサーバーでWiremockを起動する場合、以下のような設定が必要です。

Release から `wiremock-graphql-extension-x.y.z-jar-with-dependencies.jar`をDLしてください

### サーバー側の設定

#### docker run する場合

```
docker run -it --rm \
      -p 8080:8080 \
      --name wiremock \
      -v /path/to/wiremock-graphql-extension-0.5.0-jar-with-dependencies.jar:/var/wiremock/extensions/wiremock-graphql-extension-0.5.0-jar-with-dependencies.jar \
      wiremock/wiremock \
      --extensions io.github.nilwurtz.GraphqlBodyMatcher
```

#### docker build する場合

```dockerfile
FROM wiremock/wiremock:latest
COPY ./wiremock-graphql-extension-0.5.0-jar-with-dependencies.jar /var/wiremock/extensions/wiremock-graphql-extension-0.5.0-jar-with-dependencies.jar
CMD ["--extensions", "io.github.nilwurtz.GraphqlBodyMatcher"]
```

### テスト側の設定

```kotlin
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.github.nilwurtz.GraphqlBodyMatcher

fun registerGraphQLWiremock(json: String) {
     WireMock(8080).register(post(urlPathEqualTo(endPoint))
        .andMatching(GraphqlBodyMatcher.extensionName, Parameters.one("expectedJson", json))
        .willReturn(
            aResponse()
                 .withStatus(200)
     ))
}
```

## Limitations
現段階ではメジャーリリース前で、Queryの一部分をサポートしており、ミューテーションやエイリアスなどの全ての機能は網羅していません。将来的にはこれらの機能もサポートする予定です。

## License
This project is licensed under the terms of the MIT License.

## Contributing
Contributions are welcome! Feel free to open an issue or submit a pull request if you have any improvements or suggestions.