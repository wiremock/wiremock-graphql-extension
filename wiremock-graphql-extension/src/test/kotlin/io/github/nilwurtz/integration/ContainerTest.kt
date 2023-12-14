package io.github.nilwurtz.integration

import com.github.tomakehurst.wiremock.client.WireMock
import io.github.nilwurtz.GraphqlBodyMatcher
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import org.wiremock.integrations.testcontainers.WireMockContainer
import java.net.http.HttpClient
import java.nio.file.Paths
import java.util.*

@Testcontainers
class ContainerTest {

    companion object {
        const val QUERY = "{ query }"
        const val VARIABLES = """
            {
              "abc": 123
            }
        """
        const val OPERATION_NAME = "operationName"
    }

    @Container
    val wireMockContainer: WireMockContainer =
        WireMockContainer(
            DockerImageName.parse(WireMockContainer.OFFICIAL_IMAGE_NAME).withTag("3.1.0")
        )
            .withExposedPorts(8080)
            .withExtensions(
                GraphqlBodyMatcher.extensionName,
                Collections.singleton("io.github.nilwurtz.GraphqlBodyMatcher"),
                Collections.singleton(
                    Paths.get(
                        "target",
                        "test-wiremock-extension",
                        "wiremock-graphql-extension.jar"
                    ).toFile()
                )
            )
            .withMappingFromResource("mappings/all.json")
            .withMappingFromResource("mappings/operationName.json")
            .withMappingFromResource("mappings/variables.json")
            .withMappingFromResource("mappings/query.json")

    @Test
    fun `should start wiremock container`() {
        assertTrue(wireMockContainer.isRunning)
    }

    @Test
    fun testMatch() {
        WireMock(wireMockContainer.port).register(
            WireMock.post(WireMock.urlEqualTo("/graphql"))
                .andMatching(GraphqlBodyMatcher.extensionName, GraphqlBodyMatcher.withRequest("""{"query": "query { name id }"}"""))
                .willReturn(WireMock.ok())
        )
        val client = HttpClient.newHttpClient()
        val request = java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create("${wireMockContainer.baseUrl}/graphql"))
            .POST(java.net.http.HttpRequest.BodyPublishers.ofString("""{"query": "query { id name }"}"""))
            .build().let { client.send(it, java.net.http.HttpResponse.BodyHandlers.ofString()) }

        assertEquals(200, request.statusCode())
    }

    @Test
    @DisplayName("Test expected query contains new line")
    fun testNewLineContains() {
        val expectedQuery = """
            query DemoQuery {
              demo {
                id
                name
              }
            }
        """.trimIndent()
        val expectedJson = """
            {
                "query": "$expectedQuery"
            }
        """.trimIndent()
        WireMock(wireMockContainer.port).register(
            WireMock.post(WireMock.urlEqualTo("/graphql"))
                .andMatching(GraphqlBodyMatcher.extensionName, GraphqlBodyMatcher.withRequest(expectedJson))
                .willReturn(WireMock.ok())
        )

        val query = "query DemoQuery { demo { id name } }"
        val client = HttpClient.newHttpClient()
        val request = java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create("${wireMockContainer.baseUrl}/graphql"))
            .POST(java.net.http.HttpRequest.BodyPublishers.ofString("""{"query": "$query"}"""))
            .build().let { client.send(it, java.net.http.HttpResponse.BodyHandlers.ofString()) }

        assertEquals(200, request.statusCode())
    }

    @Test
    fun testMappingsAll() {
        val json = """
            {
                "query": "$QUERY",
                "variables": $VARIABLES,
                "operationName": "$OPERATION_NAME"
            }
        """.trimIndent()
        val client = HttpClient.newHttpClient()
        val request = java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create("${wireMockContainer.baseUrl}/graphql"))
            .POST(java.net.http.HttpRequest.BodyPublishers.ofString(json))
            .build().let { client.send(it, java.net.http.HttpResponse.BodyHandlers.ofString()) }

        assertEquals(200, request.statusCode())
    }

    @Test
    fun testMappingsOperationName() {
        val json = """
            {
                "query": "$QUERY",
                "operationName": "$OPERATION_NAME"
            }
        """.trimIndent()
        val client = HttpClient.newHttpClient()
        val request = java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create("${wireMockContainer.baseUrl}/graphql"))
            .POST(java.net.http.HttpRequest.BodyPublishers.ofString(json))
            .build().let { client.send(it, java.net.http.HttpResponse.BodyHandlers.ofString()) }

        assertEquals(200, request.statusCode())
    }

    @Test
    fun testMappingsVariables() {
        val json = """
            {
                "query": "$QUERY",
                "variables": $VARIABLES
            }
        """.trimIndent()
        val client = HttpClient.newHttpClient()
        val request = java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create("${wireMockContainer.baseUrl}/graphql"))
            .POST(java.net.http.HttpRequest.BodyPublishers.ofString(json))
            .build().let { client.send(it, java.net.http.HttpResponse.BodyHandlers.ofString()) }

        assertEquals(200, request.statusCode())
    }

    @Test
    fun testMappingsQuery() {
        val json = """
            {
                "query": "$QUERY"
            }
        """.trimIndent()
        val client = HttpClient.newHttpClient()
        val request = java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create("${wireMockContainer.baseUrl}/graphql"))
            .POST(java.net.http.HttpRequest.BodyPublishers.ofString(json))
            .build().let { client.send(it, java.net.http.HttpResponse.BodyHandlers.ofString()) }

        assertEquals(200, request.statusCode())
    }
}