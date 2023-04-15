import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import com.github.tomakehurst.wiremock.http.Request
import com.nilwurtz.GraphqlBodyMatcher
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertFalse

class GraphqlBodyMatcherTest {
    @Test
    @DisplayName("graphql query exactly matched")
    fun testExactlyMatched() {
        val request = mockk<Request>()
        // language=json
        val query = """
            {
                "query": "{ hero { name friends { name }}}"
            }
        """.trimIndent()

        every { request.bodyAsString } returns query
        val actual = GraphqlBodyMatcher(query).match(request, mockk())
        assertTrue(actual.isExactMatch)
    }


    @Test
    @DisplayName("graphql query not matched")
    fun testNotMatched() {
        val request = mockk<Request>()
        // language=json
        val query1 = """
            {
                "query": "{ hero { name friends { name }}}"
            }
        """.trimIndent()
        val query2 = """
            {
                "query": "{ hero { name friends { namea }}}"
            }
        """.trimIndent()

        every { request.bodyAsString } returns query1

        val actual = GraphqlBodyMatcher(query2).match(request, mockk())
        assertFalse(actual.isExactMatch)
    }
}