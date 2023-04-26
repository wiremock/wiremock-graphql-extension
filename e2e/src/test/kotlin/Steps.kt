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

    @Step("クエリ<query>を受け取って200を返すスタブを登録する")
    fun setupGraphqlQueryStub(query: String) {
        Datastore.server()
            ?.stubFor(post(urlEqualTo("/graphql"))
                .andMatching(GraphqlBodyMatcher.withRequestQueryAndVariables(query)).willReturn(ok()))
    }

    @Step("クエリ<query>と変数<variables>を受け取って200を返すスタブを登録する")
    fun setupGraphqlQueryAndVariables(query: String, variables: String) {
        Datastore.server()
            ?.stubFor(post(urlEqualTo("/graphql"))
                .andMatching(GraphqlBodyMatcher.withRequestQueryAndVariables(query, variables)).willReturn(ok()))
    }
}