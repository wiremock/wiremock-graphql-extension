# Graphql Wiremock Extension - Graphql Body Matcher

⚠️ **IMPORTANT**: Starting from version 0.6, this extension requires WireMock 3.x. WireMock 2.x is no longer supported from this version onwards.

*An extension for GraphQL testing with Wiremock*

GraphqlBodyMatcher is an extension for [WireMock](https://wiremock.org/) that allows for semantical verification of GraphQL requests.

GraphqlBodyMatcherは[WireMock](https://wiremock.org/)の拡張で、GraphQLのリクエストが意味的に一致しているかを検証します。

## Overview 📖

- In addition to handling whitespaces, the extension sorts and normalizes queries. The GraphQL parsing is handled by `graphql-java`.
- Beyond just queries, it also compares variables. For the comparison of JSON variables, `org.json.JSONObject.similar` is employed. It's important to note that the order of arrays must match.

For a comprehensive understanding of our matching logic and details on our match strategy, please refer to our [MatchStrategy documentation](./docs/MatchStrategy.md).

- この拡張機能は、空白の取り扱いに加えて、クエリをソートし正規化します。GraphQLのパースには`graphql-java`を使用しています。
- クエリだけでなく、変数も比較されます。変数のJSONの比較には`org.json.JSONObject.similar`を使用しますが、配列の順番も一致している必要があります。

詳しいマッチングロジックなど関しては、[MatchStrategyのドキュメント](./docs/MatchStrategy.md)を参照してください。


## Usage 🛠️
### For Gradle:

```groovy
repositories {
    mavenCentral()
}

dependencies {
    testImplementation 'io.github.nilwurtz:wiremock-graphql-extension:0.7.0'
}
```

### For Maven:

```xml
<dependency>
    <groupId>io.github.nilwurtz</groupId>
    <artifactId>wiremock-graphql-extension</artifactId>
    <version>0.7.0</version>
    <scope>test</scope>
</dependency>
```


## Code Examples 💡
Here are some code examples to get started:
```kotlin
import io.github.nilwurtz.GraphqlBodyMatcher

WireMock.stubFor(
    WireMock.post(WireMock.urlEqualTo("/graphql"))
        .andMatching(GraphqlBodyMatcher.withRequestJson("""{"query": "{ hero { name }}"}"""))
        .willReturn(WireMock.ok())
)
```

The GraphQL query is expected inside the `"query"` key and variables within the `"variables"` key.

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

The `withRequestQueryAndVariables` method has been deprecated from version 0.6.0 onwards. Please use `withRequestJson` instead. 

## Running with a Remote Wiremock Server 🌍

If you are using Wiremock on a remote server such as Docker, please see the configurations below:

Please download `wiremock-graphql-extension-x.y.z-jar-with-dependencies.jar` from the Release section.

### Server Configuration
#### When running with `docker run`:
```
docker run -it --rm \
      -p 8080:8080 \
      --name wiremock \
      -v /path/to/wiremock-graphql-extension-0.7.0-jar-with-dependencies.jar:/var/wiremock/extensions/wiremock-graphql-extension-0.7.0-jar-with-dependencies.jar \
      wiremock/wiremock \
      --extensions io.github.nilwurtz.GraphqlBodyMatcher
```

#### When building with `docker build`:
```dockerfile
FROM wiremock/wiremock:latest
COPY ./wiremock-graphql-extension-0.7.0-jar-with-dependencies.jar /var/wiremock/extensions/wiremock-graphql-extension-0.7.0-jar-with-dependencies.jar
CMD ["--extensions", "io.github.nilwurtz.GraphqlBodyMatcher"]
```

### Client-side (Test) Configuration

NOTE: When using a Remote Wiremock Server, you're requested to manage everything within a single JSON format.

```kotlin
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.github.nilwurtz.GraphqlBodyMatcher

fun registerGraphQLWiremock(json: String) {
    WireMock(8080).register(
        post(urlPathEqualTo(endPoint))
            .andMatching(GraphqlBodyMatcher.extensionName, GraphqlBodyMatcher.withRequest(json))
            .willReturn(
                aResponse()
                    .withStatus(200)
            )
    )
}
```

## Limitations 🚧
This project currently focuses on supporting the fundamental parts of Queries. Some advanced features, such as mutations or aliases, are not yet fully supported. However, I aim to expand this scope over time.

## License 📜
This project is licensed under the terms of the MIT License.

## Contributing 🤝
Contributions are welcome! Feel free to open an issue or submit a pull request if you have any improvements or suggestions.