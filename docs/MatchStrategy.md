# Match Strategy

## queries

The two queries below are considered matching:

```graphql
{
    hero {
        name
        friends {
            name
            age
        }
    }
}
```
```graphql
{
    hero {
        friends {
            age
            name
        }
        name
    }
}
```
But, these aren't:
```graphql
{
    hero {
        name
        friends {
            name
            age
        }
    }
}
```
```graphql
{
    hero {
        name
        friends {
            name
        }
    }
}
```

## Variables

Similar rules apply for variable matching based on `org.json.JsonObject.similar`.

```json
{
  "id": 1,
  "name": "John Doe"
}
```

```json
{
  "name": "John Doe",
  "id": 1
}
```

However, the following two variables do not match because the order of the arrays is different.

```json
{
  "ids": [1, 2, 3]
}
```
```json
{
  "ids": [3, 2, 1]
}
```