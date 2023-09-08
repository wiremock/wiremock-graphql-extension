# Comparing Graphql Request queries and variables

## When the variables match exactly, it matches
tags: remote
* Register a stub to return 200 upon receiving json <file:./fixtures/variables-match/setup.json>
* Send a POST request to URL "/graphql" with body <file:./fixtures/variables-match/request.json>
* The response status code should be "200"

## When the variables do not match exactly, it does not match
tags: remote
* Register a stub to return 200 upon receiving json <file:./fixtures/variables-not-match/setup.json>
* Send a POST request to URL "/graphql" with body <file:./fixtures/variables-not-match/request.json>
* The response status code should be "404"

## When the variable properties order differs but the structure matches, it matches
tags: remote
* Register a stub to return 200 upon receiving json <file:./fixtures/variables-complex-match/setup.json>
* Send a POST request to URL "/graphql" with body <file:./fixtures/variables-complex-match/request.json>
* The response status code should be "200"

## When the order of elements in the variable array differs, it does not match
tags: remote
* Register a stub to return 200 upon receiving json <file:./fixtures/variables-array-order-not-match/setup.json>
* Send a POST request to URL "/graphql" with body <file:./fixtures/variables-array-order-not-match/request.json>
* The response status code should be "404"

## When the query and variables match, it matches
* Register a stub to return 200 upon receiving the query<file:./fixtures/query-variables-match/setup-query.graphql> and variables<file:./fixtures/query-variables-match/setup-variables.json>
* Send a POST request to URL "/graphql" with body <file:./fixtures/query-variables-match/request.json>
* The response status code should be "200"
