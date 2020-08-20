# Core use-cases

### Add key-binding

This function is responsible for "Adding value A for key X" where key X is
represented by "key" and value A by "val", which causes creating the following 
keymap: `X-> A`. This use-case is used to create a brand new keymap with the first 
key-binding. The function returns an action (saves new keymap), which is based on the result 
returned from storage.

### Append key-binding

This function is responsible for "Adding a value A for key X, where key Y has value B.", 
where key X is represented by "key-a", value A by "val-a", key Y by "key-b" and 
value B by "val-b", which will result in following keymap being remembered:
```
X -> A
Y -> B
```
This use-case is used to append existing keymap by adding a new key-binding to
correlated key-value pair. First, function checks if keymap which is to be
expanded exists, and if not, function returns storage error. Otherwise, the
function checks, if the keymap contains the key to be added, and if it does, 
it returns an action, which is based on the result from storage with type `entity/append` 
that points type of error and value `key-exist` that points cause of an error.

### Update key-binding

This function is responsible for "Updating key X to value A where key Y has
value B" where key X is represented by "key-a", value A by "val-a", key Y by
"key-b" and value B by "val-b". This use-case also includes special case
which is "Updating key X to value A where key X has value B". First, function 
checks if keymap which is to be updated exist, and if not, function returns storage error. 
Otherwise, the function checks if the keymap contains the key to be updated, 
and if not, function returns `action/make-error` with type `entity/update` that points 
type of error and value `key-does-not-exist` that points cause of an error. 
If function contains the key to be updated, keymap is being replaced by the new 
keymap with updated key-binding. Next, the use-case returns action (saves updated keymap), 
which is based on the result returned from storage.

### Delete key-binding

This function is responsible for "Delete value for key X where key Y has value A" 
where key X is represented by "key-a", key Y by "key-b" and value A by "val-b". 
This use-case is used to delete single key-binding from existing keymap. 
Implementation of this function is similar to updating key-binding, that is the 
function updates the existing keymap by deleting given key-binding and saving 
a new keymap to the database.

### Delete keymap

This function is responsible for "Deleting keymap where key X has value A" 
where key X is represented by "key" and value A by "val". This use-case is used to 
delete a whole keymap. The use-case returns action (deletes keymap), which is based on the 
result returned from storage.

### Get key

This function is responsible for "Getting value of key-a, where key-b has value val-b". 
This use-case is used to query key-bindings. First, function checks if keymap which 
is looked for exist, and if not, function returns storage error. Otherwise, the function 
returns the value of the given key.