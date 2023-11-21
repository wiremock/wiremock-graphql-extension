import com.github.tomakehurst.wiremock.client.WireMock;
import io.github.nilwurtz.GraphqlBodyMatcher;
import org.junit.jupiter.api.DisplayName;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
public class TestSample {
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
    @DisplayName("Matches if GraphQL query is semantically equal to the request")
    public void testGraphql() throws IOException, InterruptedException {
        new WireMock(wiremockContainer.getPort()).register(
                WireMock.post(WireMock.urlEqualTo("/graphql"))
                        .andMatching(
                                GraphqlBodyMatcher.extensionName,
                                GraphqlBodyMatcher.Companion.withRequest(
                                        "{ \"query\": \"{ query { name id }}\" }"
                                )
                        )
                        .willReturn(
                                WireMock.aResponse()
                                        .withBody("{\"data\": {\"id\": 1, \"name\": \"test\"}}")
                        ));


        var client = HttpClient.newHttpClient();
        var request = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create(wiremockContainer.getBaseUrl() + "/graphql"))
                .POST(java.net.http.HttpRequest.BodyPublishers.ofString("{ \"query\": \"{ query { id name }}\" }"))
                .build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("{\"data\": {\"id\": 1, \"name\": \"test\"}}", response.body());
    }
}