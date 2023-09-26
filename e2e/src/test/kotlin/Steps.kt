import com.github.tomakehurst.wiremock.client.WireMock.*
import com.thoughtworks.gauge.Step
import io.github.nilwurtz.GraphqlBodyMatcher
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class Steps {
    @Step("Register a stub to return 200 upon receiving json <json>")
    fun setupGraphqlJsonStub(json: String) {
        // for remote
        Datastore.client()?.register(
            post(urlEqualTo("/graphql"))
                .andMatching(GraphqlBodyMatcher.extensionName, GraphqlBodyMatcher.withRequest(json)).willReturn(ok())
        )
        // for local
        Datastore.localServer()
            ?.stubFor(
                post(urlEqualTo("/graphql"))
                    .andMatching(GraphqlBodyMatcher.extensionName, GraphqlBodyMatcher.withRequest(json))
                    .willReturn(ok())
            )
    }

    @Step("Register a normal stub to return 200 upon receiving json <json>")
    fun setupJsonPostStub(json: String) {
        Datastore.client()?.register(
            post(urlEqualTo("/graphql"))
                .withRequestBody(equalToJson(json)).willReturn(ok())
        )
        Datastore.localServer()
            ?.stubFor(
                post(urlEqualTo("/graphql"))
                    .withRequestBody(equalToJson(json))
                    .willReturn(ok())
            )
    }

    @Step("Register a stub to return 200 upon receiving the query<query>")
    fun setupGraphqlQueryStub(query: String) {
        Datastore.localServer()
            ?.stubFor(
                post(urlEqualTo("/graphql"))
                    .andMatching(GraphqlBodyMatcher.withRequestQueryAndVariables(query)).willReturn(ok())
            )
    }

    @Step("Register a stub to return 200 upon receiving the query<query> and variables<variables>")
    fun setupGraphqlQueryAndVariables(query: String, variables: String) {
        Datastore.localServer()
            ?.stubFor(
                post(urlEqualTo("/graphql"))
                    .andMatching(GraphqlBodyMatcher.withRequestQueryAndVariables(query, variables)).willReturn(ok())
            )
    }

    @Step("Send a POST request to URL <uri> with body <json>")
    fun requestPost(uri: String, json: String) {
        println("Sending request to ${Configuration.baseUrl + uri} with body $json")
        HttpClient.newHttpClient().sendAsync(
            HttpRequest.newBuilder(URI.create(Configuration.baseUrl + uri))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build(),
            HttpResponse.BodyHandlers.ofString()
        ).join().let { Datastore.statusCode(it.statusCode()) }
    }

    @Step("The response status code should be <statusCode>")
    fun assertStatusCode(statusCode: Int) {
        Datastore.statusCode()?.let { assert(it == statusCode) }
    }
}