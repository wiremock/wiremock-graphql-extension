package io.github.nilwurtz

import com.github.tomakehurst.wiremock.common.JsonException
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import com.github.tomakehurst.wiremock.http.Request
import com.github.tomakehurst.wiremock.extension.Parameters
import graphql.parser.InvalidSyntaxException
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows

class GraphqlBodyMatcherTest {

    companion object {
        const val QUERY = "{ query }"
        const val WRONG_QUERY = "{ wrong }";
        const val VARIABLES = """
            {
              "abc": 123
            }
        """
        const val WRONG_VARIABLES = """
            {
              "def": 456
            }
        """
        const val OPERATION_NAME = "operationName"
        const val WRONG_OPERATION_NAME = ""
        val VARIABLES_MAP = mapOf("abc" to 123)
    }

    @Test
    fun testAllMatch() {
        val request = mockk<Request>()
        val json = """
            {
                "query": "$QUERY",
                "variables": $VARIABLES,
                "operationName": "$OPERATION_NAME"
            }
        """.trimIndent()

        every { request.bodyAsString } returns json

        val actual = GraphqlBodyMatcher().match(request, GraphqlBodyMatcher.parameters(QUERY, VARIABLES_MAP, OPERATION_NAME))
        assertTrue(actual.isExactMatch)
    }

    @Test
    fun testAllWithWrongQuery() {
        val request = mockk<Request>()
        val json = """
            {
                "query": "$WRONG_QUERY",
                "variables": $VARIABLES,
                "operationName": "$OPERATION_NAME"
            }
        """.trimIndent()

        every { request.bodyAsString } returns json

        val actual = GraphqlBodyMatcher().match(request, GraphqlBodyMatcher.parameters(QUERY, VARIABLES_MAP, OPERATION_NAME))
        assertFalse(actual.isExactMatch)
        assertEquals(1.0/3.0, actual.distance)
    }

    @Test
    fun testAllWithWrongVariables() {
        val request = mockk<Request>()
        val json = """
            {
                "query": "$QUERY",
                "variables": $WRONG_VARIABLES,
                "operationName": "$OPERATION_NAME"
            }
        """.trimIndent()

        every { request.bodyAsString } returns json

        val actual = GraphqlBodyMatcher().match(request, GraphqlBodyMatcher.parameters(QUERY, VARIABLES_MAP, OPERATION_NAME))
        assertFalse(actual.isExactMatch)
        assertEquals(1.0/3.0, actual.distance)
    }

    @Test
    fun testAllWithWrongOperationName() {
        val request = mockk<Request>()
        val json = """
            {
                "query": "$QUERY",
                "variables": $VARIABLES,
                "operationName": "$WRONG_OPERATION_NAME"
            }
        """.trimIndent()

        every { request.bodyAsString } returns json

        val actual = GraphqlBodyMatcher().match(request, GraphqlBodyMatcher.parameters(QUERY, VARIABLES_MAP, OPERATION_NAME))
        assertFalse(actual.isExactMatch)
        assertEquals(1.0/3.0, actual.distance)
    }

    @Test
    fun testOperationNameMatch() {
        val request = mockk<Request>()
        val json = """
            {
                "query": "$QUERY",
                "operationName": "$OPERATION_NAME"
            }
        """.trimIndent()

        every { request.bodyAsString } returns json

        val actual = GraphqlBodyMatcher().match(request, GraphqlBodyMatcher.parameters(QUERY, null, OPERATION_NAME))
        assertTrue(actual.isExactMatch)
    }

    @Test
    fun testOperationNameWithWrongQuery() {
        val request = mockk<Request>()
        val json = """
            {
                "query": "$WRONG_QUERY",
                "operationName": "$OPERATION_NAME"
            }
        """.trimIndent()

        every { request.bodyAsString } returns json

        val actual = GraphqlBodyMatcher().match(request, GraphqlBodyMatcher.parameters(QUERY, null, OPERATION_NAME))
        assertFalse(actual.isExactMatch)
        assertEquals(1.0/3.0, actual.distance)
    }

    @Test
    fun testOperationNameWithWrongVariables() {
        val request = mockk<Request>()
        val json = """
            {
                "query": "$QUERY",
                "variables": $WRONG_VARIABLES,
                "operationName": "$OPERATION_NAME"
            }
        """.trimIndent()

        every { request.bodyAsString } returns json

        val actual = GraphqlBodyMatcher().match(request, GraphqlBodyMatcher.parameters(QUERY, null, OPERATION_NAME))
        assertFalse(actual.isExactMatch)
        assertEquals(1.0/3.0, actual.distance)
    }

