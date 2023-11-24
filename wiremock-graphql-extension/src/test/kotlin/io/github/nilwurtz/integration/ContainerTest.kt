package io.github.nilwurtz.integration

import com.github.tomakehurst.wiremock.client.WireMock
import io.github.nilwurtz.GraphqlBodyMatcher
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.net.http.HttpClient

class ContainerTest : BaseContainerTest() {

    @Test
    fun `should start wiremock container`() {
        assertTrue(wireMockContainer.isRunning)
    }

    @Test
    fun testMatch() {
        WireMock(wireMockContainer.port).register(
            WireMock.post(WireMock.urlEqualTo("/graphql"))
                .andMatching(GraphqlBodyMatcher.extensionName, GraphqlBodyMatcher.withRequest("""{"query": "query { name id }"}"""))
                .willReturn(WireMock.aResponse().withStatus(200))
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
                .willReturn(WireMock.aResponse().withStatus(200))
        )

        val query = """
            query DemoQuery {
              demo {
                id
                name
              }
            }
        """.trimIndent()
        val client = HttpClient.newHttpClient()
        val request = java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create("${wireMockContainer.baseUrl}/graphql"))
            .POST(java.net.http.HttpRequest.BodyPublishers.ofString("""{"query": "$query"}"""))
            .build().let { client.send(it, java.net.http.HttpResponse.BodyHandlers.ofString()) }

        assertEquals(200, request.statusCode())
    }
}