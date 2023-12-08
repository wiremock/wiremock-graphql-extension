package io.github.nilwurtz

import com.github.tomakehurst.wiremock.extension.Parameters
import com.github.tomakehurst.wiremock.http.Request
import com.github.tomakehurst.wiremock.matching.MatchResult
import com.github.tomakehurst.wiremock.matching.RequestMatcherExtension
import com.github.tomakehurst.wiremock.stubbing.SubEvent
import graphql.language.AstComparator
import graphql.language.AstSorter
import graphql.language.Document
import graphql.parser.Parser
import io.github.nilwurtz.exceptions.InvalidJsonException
import io.github.nilwurtz.exceptions.InvalidQueryException
import org.json.JSONException
import org.json.JSONObject


class GraphqlBodyMatcher() : RequestMatcherExtension() {

    companion object {
        const val extensionName = "graphql-body-matcher"
        private const val expectedJsonKey = "expectedJson"

        /**
         * Creates a new instance of [GraphqlBodyMatcher] with the given GraphQL query string and variables.
         * The query string and variables are wrapped in a JSON object with "query" and "variables" fields, parsed, validated,
         * and normalized before being used for matching.
         *
         * @param expectedQuery The GraphQL query string that the matcher expects in requests.
         * @param expectedVariables The variables associated with the GraphQL query as a JSON string.
         * @return A new [GraphqlBodyMatcher] instance with the given expected query and variables.
         * @throws InvalidJsonException if the generated JSON is malformed.
         * @throws InvalidQueryException if the given query is invalid.
         */
        @Deprecated("This method will be deleted in a future release. Use withRequestJson instead.")
        fun withRequestQueryAndVariables(expectedQuery: String, expectedVariables: String? = null): GraphqlBodyMatcher {
            // Avoid to parse json here. It will be parsed in initExpectedRequestJson
            return GraphqlBodyMatcher().apply {
                val variablesJsonOrEmptyString =
                    if (expectedVariables != null) ""","variables": $expectedVariables""" else ""
                initExpectedRequestJson("""{"query": "$expectedQuery"$variablesJsonOrEmptyString}""")
            }
        }

        /**
         * Creates a new instance of [GraphqlBodyMatcher] with the given raw JSON string containing a
         * GraphQL query and optional variables. The JSON is expected to have a "query" field with the query string
         * and an optional "variables" field containing the variables.
         * The query is parsed, validated, and normalized before being used for matching.
         *
         * @param expectedJson The raw JSON string containing the GraphQL query and optional variables that the matcher expects in requests.
         * @return A new [GraphqlBodyMatcher] instance with the given expected query and variables.
         * @throws InvalidJsonException if the given JSON is malformed.
         * @throws InvalidQueryException if the given query is invalid.
         */
        fun withRequestJson(expectedJson: String): GraphqlBodyMatcher {
            return GraphqlBodyMatcher().apply {
                initExpectedRequestJson(expectedJson)
            }
        }

        /**
         * Creates a Parameters instance containing the given raw JSON string expected in the GraphQL request.
         *
         * This method is used to set up JSON expected in remote requests. The expectedJson parameter should be a raw JSON string that encapsulates the expected query and optionally variables for the GraphQL request. This string is used to create a parameters object utilized internally in the GraphqlBodyMatcher.
         *
         * @param expectedJson A raw JSON string that contains the GraphQL query and optionally variables expected in the requests.
         * @return A Parameters instance created based on the expected JSON string.
         * @throws InvalidJsonException if the given JSON is malformed.
         * @throws InvalidQueryException if the given query is invalid.
         */
        fun withRequest(expectedJson: String): Parameters {
            // check if the json and query is valid
            expectedJson.toJSONObject().graphqlQueryDocument()
            return Parameters.one(expectedJsonKey, expectedJson)
        }
    }

    private lateinit var expectedRequestJson: JSONObject

    /**
     * Initializes the expected request JSON object from the given raw JSON string containing a
     * GraphQL query and optional variables. The JSON is expected to have a "query" field with the query string
     * and an optional "variables" field containing the variables.
     * The query is parsed and normalized before being used for matching.
     *
     * @param expectedJson The raw JSON string containing the GraphQL query and optional variables that the matcher expects in requests.
     * @throws InvalidJsonException if the given JSON is malformed.
     * @throws InvalidQueryException if the given query inside the JSON is invalid.
     */
    private fun initExpectedRequestJson(expectedJson: String) {
        expectedRequestJson = expectedJson.toJSONObject()
        // Attempt to parse and normalize the query to check for validity
        expectedRequestJson.graphqlQueryDocument()
    }

    /**
     * Compares the given [Request] and its GraphQL query and variables against the expected query and variables to determine
     * if they match. If both queries and variables are semantically equal after normalization, it returns
     * an exact match result; otherwise, it returns a no match result.
     *
     * @param request The incoming request to match against the expected query and variables.
     * @param parameters Additional parameters that may be used for matching.
     * @return [MatchResult.exactMatch] if the request query and variables match the expected query and variables,
     *         [MatchResult.noMatch] otherwise.
     */
    override fun match(request: Request, parameters: Parameters): MatchResult {
        try {
            // for remote call
            if (parameters.containsKey(expectedJsonKey)) {
                expectedRequestJson = parameters.getString(expectedJsonKey).toJSONObject()
            }
            val requestJson = request.bodyAsString.toJSONObject()

            val isQueryMatch = AstComparator.isEqual(
                requestJson.graphqlQueryDocument().sort(),
                expectedRequestJson.graphqlQueryDocument().sort())
            val isVariablesMatch = requestJson.graphqlVariables().similar(expectedRequestJson.graphqlVariables())

            return when {
                isQueryMatch && isVariablesMatch -> MatchResult.exactMatch()
                else -> MatchResult.noMatch(SubEvent.info("Request query is not matched. Expected query: ${expectedRequestJson.getString("query")}"))
            }
        } catch (e: Exception) {
            return MatchResult.noMatch(SubEvent.warning(e.message))

        }
    }

    override fun getName(): String {
        return extensionName
    }
}

private fun String.toJSONObject(): JSONObject {
    try {
        return JSONObject(this.trim().replace("\n", ""))
    } catch (e: Exception) {
        throw InvalidJsonException("Failed to parse the provided JSON string: $this", e)
    }
}

private fun JSONObject.graphqlQueryDocument(): Document {
    try {
        return this.optString("query")
            .let { Parser().parseDocument(it) }
            ?: throw InvalidQueryException("Invalid query")
    } catch (e: Exception) {
        throw InvalidQueryException("Failed to parse the provided GraphQL query: ${this.optString("query")}", e)
    }
}

private fun JSONObject.graphqlVariables(): JSONObject {
    return this.optJSONObject("variables") ?: JSONObject()
}

private fun Document.sort(): Document {
    return AstSorter().sort(this);
}
