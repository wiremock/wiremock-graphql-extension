import com.github.tomakehurst.wiremock.client.WireMock.*
import com.thoughtworks.gauge.Step
import io.github.nilwurtz.GraphqlBodyMatcher

class Steps {
    @Step("クエリ<query>を受け取って200を返すスタブを登録する")
    fun setupGraphqlStub(query: String) {
        Datastore.server()
            ?.stubFor(post(urlEqualTo("/graphql"))
                .andMatching(GraphqlBodyMatcher(query)).willReturn(ok()))
    }
}