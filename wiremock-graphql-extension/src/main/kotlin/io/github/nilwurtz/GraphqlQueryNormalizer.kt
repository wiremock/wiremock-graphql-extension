package io.github.nilwurtz

import graphql.language.*

object GraphqlQueryNormalizer {
    fun normalizeGraphqlDocument(document: Document): Document {
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