    @Test
    fun testOperationNameWithWrongOperationName() {
        val request = mockk<Request>()
        val json = """
            {
                "query": "$QUERY",
                "operationName": "$WRONG_OPERATION_NAME"
            }
        """.trimIndent()

        every { request.bodyAsString } returns json

        val actual = GraphqlBodyMatcher().match(request, GraphqlBodyMatcher.parameters(QUERY, null, OPERATION_NAME))
        assertFalse(actual.isExactMatch)
        assertEquals(1.0/3.0, actual.distance)
    }

    @Test
    fun testVariables() {
        val request = mockk<Request>()
        val json = """
            {
                "query": "$QUERY",
                "variables": $VARIABLES
            }
        """.trimIndent()

        every { request.bodyAsString } returns json

        val actual = GraphqlBodyMatcher().match(request, GraphqlBodyMatcher.parameters(QUERY, VARIABLES_MAP))
        assertTrue(actual.isExactMatch)
    }

    @Test
    fun testVariablesWithWrongQuery() {
        val request = mockk<Request>()
        val json = """
            {
                "query": "$WRONG_QUERY",
                "variables": $VARIABLES
            }
        """.trimIndent()

        every { request.bodyAsString } returns json

        val actual = GraphqlBodyMatcher().match(request, GraphqlBodyMatcher.parameters(QUERY, VARIABLES_MAP))
        assertFalse(actual.isExactMatch)
        assertEquals(1.0/3.0, actual.distance)
    }

    @Test
    fun testVariablesWithWrongVariables() {
        val request = mockk<Request>()
        val json = """
            {
                "query": "$QUERY",
                "variables": $WRONG_VARIABLES
            }
        """.trimIndent()

        every { request.bodyAsString } returns json

        val actual = GraphqlBodyMatcher().match(request, GraphqlBodyMatcher.parameters(QUERY, VARIABLES_MAP))
        assertFalse(actual.isExactMatch)
        assertEquals(1.0/3.0, actual.distance)
    }

    @Test
    fun testVariablesWithWrongOperationName() {
        val request = mockk<Request>()
        val json = """
            {
                "query": "$QUERY",
                "variables": $VARIABLES,
                "operationName": "$WRONG_OPERATION_NAME"
            }
        """.trimIndent()

        every { request.bodyAsString } returns json

        val actual = GraphqlBodyMatcher().match(request, GraphqlBodyMatcher.parameters(QUERY, VARIABLES_MAP))
        assertFalse(actual.isExactMatch)
        assertEquals(1.0/3.0, actual.distance)
    }

    @Test
    fun testQuery() {
        val request = mockk<Request>()
        val json = """
            {
                "query": "$QUERY"
            }
        """.trimIndent()

        every { request.bodyAsString } returns json

        val actual = GraphqlBodyMatcher().match(request, GraphqlBodyMatcher.parameters(QUERY))
        assertTrue(actual.isExactMatch)
    }

    @Test
    fun testQueryWithWrongQuery() {
        val request = mockk<Request>()
        val json = """
            {
                "query": "$WRONG_QUERY"
            }
        """.trimIndent()

        every { request.bodyAsString } returns json

        val actual = GraphqlBodyMatcher().match(request, GraphqlBodyMatcher.parameters(QUERY))
        assertFalse(actual.isExactMatch)
        assertEquals(1.0/3.0, actual.distance)
    }

    @Test
    fun testQueryWithWrongVariables() {
        val request = mockk<Request>()
        val json = """
            {
                "query": "$QUERY",
                "variables": $WRONG_VARIABLES
            }
        """.trimIndent()

        every { request.bodyAsString } returns json

        val actual = GraphqlBodyMatcher().match(request, GraphqlBodyMatcher.parameters(QUERY))
        assertFalse(actual.isExactMatch)
        assertEquals(1.0/3.0, actual.distance)
    }

    @Test
    fun testQueryWithWrongOperationName() {
        val request = mockk<Request>()
        val json = """
            {
                "query": "$QUERY",
                "operationName": "$WRONG_OPERATION_NAME"
            }
        """.trimIndent()

        every { request.bodyAsString } returns json

        val actual = GraphqlBodyMatcher().match(request, GraphqlBodyMatcher.parameters(QUERY))
        assertFalse(actual.isExactMatch)
        assertEquals(1.0/3.0, actual.distance)
    }

