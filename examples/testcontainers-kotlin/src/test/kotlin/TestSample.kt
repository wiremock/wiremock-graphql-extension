import com.fasterxml.jackson.databind.JsonNode
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.http.Body
import io.github.nilwurtz.GraphqlBodyMatcher
import org.junit.jupiter.api.Assertions
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
class TestSample {
    @Container
    val wireMockContainer: WireMockContainer =
        WireMockContainer(
            DockerImageName.parse(WireMockContainer.OFFICIAL_IMAGE_NAME + ":3.3.1")
        )
            .withExtensions(
                GraphqlBodyMatcher.extensionName,
                Collections.singleton("io.github.nilwurtz.GraphqlBodyMatcher"),
                Collections.singleton(
                    Paths.get(
                        "target",
                        "test-wiremock-extension",
                        "wiremock-graphql-extension-jar-with-dependencies.jar"
                    ).toFile()
                )
            )

    @Test
    @DisplayName("Is container running")
    fun testRunning() {
        assertTrue { wireMockContainer.isRunning }
    }

    @Test
    @DisplayName("Matches if GraphQL query is semantically equal to the request")
    fun testMatches() {
        WireMock(wireMockContainer.port).register(
            WireMock.post(WireMock.urlEqualTo("/graphql"))
                .andMatching(
                    GraphqlBodyMatcher.extensionName,
                    GraphqlBodyMatcher.withRequest("""{"query": "query { name id }"}""")
                )
                .willReturn(ResponseDefinitionBuilder.okForJson("""{"data": {"id": 1, "name": "test"}}"""))
        )


        val client = HttpClient.newHttpClient()
        val request = java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create("${wireMockContainer.baseUrl}/graphql"))
            .POST(java.net.http.HttpRequest.BodyPublishers.ofString("""{"query": "query { id name }"}"""))
            .build().let { client.send(it, java.net.http.HttpResponse.BodyHandlers.ofString()) }

        assertEquals(200, request.statusCode())
        assertEquals(""""{\"data\": {\"id\": 1, \"name\": \"test\"}}"""", request.body())
    }

    @Test
    @DisplayName("Matches if GraphQL query is semantically equal to the request using variables")
    fun testMatchesVariables() {
        WireMock(wireMockContainer.port).register(
            WireMock.post(WireMock.urlEqualTo("/graphql"))
                .andMatching(
                    GraphqlBodyMatcher.extensionName,
                    GraphqlBodyMatcher.withRequest("""{"query": "query { name id }", "variables": {"id": 1}}""")
                )
                .willReturn(WireMock.aResponse().withStatus(200))
        )

        val client = HttpClient.newHttpClient()
        val request = java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create("${wireMockContainer.baseUrl}/graphql"))
            .POST(java.net.http.HttpRequest.BodyPublishers.ofString("""{"query": "query { id name }", "variables": {"id": 1}}"""))
            .build().let { client.send(it, java.net.http.HttpResponse.BodyHandlers.ofString()) }

        assertEquals(200, request.statusCode())
    }
}