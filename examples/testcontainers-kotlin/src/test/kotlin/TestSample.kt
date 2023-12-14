import com.github.tomakehurst.wiremock.client.WireMock
import io.github.nilwurtz.GraphqlBodyMatcher
import org.junit.jupiter.api.Assertions.*
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
    fun testRunning() {
        assertTrue { wireMockContainer.isRunning }
    }

    @Test
    fun testMatches() {
        val query = """
            query {
              name
              id
            }
        """.trimIndent()
        WireMock(wireMockContainer.port).register(
            WireMock.post(WireMock.urlEqualTo("/graphql"))
                .andMatching(
                    GraphqlBodyMatcher.extensionName,
                    GraphqlBodyMatcher.parameters(query)
                )
                .willReturn(WireMock.okJson("""{"data": {"id": 1, "name": "test"}}"""))
        )


        val client = HttpClient.newHttpClient()
        val request = java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create("${wireMockContainer.baseUrl}/graphql"))
            .POST(java.net.http.HttpRequest.BodyPublishers.ofString("""{"query": "query { id name }"}"""))
            .build().let { client.send(it, java.net.http.HttpResponse.BodyHandlers.ofString()) }

        assertEquals(200, request.statusCode())
        assertEquals("""{"data": {"id": 1, "name": "test"}}""", request.body())
    }

    @Test
    fun testMatchesVariables() {
        val query = """
            query {
              name
              id
            }
        """.trimIndent()
        val variables = mapOf("id" to 1)
        WireMock(wireMockContainer.port).register(
            WireMock.post(WireMock.urlEqualTo("/graphql"))
                .andMatching(
                    GraphqlBodyMatcher.extensionName,
                    GraphqlBodyMatcher.parameters(query, variables)
                )
                .willReturn(WireMock.okJson("""{"data": {"id": 1, "name": "test"}}"""))
        )

        val client = HttpClient.newHttpClient()
        val request = java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create("${wireMockContainer.baseUrl}/graphql"))
            .POST(java.net.http.HttpRequest.BodyPublishers.ofString("""{"query": "query { id name }", "variables": {"id": 1}}"""))
            .build().let { client.send(it, java.net.http.HttpResponse.BodyHandlers.ofString()) }

        assertEquals(200, request.statusCode())
        assertEquals("""{"data": {"id": 1, "name": "test"}}""", request.body())
    }

    @Test
    fun testMatchesVariablesAndOperationName() {
        val query = """
            query {
              name
              id
            }
        """.trimIndent()
        val variables = mapOf("id" to 1)
        val operationName = "operationName"
        WireMock(wireMockContainer.port).register(
            WireMock.post(WireMock.urlEqualTo("/graphql"))
                .andMatching(
                    GraphqlBodyMatcher.extensionName,
                    GraphqlBodyMatcher.parameters(query, variables, operationName)
                )
                .willReturn(WireMock.okJson("""{"data": {"id": 1, "name": "test"}}"""))
        )

        val client = HttpClient.newHttpClient()
        val request = java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create("${wireMockContainer.baseUrl}/graphql"))
            .POST(java.net.http.HttpRequest.BodyPublishers.ofString("""{"query": "query { id name }", "variables": {"id": 1}, "operationName": "operationName"}"""))
            .build().let { client.send(it, java.net.http.HttpResponse.BodyHandlers.ofString()) }

        assertEquals(200, request.statusCode())
        assertEquals("""{"data": {"id": 1, "name": "test"}}""", request.body())
    }
}
