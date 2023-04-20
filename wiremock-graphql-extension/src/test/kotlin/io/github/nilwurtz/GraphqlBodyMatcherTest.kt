package io.github.nilwurtz

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import com.github.tomakehurst.wiremock.http.Request
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
        // language=json
        val json = """
            {
                "query": "{ hero { name friends { name }}}"
            }
        """.trimIndent()

        every { request.bodyAsString } returns json
        val actual = GraphqlBodyMatcher.withRequestJson(json).match(request, mockk())
        assertTrue(actual.isExactMatch)
    }

    @Test
    @DisplayName("test `withRequestQuery` when queries are identical")
    fun testMatchedIdenticalWithQuery() {
        val request = mockk<Request>()
        val query = "{ hero { name friends { name }}}"
        // language=json
        val json = """
            {
                "query": "{ hero { name friends { name }}}"
            }
        """.trimIndent()

        every { request.bodyAsString } returns json
        val actual = GraphqlBodyMatcher.withRequestQuery(query).match(request, mockk())
        assertTrue(actual.isExactMatch)
    }

    @Test
    @DisplayName("query has different order in single level")
    fun testMatchedDifferentOrderSingleLevel() {
        val request = mockk<Request>()
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
        val actual = GraphqlBodyMatcher.withRequestJson(expectedJson).match(request, mockk())
        assertTrue(actual.isExactMatch)
    }


    @Test
    @DisplayName("graphql query has different order so nested")
    fun testMatchedDifferentOrderNested() {
        val request = mockk<Request>()
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

        val actual = GraphqlBodyMatcher.withRequestJson(expectedJson).match(request, mockk())
        assertTrue(actual.isExactMatch)
    }

    @Test
    @DisplayName("query has different depth")
    fun testUnmatchedDifferentDepth() {
        val request = mockk<Request>()
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

        val actual = GraphqlBodyMatcher.withRequestJson(expectedJson).match(request, mockk())
        assertFalse(actual.isExactMatch)
    }

    @Test
    @DisplayName("query is missing a field")
    fun testUnmatchedMissingField() {
        val request = mockk<Request>()
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

        val actual = GraphqlBodyMatcher.withRequestJson(expectedJson).match(request, mockk())
        assertFalse(actual.isExactMatch)
    }

    @Test
    @DisplayName("query has additional field")
    fun testUnmatchedAdditionalField() {
        val request = mockk<Request>()
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

        val actual = GraphqlBodyMatcher.withRequestJson(expectedJson).match(request, mockk())
        assertFalse(actual.isExactMatch)
    }

    @Test
    @DisplayName("query has different field name")
    fun testUnmatchedDifferentFieldName() {
        val request = mockk<Request>()
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

        val actual = GraphqlBodyMatcher.withRequestJson(json2).match(request, mockk())
        assertFalse(actual.isExactMatch)
    }

    @Test
    @DisplayName("query is invalid JSON")
    fun testUnmatchedInvalidJson() {
        val request = mockk<Request>()
        // language=json
        val invalidQuery = """
            {
                "query": "{ hero { name, age, height "
            }
        """.trimIndent()

        every { request.bodyAsString } returns invalidQuery

        assertThrows<InvalidQueryException> {
            GraphqlBodyMatcher.withRequestJson(invalidQuery).match(request, mockk())
        }
    }

    @Test
    @DisplayName("json is empty")
    fun testUnmatchedEmptyJson() {
        val request = mockk<Request>()
        val emptyJson = ""

        every { request.bodyAsString } returns emptyJson

        assertThrows<InvalidJsonException> {
            GraphqlBodyMatcher.withRequestJson(emptyJson).match(request, mockk())
        }
    }

    @Test
    @DisplayName("query is empty")
    fun testUnmatchedEmptyQuery() {
        val request = mockk<Request>()
        val json = """{ "query": "" }"""

        every { request.bodyAsString } returns json

        assertThrows<InvalidQueryException> {
            GraphqlBodyMatcher.withRequestJson(json).match(request, mockk())
        }
    }

}