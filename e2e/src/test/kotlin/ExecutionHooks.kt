import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.common.ConsoleNotifier
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.thoughtworks.gauge.AfterSuite
import com.thoughtworks.gauge.BeforeScenario
import com.thoughtworks.gauge.BeforeSuite
import io.github.nilwurtz.GraphqlBodyMatcher

class ExecutionHooks {

    @BeforeSuite()
    fun setupSuite() {
        // for remote
        WireMock(8888).let { Datastore.client(it) }

        // for local
        WireMockServer(
            wireMockConfig()
                .port(8080)
                .extensions(GraphqlBodyMatcher::class.java)
                .notifier(ConsoleNotifier(true))
        )
            .let {
                Datastore.localServer(it);
                it.start()
            }
    }

    @AfterSuite()
    fun tearDownSuite() {
        Datastore.localServer()?.shutdown()
    }

    @BeforeScenario()
    fun setupScenario() {
        Datastore.client()?.let {
            it.resetMappings()
            it.resetRequests()
        }
        Datastore.localServer()?.let {
            it.resetMappings()
            it.resetRequests()
        }
    }
}
