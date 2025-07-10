# WireMock GraphQL Extension

---

<table>
<tr>
<td>
<img src="https://wiremock.org/images/wiremock-cloud/wiremock_cloud_logo.png" alt="WireMock Cloud Logo" height="20" align="left">
<strong>WireMock OSS is supported by WireMock Cloud. Please consider trying it out for rapid GraphQL mocking with advanced features like dynamic state and federation.</strong>
</td>
</tr>
</table>

---

⚠️ **IMPORTANT**: Starting from version 0.6, this extension requires WireMock 3.x. WireMock 2.x is no longer supported from this version onwards.

_An extension for GraphQL testing with Wiremock_

GraphqlBodyMatcher is an extension for [WireMock](https://wiremock.org/) that allows for semantical verification of GraphQL requests.

GraphqlBodyMatcher は[WireMock](https://wiremock.org/)の拡張で、GraphQL のリクエストが意味的に一致しているかを検証します。

## Overview 📖

- In addition to handling whitespaces, the extension sorts and normalizes queries. The GraphQL parsing and normalizing is handled by `graphql-java`.
- Beyond just queries, it also compares variables. For the comparison of JSON variables, [EqualToJsonPattern](https://github.com/wiremock/wiremock/blob/3.3.1/src/main/java/com/github/tomakehurst/wiremock/matching/EqualToJsonPattern.java) is used. It's important to note that the order of arrays must match.

For a comprehensive understanding of our matching logic and details on our match strategy, please refer to our [MatchStrategy documentation](./docs/MatchStrategy.md).

- この拡張機能は、空白の取り扱いに加えて、クエリをソートし正規化します。GraphQL のパースおよび正規化には`graphql-java`を使用しています。
- クエリだけでなく、変数も比較されます。変数の JSON の比較には`org.json.JSONObject.similar`を使用しますが、配列の順番も一致している必要があります。

詳しいマッチングロジックなど関しては、[MatchStrategy のドキュメント](./docs/MatchStrategy.md)を参照してください。

## Usage 🛠️

### For Gradle:

```groovy
repositories {
    mavenCentral()
}

dependencies {
    testImplementation 'io.github.nilwurtz:wiremock-graphql-extension:0.9.0'
}
```

### For Maven:

```xml
<dependency>
    <groupId>io.github.nilwurtz</groupId>
    <artifactId>wiremock-graphql-extension</artifactId>
    <version>0.9.0</version>
    <scope>test</scope>
</dependency>
```

## Code Examples 💡

Here are some code examples to get started.

```java Java
import com.github.tomakehurst.wiremock.client.WireMock;
import io.github.nilwurtz.GraphqlBodyMatcher;

import java.util.Map;

var expectedQuery = """
        query HeroInfo($id: Int) {
            hero(id: $id) {
                name
            }
        }
        """;
var expectedVariables = Map.of("id", 1);

WireMock.stubFor(WireMock.post(WireMock.urlEqualTo("/graphql"))
        .andMatching(GraphqlBodyMatcher.extensionName, GraphqlBodyMatcher.parameters(expectedQuery, expectedVariables))
        .willReturn(WireMock.okJson("""
                {
                    "data": {
                        "hero": {
                            "name": "example"
                        }
                    }
                }""")));
```

```kotlin Kotlin
import com.github.tomakehurst.wiremock.client.WireMock
import io.github.nilwurtz.GraphqlBodyMatcher

val expectedQuery = """
    query HeroInfo(${'$'}id: Int) {
        hero(id: ${'$'}id) {
            name
        }
    }
""".trimIndent()
val expectedVariables = mapOf("id" to 1)

WireMock.stubFor(
    WireMock.post(WireMock.urlEqualTo("/graphql"))
        .andMatching(GraphqlBodyMatcher.extensionName, GraphqlBodyMatcher.parameters(expectedQuery, expectedVariables))
        .willReturn(
            WireMock.okJson("""
                {
                    "data": {
                        "hero": {
                            "name": "example"
                        }
                    }
                }
            """.trimIndent()
            )
        )
)
```

As we head towards a 1.0 release, we are focusing on supporting both the WireMock standalone and the WireMock Java API
with the same paradigm. This is done through the use of `WireMock.requestMatching()` or `MappingBuilder#andMatching()`.
To that end, the `withRequestQueryAndVariables` method has been deprecated from version 0.6.0 onwards and
`withRequestJson` and `withRequest` methods have been deprecated from version 0.9.0 onwards.

## Running with a Remote Wiremock Server 🌍

If you are using Wiremock on a remote server such as Docker, please see the configurations below:

Please download `wiremock-graphql-extension-x.y.z-jar-with-dependencies.jar` from the Release section.

### Server Configuration

#### When running with `docker run`:

```
docker run -it --rm \
      -p 8080:8080 \
      --name wiremock \
      -v /path/to/wiremock-graphql-extension-0.9.0-jar-with-dependencies.jar:/var/wiremock/extensions/wiremock-graphql-extension-0.9.0-jar-with-dependencies.jar \
      wiremock/wiremock \
      --extensions io.github.nilwurtz.GraphqlBodyMatcher
```

#### When building with `docker build`:

```dockerfile
FROM wiremock/wiremock:latest
COPY ./wiremock-graphql-extension-0.9.0-jar-with-dependencies.jar /var/wiremock/extensions/wiremock-graphql-extension-0.9.0-jar-with-dependencies.jar
```

### Client-side (Test) Configuration

NOTE: When using a Remote Wiremock Server, you're requested to manage everything within a single JSON format.

```java Java
import com.github.tomakehurst.wiremock.client.WireMock;
import io.github.nilwurtz.GraphqlBodyMatcher;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public registerGraphQLWiremock(String query, String response) {
    WireMock(8080).register(
        post(urlPathEqualTo(endPoint))
            .andMatching(GraphqlBodyMatcher.extensionName, GraphqlBodyMatcher.parameters(query))
            .willReturn(okJson(response)));
}
```

```kotlin Kotlin
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.github.nilwurtz.GraphqlBodyMatcher

fun registerGraphQLWiremock(query: String, response: String) {
    WireMock(8080).register(
        post(urlPathEqualTo(endPoint))
            .andMatching(GraphqlBodyMatcher.extensionName, GraphqlBodyMatcher.parameters(query))
            .willReturn(okJson(response)))
}
```

## License 📜

This project is licensed under the terms of the MIT License.

## Contributing 🤝

Contributions are welcome! Feel free to open an issue or submit a pull request if you have any improvements or suggestions.
