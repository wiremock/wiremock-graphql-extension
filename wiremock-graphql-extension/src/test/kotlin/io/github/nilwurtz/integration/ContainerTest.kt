package io.github.nilwurtz.integration

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ContainerTest : BaseContainerTest() {

    @Test
    fun `should start wiremock container`() {
        Assertions.assertTrue(wireMockContainer.isRunning)
    }
}