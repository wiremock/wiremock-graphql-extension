package com.nilwurtz

import com.github.tomakehurst.wiremock.extension.Parameters
import com.github.tomakehurst.wiremock.http.Request
import com.github.tomakehurst.wiremock.matching.MatchResult
import com.github.tomakehurst.wiremock.matching.RequestMatcherExtension
import graphql.language.Document
import graphql.language.OperationDefinition
import graphql.language.SelectionSet
import graphql.parser.Parser
import org.json.JSONObject

class GraphqlBodyMatcher() : RequestMatcherExtension() {
    private lateinit var expectedRequestJson: JSONObject

    constructor(expectedJson: String): this() {
        try {
            expectedRequestJson = JSONObject(expectedJson)
        } catch (e: Exception) {
            println(e)
        }
    }

    override fun match(p0: Request, p1: Parameters): MatchResult {
        val requestBody = p0.bodyAsString
        val requestJson = JSONObject(requestBody)

        val requestQuery =
            requestJson.getString("query").let { Parser().parseDocument(it) }.let { normalizeGraphqlDocument(it) }
        val expectedQuery =
            expectedRequestJson.getString("query").let { Parser().parseDocument(it) }.let { normalizeGraphqlDocument(it) }

        return if (requestQuery.toString() == expectedQuery.toString()){
            MatchResult.exactMatch()
        } else {
            MatchResult.noMatch()
        }
    }

    override fun getName(): String {
        return "graphql-body-matcher"
    }

    private fun normalizeGraphqlDocument(document: Document): Document {
        val documentBuilder: Document.Builder = Document.newDocument()
        document.definitions.forEach { definition ->
            if (definition is OperationDefinition) {
                documentBuilder.definition(normalizeOperationDefinition(definition))
            } else {
                documentBuilder.definition(definition)
            }
        }
        return documentBuilder.build()
    }

    private fun normalizeOperationDefinition(operationDefinition: OperationDefinition): OperationDefinition {
        val operationBuilder = OperationDefinition.newOperationDefinition()
        operationBuilder.selectionSet(
            operationDefinition.selectionSet.selections.sortedBy { it.toString() }
                .let { selections -> SelectionSet.newSelectionSet(selections).build() }
        )
        return operationBuilder.build()
    }
}