# Graphql Wiremock Extension - Graphql Body Matcher
GraphqlBodyMatcherはWireMockの拡張で、Graphqlのリクエストが意味的に一致しているかを検証します。これは、WireMockのカスタムリクエストマッチャーを利用して実現しています。

空白などの処理に加え、クエリのソートを行い正規化します。
graphqlのパースには`graphql-java`を利用しています。

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


## Usage
### Gradle

```groovy
repositories {
    mavenCentral()
}

dependencies {
    testImplementation 'io.github.nilwurtz:wiremock-graphql-extension:0.2.0'
}
```

### Maven

```xml
<dependency>
    <groupId>io.github.nilwurtz</groupId>
    <artifactId>wiremock-graphql-extension</artifactId>
    <version>0.2.0</version>
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

withRequestQuery メソッドも利用できます。

```kotlin
import io.github.nilwurtz.GraphqlBodyMatcher

WireMock.stubFor(
    WireMock.post(WireMock.urlEqualTo("/graphql"))
        .andMatching(GraphqlBodyMatcher.withRequestQuery("{ hero { name }}"))
        .willReturn(WireMock.ok())
)
```

## Limitations
現段階ではメジャーリリース前で、Queryの一部分をサポートしており、ミューテーションやエイリアスなどの全ての機能は網羅していません。将来的にはこれらの機能もサポートする予定です。

## License
This project is licensed under the terms of the MIT License.

## Contributing
Contributions are welcome! Feel free to open an issue or submit a pull request if you have any improvements or suggestions.