    @Nested
    @DisplayName("test `withRequestJson`")
    inner class WithRequestJsonTest {
        @Test
        @DisplayName("queries are identical")
        fun testMatchedIdentical() {
            val request = mockk<Request>()
            val parameters = Parameters()
            // language=json
            val json = """
            {
                "query": "{ hero { name friends { name }}}"
            }
        """.trimIndent()

            every { request.bodyAsString } returns json

            val actual = GraphqlBodyMatcher.withRequestJson(json).match(request, parameters)
            assertTrue(actual.isExactMatch)
        }

        @Test
        @DisplayName("query has different order in single level")
        fun testMatchedDifferentOrderSingleLevel() {
            val request = mockk<Request>()
            val parameters = Parameters()
            // language=JSON
            val requestJson = """
            {
                "query": "{ hero { name, age, height }}"
            }
        """.trimIndent()
            // language=JSON
            val expectedJson = """
            {
                "query": "{ hero { age, height, name }}"
            }
        """.trimIndent()

            every { request.bodyAsString } returns requestJson

            val actual = GraphqlBodyMatcher.withRequestJson(expectedJson).match(request, parameters)
            assertTrue(actual.isExactMatch)
        }

        @Test
        @DisplayName("graphql query has different order so nested")
        fun testMatchedDifferentOrderNested() {
            val request = mockk<Request>()
            val parameters = Parameters()
            // language=JSON
            val requestJson = """
            {
                "query": "{ hero { name friends { name friends { name age }}}}"
            }
        """.trimIndent()
            // language=JSON
            val expectedJson = """
            {
                "query": "{ hero { name friends { name friends { age name }}}}"
            }
        """.trimIndent()

            every { request.bodyAsString } returns requestJson

            val actual = GraphqlBodyMatcher.withRequestJson(expectedJson).match(request, parameters)
            assertTrue(actual.isExactMatch)
        }

        @Test
        @DisplayName("query has different depth")
        fun testUnmatchedDifferentDepth() {
            val request = mockk<Request>()
            val parameters = Parameters()
            // language=json
            val requestJson = """
            {
                "query": "{ hero { name friends { name }}}"
            }
        """.trimIndent()
            // language=json
            val expectedJson = """
            {
                "query": "{ hero { name friends { name { first last } }}}"
            }
        """.trimIndent()

            every { request.bodyAsString } returns requestJson

            val actual = GraphqlBodyMatcher.withRequestJson(expectedJson).match(request, parameters)
            assertFalse(actual.isExactMatch)
        }

        @Test
        @DisplayName("query is missing a field")
        fun testUnmatchedMissingField() {
            val request = mockk<Request>()
            val parameters = Parameters()
            // language=json
            val requestJson = """
            {
                "query": "{ hero { friends { name }}}"
            }
        """.trimIndent()
            // language=json
            val expectedJson = """
            {
                "query": "{ hero { name friends { name }}}"
            }
        """.trimIndent()

            every { request.bodyAsString } returns requestJson

            val actual = GraphqlBodyMatcher.withRequestJson(expectedJson).match(request, parameters)
            assertFalse(actual.isExactMatch)
        }

        @Test
        @DisplayName("query has additional field")
        fun testUnmatchedAdditionalField() {
            val request = mockk<Request>()
            val parameters = Parameters()
            // language=json
            val requestJson = """
            {
                "query": "{ hero { name friends { name }}}"
            }
        """.trimIndent()
            // language=json
            val expectedJson = """
            {
                "query": "{ hero { friends { name }}}"
            }
        """.trimIndent()

            every { request.bodyAsString } returns requestJson

            val actual = GraphqlBodyMatcher.withRequestJson(expectedJson).match(request, parameters)
            assertFalse(actual.isExactMatch)
        }

        @Test
        @DisplayName("query has different field name")
        fun testUnmatchedDifferentFieldName() {
            val request = mockk<Request>()
            val parameters = Parameters()
            // language=json
            val json1 = """
            {
                "query": "{ hero { name friends { name }}}"
            }
        """.trimIndent()
            // language=json
            val json2 = """
            {
                "query": "{ hero { name friends { namea }}}"
            }
        """.trimIndent()

            every { request.bodyAsString } returns json1

            val actual = GraphqlBodyMatcher.withRequestJson(json2).match(request, parameters)
            assertFalse(actual.isExactMatch)
        }

        @Test
        @DisplayName("query is invalid JSON")
        fun testUnmatchedInvalidJson() {
            val request = mockk<Request>()
            val parameters = Parameters()
            // language=json
            val invalidQuery = """
            {
                "query": "{ hero { name, age, height "
            }
        """.trimIndent()

            every { request.bodyAsString } returns invalidQuery

            assertThrows<InvalidSyntaxException> {
                GraphqlBodyMatcher.withRequestJson(invalidQuery).match(request, parameters)
            }
        }

        @Test
        @DisplayName("json is empty")
        fun testUnmatchedEmptyJson() {
            val request = mockk<Request>()
            val parameters = Parameters()
            val emptyJson = ""

            every { request.bodyAsString } returns emptyJson

            assertThrows<JsonException> {
                GraphqlBodyMatcher.withRequestJson(emptyJson).match(request, mockk())
            }
        }

        @Test
        @DisplayName("query is empty")
        fun testUnmatchedEmptyQuery() {
            val request = mockk<Request>()
            val parameters = Parameters()
            val json = """{ "query": "" }"""

            every { request.bodyAsString } returns json

            assertThrows<InvalidSyntaxException> {
                GraphqlBodyMatcher.withRequestJson(json).match(request, parameters)
            }
        }

    }


