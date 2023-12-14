import com.github.tomakehurst.wiremock.client.WireMock;
import io.github.nilwurtz.GraphqlBodyMatcher;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.wiremock.integrations.testcontainers.WireMockContainer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class TestSample {

    private static final String RESPONSE = """
            {
              "data": {
                "id": 1,
                "name": "test"
              }
            }
            """;
    @Container
    private static final WireMockContainer wiremockContainer = new WireMockContainer(
            DockerImageName.parse(WireMockContainer.OFFICIAL_IMAGE_NAME)
                    .withTag("3.3.1")
    ).withExtensions(
            GraphqlBodyMatcher.extensionName,
            Collections.singleton("io.github.nilwurtz.GraphqlBodyMatcher"),
            Collections.singleton(
                    Paths.get(
                            "target",
                            "test-wiremock-extension",
                            "wiremock-graphql-extension-jar-with-dependencies.jar"
                    ).toFile()
            )
    );

    @Test
    public void testRunning() {
        assertTrue(wiremockContainer.isRunning());
    }

    @Test
    public void testMatches() throws IOException, InterruptedException {
        var query = """
                query {
                  name
                  id
                }
                """;
        new WireMock(wiremockContainer.getPort()).register(
                WireMock.post(WireMock.urlEqualTo("/graphql"))
                        .andMatching(
                                GraphqlBodyMatcher.extensionName,
                                GraphqlBodyMatcher.parameters(query))
                        .willReturn(WireMock.okJson(RESPONSE)));;


        var client = HttpClient.newHttpClient();
        var request = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create(wiremockContainer.getBaseUrl() + "/graphql"))
                .POST(java.net.http.HttpRequest.BodyPublishers.ofString("""
                        { "query": "query { id name }" }"""))
                .build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(RESPONSE, response.body());
    }

    @Test
    public void testMatchesVariables() throws IOException, InterruptedException {
        var query = """
                query {
                  name
                  id
                }
                """;
        var variables = Map.of("id", 1);
        new WireMock(wiremockContainer.getPort()).register(
                WireMock.post(WireMock.urlEqualTo("/graphql"))
                        .andMatching(
                                GraphqlBodyMatcher.extensionName,
                                GraphqlBodyMatcher.parameters(query, variables)
                        )
                        .willReturn(WireMock.okJson(RESPONSE)));


        var client = HttpClient.newHttpClient();
        var request = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create(wiremockContainer.getBaseUrl() + "/graphql"))
                .POST(java.net.http.HttpRequest.BodyPublishers.ofString("""
                        {"query": "query { id name }", "variables": {"id": 1}}"""))
                .build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(RESPONSE, response.body());
    }

    @Test
    public void testMatchesVariablesAndOperationName() throws IOException, InterruptedException {
        var query = """
              query {
                name
                id
              }
              """;
        var variables = Map.of("id", 1);
        var operationName = "operationName";
        new WireMock(wiremockContainer.getPort()).register(
                WireMock.post(WireMock.urlEqualTo("/graphql"))
                        .andMatching(
                                GraphqlBodyMatcher.extensionName,
                                GraphqlBodyMatcher.parameters(query, variables, operationName)
                        )
                        .willReturn(WireMock.okJson(RESPONSE)));


        var client = HttpClient.newHttpClient();
        var request = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create(wiremockContainer.getBaseUrl() + "/graphql"))
                .POST(java.net.http.HttpRequest.BodyPublishers.ofString("""
                        {"query": "query { id name }", "variables": {"id": 1}, "operationName": "operationName"}"""))
                .build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(RESPONSE, response.body());
    }
}