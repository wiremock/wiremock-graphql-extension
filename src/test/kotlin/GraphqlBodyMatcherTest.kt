import com.github.tomakehurst.wiremock.extension.Parameters
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import com.github.tomakehurst.wiremock.http.Request
import io.mockk.every
import io.mockk.mockk
import org.junit.Ignore
import org.junit.jupiter.api.Assertions.assertFalse

class GraphqlBodyMatcherTest {
    @Test
    @DisplayName("graphql query exactly matched")
    fun testExactlyMatched() {
        val request = mockk<Request>()
        val parameter = mockk<Parameters>()
        // language=json
        val query = """
            {
                "query": "{ hero { name friends { name }}}"
            }
        """.trimIndent()

        every { request.bodyAsString } returns query
        every { parameter["body"] } returns query


        val actual = GraphqlBodyMatcher().match(request, parameter)
        assertTrue(actual.isExactMatch)
    }

    @Ignore
    @Test
    @DisplayName("graphql query not matched")
    fun testNotMatched() {
        val request = mockk<Request>()
        val parameter = mockk<Parameters>()
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
        every { parameter["body"] } returns query2


        val actual = GraphqlBodyMatcher().match(request, parameter)
        assertFalse(actual.isExactMatch)
    }
}