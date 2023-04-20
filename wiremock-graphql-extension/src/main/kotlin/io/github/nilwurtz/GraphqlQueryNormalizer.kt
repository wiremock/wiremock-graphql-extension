package io.github.nilwurtz

import graphql.language.*

object GraphqlQueryNormalizer {
    fun normalizeGraphqlDocument(document: Document): Document {
        val documentBuilder: Document.Builder = Document.newDocument()
        document.definitions.forEach { definition ->
            when (definition) {
                is OperationDefinition -> {
                    documentBuilder.definition(normalizeOperationDefinition(definition))
                }
                is FragmentDefinition -> {
                    documentBuilder.definition(normalizeFragmentDefinition(definition))
                }
                else -> {
                    documentBuilder.definition(definition)
                }
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

    private fun normalizeFragmentDefinition(fragmentDefinition: FragmentDefinition): FragmentDefinition {
        val fragmentDefinitionBuilder = FragmentDefinition.newFragmentDefinition()
            .typeCondition(fragmentDefinition.typeCondition)

        val normalizedSelectionSet = normalizeSelectionSet(fragmentDefinition.selectionSet)
        fragmentDefinitionBuilder.selectionSet(normalizedSelectionSet)

        return fragmentDefinitionBuilder.build()
    }

    private fun normalizeSelectionSet(selectionSet: SelectionSet): SelectionSet {
        val selectionBuilder = SelectionSet.newSelectionSet()

        val normalizedSelections = selectionSet.selections.map { selection ->
            when (selection) {
                is Field -> normalizeField(selection)
                is InlineFragment -> normalizeInlineFragment(selection)
                is FragmentSpread -> FragmentSpread.newFragmentSpread()
                    .name("normalizedFragmentName") // TODO: support multiple fragment
                    .directives(selection.directives)
                    .build()
                else -> selection
            }
        }.sortedBy { it.toString() }

        normalizedSelections.forEach { selection ->
            selectionBuilder.selection(selection)
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