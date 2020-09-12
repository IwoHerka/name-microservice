# mongoDB

Microservice is using [Clojure Monger](http://clojuremongodb.info/) to connect with MongoDB.

## Usage

##### make-storage
To connect, use `make-storage` with `host port db-name coll-name` arguments.
All arguments should be a string value.

`(make-storage {:host "127.0.0.1", :port "27017", :db-name "db-name", :coll-name "coll-name"})`

##### fetch
To find, use `fetch` with `storeage key val` arguments.

`(fetch store "key" "val")`

In return, you should get a map with found values. If wanted keybinding doesn't exist You get `nil`

##### delete
To remove, use `delete` with `storage key val` arguments.
All arguments should be a string value.

`(delete store "key" "val")`

##### save
Save function can insert/replace the provided document.
Following function getting `storage keymap key val` arguments.
If keybinding doesn't already exist function insert provided keymap.
If keybinding does exist function replace found keymap with provided keymap.

`(delete store {"key" "val" "key2" "val2"} "key" "val")`
