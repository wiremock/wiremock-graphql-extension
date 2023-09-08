import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.thoughtworks.gauge.datastore.SuiteDataStore

object Datastore {
    fun localServer(): WireMockServer? {
        return SuiteDataStore.get("localServer") as WireMockServer?
    }

    fun localServer(client: WireMockServer) {
        SuiteDataStore.put("localServer", client)
    }

    fun client(): WireMock? {
        return SuiteDataStore.get("client") as WireMock?
    }

    fun client(client: WireMock) {
        SuiteDataStore.put("client", client)
    }

    fun statusCode(): Int? {
        return SuiteDataStore.get("statusCode") as Int?
    }

    fun statusCode(statusCode: Int) {
        SuiteDataStore.put("statusCode", statusCode)
    }
}