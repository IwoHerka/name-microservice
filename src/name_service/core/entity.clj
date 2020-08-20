(ns name-service.core.entity)

(defprotocol KeyMapStorage
  (-fetch [this key val] "Fetch key map")
  (-save [this keymap key val] "Save key map")
  (-delete [this key val] "Removes key-binding from storage"))

(defn fetch
  "Fetch keymap from storage."
  [storage key val]
  (-fetch storage key val))

(defn save
  "Save keymap to storage. Inserts if new (and (= key nil) (= val nil)),
  otherwise updates an existing binding."
  [storage keymap key val]
  (-save storage keymap key val))

(defn delete
  "Removes a bindings from storage by ID."
  [storage key val]
  (-delete storage key val))

(defn storage-error?
  "Check if the given storage result was an error."
  [result]
  (keyword? result))