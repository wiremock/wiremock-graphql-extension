package io.github.nilwurtz

import com.github.tomakehurst.wiremock.matching.ContentPattern
import com.github.tomakehurst.wiremock.matching.MatchResult
import com.github.tomakehurst.wiremock.stubbing.SubEvent
import graphql.language.AstComparator
import graphql.language.AstSorter
import graphql.language.Document
import graphql.parser.InvalidSyntaxException
import graphql.parser.Parser

class EqualToGraphqlQueryPattern(expectedValue: String) : ContentPattern<String>(expectedValue) {

    private val expectedDocument = expectedValue.parse().sort()

    override fun match(requestValue: String?): MatchResult {
        return try {
            val requestDocument = requestValue?.parse()?.sort()
            if (AstComparator.isEqual(expectedDocument, requestDocument)) {
                MatchResult.exactMatch()
            } else {
                MatchResult.noMatch()
            }
        } catch (e: InvalidSyntaxException) {
            MatchResult.noMatch(SubEvent.warning(e.message))
        }
    }

    override fun getName(): String {
        return "equalToGraphqlQuery"
    }

    override fun getExpected(): String {
        return expectedValue
    }
}

private fun String.parse(): Document {
    return Parser().parseDocument(this)
}

private fun Document.sort(): Document {
    return AstSorter().sort(this);
}
