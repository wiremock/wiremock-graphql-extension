import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.common.ConsoleNotifier
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.nilwurtz.GraphqlBodyMatcher
import com.thoughtworks.gauge.AfterSuite
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
}

object Datastore {
    fun server(): WireMockServer? {
        return SuiteDataStore.get("server") as WireMockServer?
    }

    fun server(client: WireMockServer) {
        SuiteDataStore.put("server", client)
    }
}