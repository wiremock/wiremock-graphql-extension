package io.github.nilwurtz

import com.github.tomakehurst.wiremock.extension.Parameters
import com.github.tomakehurst.wiremock.http.Request
import com.github.tomakehurst.wiremock.matching.MatchResult
import com.github.tomakehurst.wiremock.matching.RequestMatcherExtension
import graphql.language.*
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

        operationDefinition.selectionSet?.let { selectionSet ->
            val normalizedSelectionSet = normalizeSelectionSet(selectionSet)
            operationBuilder.selectionSet(normalizedSelectionSet)
        }

        return operationBuilder.build()
    }

    private fun normalizeSelectionSet(selectionSet: SelectionSet): SelectionSet {
        val selectionBuilder = SelectionSet.newSelectionSet()
        val sortedSelections = selectionSet.selections.sortedBy { it.toString() }

        sortedSelections.forEach { selection ->
            when (selection) {
                is Field -> {
                    val normalizedField = normalizeField(selection)
                    selectionBuilder.selection(normalizedField)
                }
                is InlineFragment -> {
                    val normalizedInlineFragment = normalizeInlineFragment(selection)
                    selectionBuilder.selection(normalizedInlineFragment)
                }
                is FragmentSpread -> {
                    // Assuming FragmentSpread does not need to be normalized
                    selectionBuilder.selection(selection)
                }
            }
        }

        return selectionBuilder.build()
    }

    private fun normalizeField(field: Field): Field {
        val fieldBuilder = Field.newField()

        fieldBuilder.name(field.name)
        fieldBuilder.alias(field.alias)
        fieldBuilder.arguments(field.arguments)

        // If field has a selection set, normalize it
        field.selectionSet?.let { selectionSet ->
            val normalizedSelectionSet = normalizeSelectionSet(selectionSet)
            fieldBuilder.selectionSet(normalizedSelectionSet)
        }

        return fieldBuilder.build()
    }

    private fun normalizeInlineFragment(inlineFragment: InlineFragment): InlineFragment {
        val inlineFragmentBuilder = InlineFragment.newInlineFragment()

        inlineFragment.typeCondition?.let { inlineFragmentBuilder.typeCondition(it) }

        val normalizedSelectionSet = normalizeSelectionSet(inlineFragment.selectionSet)
        inlineFragmentBuilder.selectionSet(normalizedSelectionSet)

        return inlineFragmentBuilder.build()
    }
}