    @Nested
    @DisplayName("test `withRequestQueryAndVariables`")
    inner class WithRequestQueryAndVariables {
        @Test
        @DisplayName("test `withRequest` when queries are identical")
        fun testMatchedIdenticalWithRequest() {
            val request = mockk<Request>()
            val parameters = Parameters()
            val query = "{ hero { name friends { name }}}"
            // language=json
            val json = """
            {
                "query": "{ hero { name friends { name }}}"
            }
        """.trimIndent()

            every { request.bodyAsString } returns json

            val actual = GraphqlBodyMatcher.withRequestQueryAndVariables(query).match(request, parameters)
            assertTrue(actual.isExactMatch)
        }

        @Test
        @DisplayName("test `withRequest` when queries and variables are identical")
        fun testMatchedIdenticalWithRequestAndVariables() {
            val request = mockk<Request>()
            val parameters = Parameters()
            val query = "query GetCharacters(\$ids: [ID!]) { characters(ids: \$ids) { name age } }"
            val variables = """{"ids": [1, 2, 3]}"""
            val escaped = "\$ids"

            val json = """
            {
                "query": "query GetCharacters($escaped: [ID!]) { characters(ids: $escaped) { name age } }",
                "variables": {
                    "ids": [
                        1,
                        2,
                        3
                    ]
                }
            }
        """.trimIndent()

            every { request.bodyAsString } returns json

            val actual = GraphqlBodyMatcher.withRequestQueryAndVariables(query, variables).match(request, parameters)
            assertTrue(actual.isExactMatch)
        }
    }

    @Nested
    @DisplayName("test `withRequest`")
    inner class WithRequestTest {
        @Test
        @DisplayName("returns Parameters with expectedJsonKey")
        fun testMatchedIdentical() {
            // language=json
            val json = """
            {
                "query": "{ hero { name friends { name }}}"
            }
        """.trimIndent()

            val actual = GraphqlBodyMatcher.withRequest(json)
            assertTrue(actual.containsKey("query"))
            assertEquals("{ hero { name friends { name }}}", actual.getString("query"))
            assertFalse(actual.containsKey("variables"))
            assertFalse(actual.containsKey("operationName"))
        }

        @Test
        @DisplayName("Throws JsonException when json is empty")
        fun testUnmatchedEmptyJson() {
            val emptyJson = ""

            assertThrows<JsonException> {
                GraphqlBodyMatcher.withRequest(emptyJson)
            }
        }

        @Test
        @DisplayName("Throws JsonException when json is invalid")
        fun testUnmatchedInvalidJson() {
            val invalidJson = """
            {
                "query": "{ hero { name, age, height }"
        """.trimIndent()

            assertThrows<JsonException> {
                GraphqlBodyMatcher.withRequest(invalidJson)
            }
        }

        @Test
        @DisplayName("Throws InvalidSyntaxException when query is invalid")
        fun testUnmatchedInvalidQuery() {
            // language=json
            val invalidQueryJson = """
            {
                "query": "{ hero { name, age, height "
            }
        """.trimIndent()

            assertThrows<InvalidSyntaxException> {
                GraphqlBodyMatcher.withRequest(invalidQueryJson)
            }
        }

        @Test
        @DisplayName("Throws InvalidSyntaxException when query is empty")
        fun testUnmatchedEmptyQuery() {
            val json = """{ "query": "" }"""

            assertThrows<InvalidSyntaxException> {
                GraphqlBodyMatcher.withRequest(json)
            }
        }
    }
}
