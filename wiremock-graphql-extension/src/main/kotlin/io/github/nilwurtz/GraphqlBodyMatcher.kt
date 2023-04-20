package io.github.nilwurtz

import com.github.tomakehurst.wiremock.extension.Parameters
import com.github.tomakehurst.wiremock.http.Request
import com.github.tomakehurst.wiremock.matching.MatchResult
import com.github.tomakehurst.wiremock.matching.RequestMatcherExtension
import graphql.parser.Parser
import io.github.nilwurtz.exceptions.InvalidJsonException
import io.github.nilwurtz.exceptions.InvalidQueryException
import org.json.JSONException
import org.json.JSONObject


class GraphqlBodyMatcher private constructor() : RequestMatcherExtension() {

    companion object {
        /**
         * Creates a new instance of [GraphqlBodyMatcher] with the given GraphQL query string.
         * The query string is wrapped in a JSON object with a "query" field, parsed, validated,
         * and normalized before being used for matching.
         *
         * @param expectedQuery The GraphQL query string that the matcher expects in requests.
         * @return A new [GraphqlBodyMatcher] instance with the given expected query.
         * @throws InvalidJsonException if the generated JSON is malformed.
         * @throws InvalidQueryException if the given query is invalid.
         */
        fun withRequestQuery(expectedQuery: String): GraphqlBodyMatcher {
            return GraphqlBodyMatcher().apply {
                initExpectedRequestJson("""{"query": "$expectedQuery"}""")
            }
        }

        /**
         * Creates a new instance of [GraphqlBodyMatcher] with the given raw JSON string containing a
         * GraphQL query. The JSON is expected to have a "query" field with the query string.
         * The query is parsed, validated, and normalized before being used for matching.
         *
         * @param expectedJson The raw JSON string containing the GraphQL query that the matcher expects in requests.
         * @return A new [GraphqlBodyMatcher] instance with the given expected query.
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
     * GraphQL query. The JSON is expected to have a "query" field with the query string.
     * The query is parsed and normalized before being used for matching.
     *
     * @param expectedJson The raw JSON string containing the GraphQL query that the matcher expects in requests.
     * @throws InvalidJsonException if the given JSON is malformed.
     * @throws InvalidQueryException if the given query inside the JSON is invalid.
     */
    private fun initExpectedRequestJson(expectedJson: String) {
        try {
            expectedRequestJson = JSONObject(expectedJson)
            val query = expectedRequestJson.getString("query")
            // Attempt to parse and normalize the query to check for validity
            Parser().parseDocument(query)
        } catch (e: JSONException) {
            throw InvalidJsonException("Failed to parse the provided JSON string: $expectedJson", e)
        } catch (e: Exception) {
            throw InvalidQueryException("Failed to parse the provided GraphQL query: ${expectedRequestJson.getString("query")}", e)
        }
    }

    /**
     * Compares the given [Request] and its GraphQL query against the expected query to determine
     * if they match. If both queries are semantically equal after normalization, it returns
     * an exact match result; otherwise, it returns a no match result.
     *
     * @param request The incoming request to match against the expected query.
     * @param parameters Additional parameters that may be used for matching.
     * @return [MatchResult.exactMatch] if the request query matches the expected query,
     *         [MatchResult.noMatch] otherwise.
     * @throws InvalidJsonException if the request JSON or the expected JSON is invalid.
     * @throws InvalidQueryException if the request query or the expected query is invalid.
     */
    override fun match(request: Request, parameters: Parameters): MatchResult {
        val requestBody = request.bodyAsString
        val requestJson = JSONObject(requestBody)

        val requestQuery = try {
            requestJson.getString("query")
                .let { Parser().parseDocument(it) }
                .let { GraphqlQueryNormalizer.normalizeGraphqlDocument(it) }
        } catch (e: Exception) {
            throw InvalidQueryException("Invalid request query: ${e.message}")
        }

        val expectedQuery = try {
            expectedRequestJson.getString("query")
                .let { Parser().parseDocument(it) }
                .let { GraphqlQueryNormalizer.normalizeGraphqlDocument(it) }
        } catch (e: Exception) {
            throw InvalidQueryException("Invalid expected query: ${e.message}")
        }

        return if (requestQuery.toString() == expectedQuery.toString()) {
            MatchResult.exactMatch()
        } else {
            MatchResult.noMatch()
        }
    }

    override fun getName(): String {
        return "graphql-body-matcher"
    }

}