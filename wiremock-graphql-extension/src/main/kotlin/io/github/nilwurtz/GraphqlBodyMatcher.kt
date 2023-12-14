package io.github.nilwurtz

import com.github.tomakehurst.wiremock.common.Json
import com.github.tomakehurst.wiremock.common.JsonException
import com.github.tomakehurst.wiremock.extension.Parameters
import com.github.tomakehurst.wiremock.http.Request
import com.github.tomakehurst.wiremock.matching.AbsentPattern
import com.github.tomakehurst.wiremock.matching.EqualToJsonPattern
import com.github.tomakehurst.wiremock.matching.EqualToPattern
import com.github.tomakehurst.wiremock.matching.MatchResult
import com.github.tomakehurst.wiremock.matching.RequestMatcherExtension
import com.github.tomakehurst.wiremock.matching.StringValuePattern
import com.github.tomakehurst.wiremock.stubbing.SubEvent
import graphql.parser.InvalidSyntaxException
import graphql.parser.Parser


class GraphqlBodyMatcher() : RequestMatcherExtension() {

    companion object {
        const val extensionName = "graphql-body-matcher"

        /**
         * Creates a new instance of [GraphqlBodyMatcher] with the given GraphQL query string and variables.
         * The query string and variables are wrapped in a JSON object with "query" and "variables" fields, parsed, validated,
         * and normalized before being used for matching.
         *
         * @param expectedQuery The GraphQL query string that the matcher expects in requests.
         * @param expectedVariables The variables associated with the GraphQL query as a JSON string.
         * @return A new [GraphqlBodyMatcher] instance with the given expected query and variables.
         * @throws JsonException if the generated JSON is malformed.
         * @throws InvalidSyntaxException if the given query is invalid.
         */
        @Deprecated("Use parameters instead. Along with Wiremock.requestMatching(String, Parameters) or MappingBuilder#andMatching(String, Parameters).")
        @JvmStatic
        @JvmOverloads
        fun withRequestQueryAndVariables(expectedQuery: String, expectedVariables: String? = null): GraphqlBodyMatcher {
            // Avoid to parse json here. It will be parsed in initExpectedRequestJson
            return GraphqlBodyMatcher().apply {
                val variablesJsonOrEmptyString =
                    if (expectedVariables != null) ""","variables": $expectedVariables""" else ""
                initParameters(withRequest("""{"query": "$expectedQuery"$variablesJsonOrEmptyString}"""))
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
         * @throws JsonException if the given JSON is malformed.
         * @throws InvalidSyntaxException if the given query is invalid.
         */
        @Deprecated("Use parameters instead. Along with Wiremock.requestMatching(String, Parameters) or MappingBuilder#andMatching(String, Parameters).")
        @JvmStatic
        fun withRequestJson(expectedJson: String): GraphqlBodyMatcher {
            return GraphqlBodyMatcher().apply {
                initParameters(withRequest(expectedJson))
            }
        }

        /**
         * Creates a Parameters instance containing the given raw JSON string expected in the GraphQL request.
         *
         * This method is used to set up JSON expected in remote requests. The expectedJson parameter should be a raw JSON string that encapsulates the expected query and optionally variables for the GraphQL request. This string is used to create a parameters object utilized internally in the GraphqlBodyMatcher.
         *
         * @param expectedJson A raw JSON string that contains the GraphQL query and optionally variables expected in the requests.
         * @return A Parameters instance created based on the expected JSON string.
         * @throws JsonException if the given JSON is malformed.
         * @throws InvalidSyntaxException if the given query is invalid.
         */
        @Deprecated("Use parameters instead.")
        @JvmStatic
        fun withRequest(expectedJson: String): Parameters {
            val expectedJsonObject = Json.read(expectedJson.replace("\n", ""), Map::class.java)
            return parameters(
                expectedJsonObject["query"] as String,
                expectedJsonObject["variables"] as Map<String, Any>?,
                expectedJsonObject["operationName"] as String?)
        }

        /**
         * Creates a Parameters instance containing the query and optionally the variables and operationName.
         *
         * @param query A GraphQL query string.
         * @param variables An optional map of variables used in the GraphQL query.
         * @param operationName The optional name of the operation in the GraphQL query.
         * @return A Parameters instance containing the query and optionally the variables and operationName.
         * @throws InvalidSyntaxException if the given query is invalid.
         * @see <a href="https://graphql.org/learn/queries/">GraphQL Queries and Mutations</a>
         * @see <a href="https://graphql.org/learn/queries/#variables">GraphQL Variables</a>
         * @see <a href="https://graphql.org/learn/queries/#operationName">GraphQL Operation Name</a>
         */
        @JvmStatic
        @JvmOverloads
        fun parameters(query: String, variables: Map<String, Any>? = null, operationName: String? = null): Parameters {
            Parser().parseDocument(query)
            return Parameters.one("query", query).apply {
                variables?.let { put("variables", it) }
                operationName?.let { put("operationName", it) }
            }
        }
    }

    private lateinit var parameters: Parameters

    private fun initParameters(parameters: Parameters) {
        this.parameters = parameters
    }

    /**
     * Compares the given [Request] against the expected GraphQL query, variables, and operationName to determine if
     * they match. If query, variables, and operationName are semantically equal, it returns an exact match result;
     * otherwise, it returns a no match result.
     *
     * @param request The incoming request to match against the expected query and variables.
     * @param parameters Additional parameters that may be used for matching.
     * @return [MatchResult.exactMatch] if the request query and variables match the expected query and variables,
     *         [MatchResult.noMatch] otherwise.
     */
    override fun match(request: Request, parameters: Parameters): MatchResult {
        try {
            // for local call
            if (parameters.isEmpty()) {
                parameters.putAll(this.parameters)
            }
            val expectedQuery = parameters.getString("query")
            val expectedVariables = parameters["variables"]?.writeJson()
            val expectedOperationName = parameters.getString("operationName", null)

            val requestJson = Json.read(request.bodyAsString, Map::class.java)
            val requestQuery = requestJson["query"] as String
            val requestVariables = requestJson["variables"]?.writeJson()
            val requestOperationName = requestJson["operationName"] as String?

            return MatchResult.aggregate(
                EqualToGraphqlQueryPattern(expectedQuery).match(requestQuery),
                variablesPattern(expectedVariables).match(requestVariables),
                operationNamePattern(expectedOperationName).match(requestOperationName)
            )
        } catch (e: Exception) {
            return MatchResult.noMatch(SubEvent.warning(e.message))
        }
    }

    private fun variablesPattern(expectedVariables: String?) : StringValuePattern {
        return if (expectedVariables == null) {
            AbsentPattern.ABSENT
        } else {
            EqualToJsonPattern(expectedVariables, false, false)
        }
    }

    private fun operationNamePattern(expectedOperationName: String?) : StringValuePattern {
        return if (expectedOperationName == null) {
            AbsentPattern.ABSENT
        } else {
            EqualToPattern(expectedOperationName)
        }
    }

    override fun getName(): String {
        return extensionName
    }
}

private fun Any.writeJson(): String {
    return Json.write(this)
}
