## 0. Rationale

Mikroserwis powinien mieć możliwość przypisania mapowania unikatowych ID userów w różnych serwisach do siebie. Jego klienci zadawaliby pytanie w stylu:
– podaj proszę identyfikator w serwisie B, jeśli mam w serwisie A identyfikator I i dostawali odpowiedni output.

## 1. Entity model

We define three basic concepts:

### a. Key

Simply put, key is a string which identifies some service of interest such as Slack or JIRA.
Example keys: "slack", "jira".

### b. Value

String which defines value of interest for some key. Example: "john.doe" or "johndoe@gmail.com".

### c. Key-binding

Relation between a key and a value, a pair. Example: `"slack" -> "john.doe"`.

### d. Keymap

Keymap is a set of key-bindings. Informally, each keymap is related to some user.
In keymap, key-bindings are *unique*, which means that for any keymap and particular key A, there can be 
only one value X. Example of *invalid* keymap:

```
"slack" -> "john.doe"
"slack" -> "doejohn"
"jira" -> "johndoe@gmail.com"
```

However, the following two keymaps are valid:

```
1. "slack" -> "doejohn"

2. "slack" -> "john.doe"
   "jira" -> "johndoe@gmail.com"
```

Additionally, any key-binding must uniqely identify keymap, which means that
if given value for some key, it must point to at most one keymap.

## 2. Use cases

### a. Append (to) keymap

Client should be able to add new key-binding, which is a key-value pair.
He/she should be able to specify that such binding should be correlated
with other key-value pair. Example:

"Add a value A for key X, where key Y has value B."

Which will result in following keymap being remembered (if there already
exists keymap with value B for Y):

```
X -> A
Y -> B
```

In certain situation we can also simply specify:

"Add value A for key X"

- which should add new keymap ("X -> A") to the store.

### b. Update existing keymap

Client should be able to update existing keymaps. Same rules apply 
as above. Example query could look like:

"Update key X to value A where key Y has value B".

Notice the special case:

"Update key X to value A where key X has value B".

### c. Delete (from) existing keymap

Client should be able to delete information: both
specific keys and whole keymaps. Example queries:

"Delete keymap where key X has value A"

or:

"Delete value for key X where key Y has value A"

### d. Querying key-bindings

Finally, client should be able to retrieve information:

"Get value of key X where key Y has value A."
