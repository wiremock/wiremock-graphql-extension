package io.github.nilwurtz.integration

import io.github.nilwurtz.GraphqlBodyMatcher
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName
import org.wiremock.integrations.testcontainers.WireMockContainer
import java.nio.file.Paths
import java.util.Collections

@Testcontainers
open class BaseContainerTest {
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
}
