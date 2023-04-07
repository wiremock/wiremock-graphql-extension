import com.github.tomakehurst.wiremock.extension.Parameters
import com.github.tomakehurst.wiremock.http.Request
import com.github.tomakehurst.wiremock.matching.MatchResult
import com.github.tomakehurst.wiremock.matching.RequestMatcherExtension
import graphql.ExecutionInput
import graphql.ParseAndValidate
import org.json.JSONObject

class GraphqlBodyMatcher : RequestMatcherExtension() {
    override fun match(p0: Request, p1: Parameters): MatchResult {
        val requestBody = p0.bodyAsString
        val expectedRequestBody = p1["body"].toString()

        val requestJson = JSONObject(requestBody)
        val expectedRequestJson = JSONObject(expectedRequestBody)

        val requestQuery =
            requestJson.getString("query").let { ParseAndValidate.parse(ExecutionInput.newExecutionInput(it).build()) }
        val expectedQuery =
            expectedRequestJson.getString("query").let { ParseAndValidate.parse(ExecutionInput.newExecutionInput(it).build()) }

        if (requestQuery.isFailure || expectedQuery.isFailure) throw RuntimeException("failed to parse.")

        return if (requestQuery.document.isEqualTo(expectedQuery.document)){
            MatchResult.exactMatch()
        } else {
            MatchResult.noMatch()
        }
    }

    override fun getName(): String {
        return "graphql-body-matcher"
    }
}