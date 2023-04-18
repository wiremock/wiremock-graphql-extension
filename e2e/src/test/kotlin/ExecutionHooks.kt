import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.common.ConsoleNotifier
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.thoughtworks.gauge.AfterSuite
import com.thoughtworks.gauge.BeforeScenario
import com.thoughtworks.gauge.BeforeSuite
import com.thoughtworks.gauge.datastore.SuiteDataStore

class ExecutionHooks {

    @BeforeSuite()
    fun setupSuite() {
        WireMockServer(
            wireMockConfig()
                .port(8080)
                .notifier(ConsoleNotifier(true))
        )
            .let {
                Datastore.server(it);
                it.start()
            }
    }

    @AfterSuite()
    fun tearDownSuite() {
        Datastore.server()?.shutdown()
    }

    @BeforeScenario()
    fun setupScenario() {
        Datastore.server()?.let {
            it.resetMappings()
            it.resetRequests()
        }
    }
}

object Datastore {
    fun server(): WireMockServer? {
        return SuiteDataStore.get("server") as WireMockServer?
    }

    fun server(client: WireMockServer) {
        SuiteDataStore.put("server", client)
    }
}