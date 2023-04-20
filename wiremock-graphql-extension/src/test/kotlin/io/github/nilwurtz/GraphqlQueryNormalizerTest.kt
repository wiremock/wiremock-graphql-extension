import graphql.parser.Parser
import io.github.nilwurtz.GraphqlQueryNormalizer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class GraphqlQueryNormalizerTest {

    @Test
    @DisplayName("Normalize GraphQL document with reordered fields")
    fun testNormalizeGraphqlDocumentReorderedFields() {
        val originalQuery = """
            query {
                company {
                    name
                    location
                }
                person {
                    name
                    age
                }
            }
        """.trimIndent()

        val reorderedQuery = """
            query {
                person {
                    age
                    name
                }
                company {
                    location
                    name
                }
            }
        """.trimIndent()

        val originalDocument = Parser().parseDocument(originalQuery)
        val reorderedDocument = Parser().parseDocument(reorderedQuery)

        val normalizedOriginalDocument = GraphqlQueryNormalizer.normalizeGraphqlDocument(originalDocument)
        val normalizedReorderedDocument = GraphqlQueryNormalizer.normalizeGraphqlDocument(reorderedDocument)

        assertEquals(normalizedOriginalDocument.toString(), normalizedReorderedDocument.toString())
    }
}
