# Web Api server

### Update key-binding

User can update existing key-binding.
This endpoint is responsible for "Updating key X to value A where key Y has value B".

* HTTP method: **PATCH**
* Method URL: http://localhost:8080/
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
    "error": null,
    "type": "update"
}
```

#### Errors:

* error from `clojure.spec.alpha`:
    * required arguments not given (update, where)
    * wrong number of key-value provided in arguments e.g. two pairs in where
    * keys and values are not in proper type
* key_does_not_exist - key for update not exist in keymap
* storage - keymap which is to be updated not exist

### Delete keymap or key-binding

* HTTP method: **DELETE**
* Method URL: http://localhost:8080/
* Accepted content types: `application/json`

#### Delete is supported for:

* key-binding - used to delete single key-binding from existing keymap
* keymap - used to delete a whole keymap

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
    "error": null,
    "type": "delete-key-binding"
}
```

#### Errors:

* error from `clojure.spec.alpha`:
    * required argument not given (where)
    * wrong number of key-value provided in where
    * keys and values are not in proper type
* key_does_not_exist - key for delete not exist in keymap
* storage - keymap not exist