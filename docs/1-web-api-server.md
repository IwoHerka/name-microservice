# Web API server

### Append (to) keymap

This endpoint is responsible for "Add a value `A` for key `X`, where key `Y` has value `B`". 
If the key-binding does not exist, a new keymap is created with the `Y` key and the `B` value.

* HTTP method: **POST**
* Method URL: `/`
* Accepted content types: `application/json`

#### Append is supported for:

* append - used to add single key-binding to existing keymap
* add - used to add new keymap

#### Request Body: 

* where (required)
    * must contain one pair key-value e.g. `Y`: `B`
* append (optional)
    * must contain one pair key-value for append e.g. `X`: `A`

```
{
    "where": {
        "Y": "B"
    },
    "append": {
         "X": "A"
    }
}
```

#### Response:
```
{
    "result": {
        "X": "A"
    },
    "error": null
}
```

When no include is specified, a new keymap is created.
```
{
    "where": {
        "Z": "C"
    }
}
```

#### Response:
```
{
    "result": {
        "Z": "C"
    },
    "error": null
}
```

#### Errors:
* error from `clojure.spec.alpha`:
    * required argument not given (where),
    * wrong number of key-value provided in where,
    * keys and values are not in proper type,
* `key_exists` - key for append is exist in keymap,
* storage - there is no keymap to which we want to append a key-value pair.

## Update key-binding

User can update existing key-binding.
This endpoint is responsible for "Updating key X to value A where key Y has value B".

* HTTP method: **PATCH**
* Method URL: `/`
* Accepted content types: `application/json`

#### Request Body: 

* update (required)
    * must contain one pair key-value e.g. "jira": "new-name2@gmail.com" 
* where (required)
    * must contain one pair key-value e.g. "slack": "name1@gmail.com"

```
{
    "update": {
        "jira": "new-name2@gmail.com"
    },
    "where": {
        "slack": "name1@gmail.com"
    }
}
```

#### Response:
```
{
    "result": {
        "slack": "name1@gmail.com",
        "jira": "new-name2@gmail.com"
    },
    "error": null
}
```

#### Errors:

* error from `clojure.spec.alpha`:
    * required arguments not given (update, where)
    * wrong number of key-value provided in arguments e.g. two pairs in where
    * keys and values are not in proper type
* `key_does_not_exist` - key for update not exist in keymap
* `storage` - keymap which is to be updated not exist

### Delete keymap or key-binding

* HTTP method: **DELETE**
* Method URL: `/`
* Accepted content types: `application/json`

#### Delete is supported for:

* `key-binding` - used to delete single key-binding from existing keymap
* `keymap` - used to delete a whole keymap

#### Request Body: 

* delete 
    * must contain one key for delete e.g. "jira" 
* where (required)
    * must contain one pair key-value e.g. "slack": "name1@gmail.com"

When delete is not given, whole keymap would be deleted.

```
{
    "delete": "jira",
    "where": {
        "slack": "name1@gmail.com"
    }
}
```

#### Response:
```
{
    "result": {
        "slack": "name1@gmail.com"
    },
    "error": null
}
```

#### Errors:

* error from `clojure.spec.alpha`:
    * required argument not given (where)
    * wrong number of key-value provided in where
    * keys and values are not in proper type
* `key_does_not_exist` - key for delete not exist in keymap
* `storage` - keymap not exist

### Querying key-bindings

This endpoint is responsible for "Get value of key X where key Y has value A".

* HTTP method: **GET**
* Method URL: `/<key>?<y>=<a>`

#### Request Body: 

* `<key>` (required) - must contain one key for querying e.g. `X` 
* `<y>=<a>` (optional) - must contain one pair key-value e.g. `Y`: `A`

``` 
   GET /X?Y=B
```

#### Response:

```
{
    "result": "A"
    "error": null
}
```

#### Errors:

* error from `clojure.spec.alpha`:
    * required path-params (key),
    * required query-params (only one pair key-value),
* storage - keymap not exist.
