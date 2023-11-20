package io.github.nilwurtz.integration

import com.github.tomakehurst.wiremock.client.WireMock
import io.github.nilwurtz.GraphqlBodyMatcher
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
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
}