package io.github.nilwurtz

import com.github.tomakehurst.wiremock.extension.Parameters
import com.github.tomakehurst.wiremock.http.Request
import com.github.tomakehurst.wiremock.matching.MatchResult
import com.github.tomakehurst.wiremock.matching.RequestMatcherExtension
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
        try {
            expectedRequestJson = JSONObject(expectedJson)
            // Attempt to parse and normalize the query to check for validity
            expectedRequestJson.getString("query").run {
                Parser().parseDocument(this)
            }
        } catch (e: JSONException) {
            throw InvalidJsonException("Failed to parse the provided JSON string: $expectedJson", e)
        } catch (e: Exception) {
            throw InvalidQueryException(
                "Failed to parse the provided GraphQL query: ${expectedRequestJson.getString("query")}",
                e
            )
        }
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
     * @throws InvalidJsonException if the request JSON or the expected JSON is invalid.
     * @throws InvalidQueryException if the request query or the expected query is invalid.
     */
    override fun match(request: Request, parameters: Parameters): MatchResult {
        // for remote call
        if (parameters.containsKey(expectedJsonKey)) {
            expectedRequestJson = JSONObject(parameters.getString(expectedJsonKey))
        }
        val requestJson = JSONObject(request.bodyAsString)

        val isQueryMatch =
            requestJson.graphqlQueryDocument().normalize().toString() == expectedRequestJson.graphqlQueryDocument()
                .normalize().toString()
        val isVariablesMatch = requestJson.graphqlVariables().similar(expectedRequestJson.graphqlVariables())

        return when {
            isQueryMatch && isVariablesMatch -> MatchResult.exactMatch()
            else -> MatchResult.noMatch()
        }
    }

    override fun getName(): String {
        return extensionName
    }
}

private fun JSONObject.graphqlQueryDocument(): Document {
    return this.optString("query")
        .let { Parser().parseDocument(it) }
        ?: throw InvalidQueryException("Invalid query")
}

private fun JSONObject.graphqlVariables(): JSONObject {
    return this.optJSONObject("variables") ?: JSONObject()
}

private fun Document.normalize(): Document {
    return GraphqlQueryNormalizer.normalizeGraphqlDocument(this)
}
