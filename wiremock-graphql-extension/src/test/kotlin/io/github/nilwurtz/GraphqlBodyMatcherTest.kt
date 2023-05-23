package io.github.nilwurtz

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import com.github.tomakehurst.wiremock.http.Request
import com.github.tomakehurst.wiremock.extension.Parameters
import io.github.nilwurtz.exceptions.InvalidJsonException
import io.github.nilwurtz.exceptions.InvalidQueryException
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows

class GraphqlBodyMatcherTest {
    @Test
    @DisplayName("queries are identical")
    fun testMatchedIdentical() {
        val request = mockk<Request>()
        val parameters = mockk<Parameters>()
        // language=json
        val json = """
            {
                "query": "{ hero { name friends { name }}}"
            }
        """.trimIndent()

        every { request.bodyAsString } returns json
        every { parameters.containsKey("expectedQuery") } returns false
        val actual = GraphqlBodyMatcher.withRequestJson(json).match(request, parameters)
        assertTrue(actual.isExactMatch)
    }

    @Test
    @DisplayName("test `withRequest` when queries are identical")
    fun testMatchedIdenticalWithRequest() {
        val request = mockk<Request>()
        val parameters = mockk<Parameters>()
        val query = "{ hero { name friends { name }}}"
        // language=json
        val json = """
            {
                "query": "{ hero { name friends { name }}}"
            }
        """.trimIndent()

        every { request.bodyAsString } returns json
        every { parameters.containsKey("expectedQuery") } returns false
        val actual = GraphqlBodyMatcher.withRequestQueryAndVariables(query).match(request, parameters)
        assertTrue(actual.isExactMatch)
    }

    @Test
    @DisplayName("test `withRequest` when queries and variables are identical")
    fun testMatchedIdenticalWithRequestAndVariables() {
        val request = mockk<Request>()
        val parameters = mockk<Parameters>()
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
        every { parameters.containsKey("expectedQuery") } returns false
        val actual = GraphqlBodyMatcher.withRequestQueryAndVariables(query, variables).match(request, parameters)
        assertTrue(actual.isExactMatch)
    }

    @Test
    @DisplayName("query has different order in single level")
    fun testMatchedDifferentOrderSingleLevel() {
        val request = mockk<Request>()
        val parameters = mockk<Parameters>()
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
        every { parameters.containsKey("expectedQuery") } returns false
        val actual = GraphqlBodyMatcher.withRequestJson(expectedJson).match(request, parameters)
        assertTrue(actual.isExactMatch)
    }


    @Test
    @DisplayName("graphql query has different order so nested")
    fun testMatchedDifferentOrderNested() {
        val request = mockk<Request>()
        val parameters = mockk<Parameters>()
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
        every { parameters.containsKey("expectedQuery") } returns false

        val actual = GraphqlBodyMatcher.withRequestJson(expectedJson).match(request, parameters)
        assertTrue(actual.isExactMatch)
    }

    @Test
    @DisplayName("query has different depth")
    fun testUnmatchedDifferentDepth() {
        val request = mockk<Request>()
        val parameters = mockk<Parameters>()
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
        every { parameters.containsKey("expectedQuery") } returns false

        val actual = GraphqlBodyMatcher.withRequestJson(expectedJson).match(request, parameters)
        assertFalse(actual.isExactMatch)
    }

    @Test
    @DisplayName("query is missing a field")
    fun testUnmatchedMissingField() {
        val request = mockk<Request>()
        val parameters = mockk<Parameters>()
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
        every { parameters.containsKey("expectedQuery") } returns false

        val actual = GraphqlBodyMatcher.withRequestJson(expectedJson).match(request, parameters)
        assertFalse(actual.isExactMatch)
    }

    @Test
    @DisplayName("query has additional field")
    fun testUnmatchedAdditionalField() {
        val request = mockk<Request>()
        val parameters = mockk<Parameters>()
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
        every { parameters.containsKey("expectedQuery") } returns false

        val actual = GraphqlBodyMatcher.withRequestJson(expectedJson).match(request, parameters)
        assertFalse(actual.isExactMatch)
    }

    @Test
    @DisplayName("query has different field name")
    fun testUnmatchedDifferentFieldName() {
        val request = mockk<Request>()
        val parameters = mockk<Parameters>()
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
        every { parameters.containsKey("expectedQuery") } returns false

        val actual = GraphqlBodyMatcher.withRequestJson(json2).match(request, parameters)
        assertFalse(actual.isExactMatch)
    }

    @Test
    @DisplayName("query is invalid JSON")
    fun testUnmatchedInvalidJson() {
        val request = mockk<Request>()
        val parameters = mockk<Parameters>()
        // language=json
        val invalidQuery = """
            {
                "query": "{ hero { name, age, height "
            }
        """.trimIndent()

        every { request.bodyAsString } returns invalidQuery
        every { parameters.containsKey("expectedQuery") } returns false

        assertThrows<InvalidQueryException> {
            GraphqlBodyMatcher.withRequestJson(invalidQuery).match(request, parameters)
        }
    }

    @Test
    @DisplayName("json is empty")
    fun testUnmatchedEmptyJson() {
        val request = mockk<Request>()
        val parameters = mockk<Parameters>()
        val emptyJson = ""

        every { request.bodyAsString } returns emptyJson
        every { parameters.containsKey("expectedQuery") } returns false

        assertThrows<InvalidJsonException> {
            GraphqlBodyMatcher.withRequestJson(emptyJson).match(request, mockk())
        }
    }

    @Test
    @DisplayName("query is empty")
    fun testUnmatchedEmptyQuery() {
        val request = mockk<Request>()
        val parameters = mockk<Parameters>()
        val json = """{ "query": "" }"""

        every { request.bodyAsString } returns json
        every { parameters.containsKey("expectedQuery") } returns false

        assertThrows<InvalidQueryException> {
            GraphqlBodyMatcher.withRequestJson(json).match(request, parameters)
        }
    }

}
