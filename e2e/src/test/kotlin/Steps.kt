import com.github.tomakehurst.wiremock.client.WireMock.*
import com.thoughtworks.gauge.Step
import io.github.nilwurtz.GraphqlBodyMatcher

class Steps {
    @Step("json<json>を受け取って200を返すスタブを登録する")
    fun setupGraphqlJsonStub(json: String) {
        Datastore.server()
            ?.stubFor(post(urlEqualTo("/graphql"))
                .andMatching(GraphqlBodyMatcher.withRequestJson(json)).willReturn(ok()))
    }
}