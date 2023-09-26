# With other mappings

## When using a mapping with this extension and normal mapping, graphql request should work
tags: remote
* Register a stub to return 200 upon receiving json <file:./fixtures/with-other/setup.json>
* Register a normal stub to return 200 upon receiving json "{\"foo\": \"bar\"}"
* Send a POST request to URL "/graphql" with body <file:./fixtures/with-other/request.json>
* The response status code should be "200"

## When using a mapping with this extension and normal mapping, normal request should work
tags: remote
* Register a stub to return 200 upon receiving json <file:./fixtures/with-other/setup.json>
* Register a normal stub to return 200 upon receiving json "{\"foo\": \"bar\"}"
* Send a POST request to URL "/graphql" with body "{\"foo\": \"bar\"}"
* The response status code should be "200"

## When using a mapping with this extension and normal mapping (change order), normal request should work
tags: remote
* Register a normal stub to return 200 upon receiving json "{\"foo\": \"bar\"}"
* Register a stub to return 200 upon receiving json <file:./fixtures/with-other/setup.json>
* Send a POST request to URL "/graphql" with body "{\"foo\": \"bar\"}"
* The response status code should be "200"
