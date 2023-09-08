# Comparing Graphql Request Queries

## When JSON perfectly matches, it should match
tags: remote
* Register a stub to return 200 upon receiving json <file:./fixtures/exact-match/setup.json>
* Send a POST request to URL "/graphql" with body <file:./fixtures/exact-match/request.json>
* The response status code should be "200"

## When JSON does not perfectly match, it should not match
tags: remote
* Register a stub to return 200 upon receiving json <file:./fixtures/not-match/setup.json>
* Send a POST request to URL "/graphql" with body <file:./fixtures/not-match/request.json>
* The response status code should be "404"

## When JSON has different order, it should match
tags: remote
* Register a stub to return 200 upon receiving json <file:./fixtures/order-match/setup.json>
* Send a POST request to URL "/graphql" with body <file:./fixtures/order-match/request.json>
* The response status code should be "200"

## When JSON uses aliases, it should match
tags: remote
* Register a stub to return 200 upon receiving json <file:./fixtures/alias-match/setup.json>
* Send a POST request to URL "/graphql" with body <file:./fixtures/alias-match/request.json>
* The response status code should be "200"

## When JSON uses different aliases, it should not match
tags: remote
* Register a stub to return 200 upon receiving json <file:./fixtures/alias-not-match/setup.json>
* Send a POST request to URL "/graphql" with body <file:./fixtures/alias-not-match/request.json>
* The response status code should be "404"

## When JSON uses the same fragments, it should match
tags: remote
* Register a stub to return 200 upon receiving json <file:./fixtures/fragment-match/setup.json>
* Send a POST request to URL "/graphql" with body <file:./fixtures/fragment-match/request.json>
* The response status code should be "200"

## When JSON uses different fragment names for the same set, it should match
tags: remote
* Register a stub to return 200 upon receiving json <file:./fixtures/fragment-name-match/setup.json>
* Send a POST request to URL "/graphql" with body <file:./fixtures/fragment-name-match/request.json>
* The response status code should be "200"

## When JSON uses the same fragment names for different sets, it should not match
tags: remote
* Register a stub to return 200 upon receiving json <file:./fixtures/fragment-set-not-match/setup.json>
* Send a POST request to URL "/graphql" with body <file:./fixtures/fragment-set-not-match/request.json>
* The response status code should be "404"

## When the query contains multiple fragments, it should match
tags: remote
* Register a stub to return 200 upon receiving json <file:./fixtures/multiple-fragments-match/setup.json>
* Send a POST request to URL "/graphql" with body <file:./fixtures/multiple-fragments-match/request.json>
* The response status code should be "200"

## When the query contains different sets with multiple fragments, it should not match
tags: remote
* Register a stub to return 200 upon receiving json <file:./fixtures/multiple-fragments-not-match/setup.json>
* Send a POST request to URL "/graphql" with body <file:./fixtures/multiple-fragments-not-match/request.json>
* The response status code should be "404"

## When JSON has the same arguments, it should match
tags: remote
* Register a stub to return 200 upon receiving json <file:./fixtures/argument-match/setup.json>
* Send a POST request to URL "/graphql" with body <file:./fixtures/argument-match/request.json>
* The response status code should be "200"

## When JSON has different arguments, it should not match
tags: remote
* Register a stub to return 200 upon receiving json <file:./fixtures/argument-not-match/setup.json>
* Send a POST request to URL "/graphql" with body <file:./fixtures/argument-not-match/request.json>
* The response status code should be "404"

## Specifying the query should match
* Register a stub to return 200 upon receiving the query<file:./fixtures/query-match/setup-query.graphql>
* Send a POST request to URL "/graphql" with body <file:./fixtures/query-match/request.json>
* The response status code should be "200"
