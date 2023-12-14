package io.github.nilwurtz

import graphql.parser.InvalidSyntaxException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class EqualToGraphqlQueryPatternTest {

    @Test
    fun testMatchedIdentical() {
        val query = """
            {
              hero {
                name
                friends {
                  name
                }
              }
            }
        """.trimIndent()
        val pattern = EqualToGraphqlQueryPattern(query)
        val result = pattern.match(query)
        assertTrue(result.isExactMatch)
    }

    @Test
    fun testMatchedDifferentOrderSingleLevel() {
        val query1 = """
            {
              hero {
                name
                age
                height
              }
            }
        """.trimIndent()
        val query2 = """
            {
              hero {
                age
                height
                name
              }
            }
        """.trimIndent()
        val pattern = EqualToGraphqlQueryPattern(query1)
        val result = pattern.match(query2)
        assertTrue(result.isExactMatch)
    }

    @Test
    fun testMatchedDifferentOrderNested() {
        val query1 = """
            {
              hero {
                name
                friends {
                  name
                  friends {
                    name
                    age
                  }
                }
              }
            }
        """.trimIndent()
        val query2 = """
            {
              hero {
                name
                friends {
                  name
                  friends {
                    age
                    name
                  }
                }
              }
            }
        """.trimIndent()
        val pattern = EqualToGraphqlQueryPattern(query1)
        val result = pattern.match(query2)
        assertTrue(result.isExactMatch)
    }

    @Test
    fun testUnmatchedDifferentDepth() {
        val query1 = """
            {
              hero {
                name
                friends {
                  name
                }
              }
            }
        """.trimIndent()
        val query2 = """
            {
              hero {
                name
                friends {
                  name {
                    first
                    last
                  }
                }
              }
            }
        """.trimIndent()
        val pattern = EqualToGraphqlQueryPattern(query1)
        val result = pattern.match(query2)
        assertFalse(result.isExactMatch)
    }

    @Test
    fun testUnmatchedMissingField() {
        val query1 = """
            {
              hero {
                friends {
                  name
                }
              }
            }
        """.trimIndent()
        val query2 = """
            {
              hero {
                name
                friends {
                  name
                }
              }
            }
        """.trimIndent()
        val pattern = EqualToGraphqlQueryPattern(query1)
        val result = pattern.match(query2)
        assertFalse(result.isExactMatch)
    }

    @Test
    fun testUnmatchedAdditionalField() {
        val query1 = """
            {
              hero {
                name
                friends {
                  name
                }
              }
            }
        """.trimIndent()
        val query2 = """
            {
              hero {
                name
                friends {
                  namea
                }
              }
            }
        """.trimIndent()
        val pattern = EqualToGraphqlQueryPattern(query1)
        val result = pattern.match(query2)
        assertFalse(result.isExactMatch)
    }

    @Test
    fun testUnmatchedDifferentFieldName() {
        val query1 = """
            {
              hero {
                name
                friends {
                  name
                }
              }
            }
        """.trimIndent()
        val query2 = """
            {
              hero {
                name
                friends {
                  firstName
                }
              }
            }
        """.trimIndent()
        val pattern = EqualToGraphqlQueryPattern(query1)
        val result = pattern.match(query2)
        assertFalse(result.isExactMatch)
    }

    @Test
    fun testInvalidQuery() {
        val query = """
            {
              hero {
                name
                age
                height
        """.trimIndent()
        org.junit.jupiter.api.assertThrows<InvalidSyntaxException> {
            GraphqlBodyMatcher.parameters(query)
        }
    }
}
