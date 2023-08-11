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
        Datastore.localServer()?.let {
            it.resetMappings()
            it.resetRequests()
        }
    }
}

object Datastore {
    fun localServer(): WireMockServer? {
        return SuiteDataStore.get("localServer") as WireMockServer?
    }

    fun localServer(client: WireMockServer) {
        SuiteDataStore.put("localServer", client)
    }
}