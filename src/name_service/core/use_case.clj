(ns name-service.core.use-case
  "Core logic use-cases. Represents business logic of the application."
  (:require [com.walmartlabs.cond-let :refer [cond-let]]
            [name-service.core.action :as a]
            [name-service.core.entity :as e]))

(declare result->action)

(defn add-key-binding [storage key val]
  "Add value A for key X."
  (result->action ::e/add (e/save storage {key val} nil nil)))

(defn append-key-binding [storage key-a val-a key-b val-b]
  "Add a value A for key X, where key Y has value B."
  (cond-let
    :let [keymap (e/fetch storage key-b val-b)]

    (e/storage-error? keymap)
    (a/make-error ::e/append keymap)

    (contains? keymap key-a)
    (a/make-error ::e/append :key-exists)

    :let [result (e/save storage (assoc keymap key-a val-a) key-b val-b)]
    :else (result->action ::e/append result)))

(defn update-key-binding [storage key-a val-a key-b val-b]
  "Update key X to value A where key Y has value B or
  Update key X to value A where key X has value B."
  (cond-let
    :let [keymap (e/fetch storage key-b val-b)]

    (e/storage-error? keymap)
    (a/make-error ::e/update keymap)

    (contains? keymap key-a)
    (let [result (e/save storage (assoc keymap key-a val-a) key-b val-b)]
      (result->action ::e/update result))

    :else (a/make-error ::e/update :key-does-not-exist)))

(defn delete-key-binding [storage key-a key-b val-b]
  "Delete value for key X where key Y has value A."
  (cond-let
    :let [keymap (e/fetch storage key-b val-b)]

    (e/storage-error? keymap)
    (a/make-error ::e/delete-key-binding keymap)

    (contains? keymap key-a)
    (result->action
      ::e/delete-key-binding
      (e/save storage (dissoc keymap key-a) key-b val-b))

    :else (a/make-error ::e/delete-key-binding :key-does-not-exist)))

(defn delete-keymap [storage key val]
  "Delete keymap where key X has value A."
  (result->action ::e/delete-keymap (e/delete storage key val)))

(defn get-key [storage key-a key-b val-b]
  "Get value of key-a, where key-b has value val-b."
  (let [keymap (e/fetch storage key-b val-b)]
    (if (e/storage-error? keymap)
      (a/make-error ::e/get keymap)
      (result->action ::e/get (get keymap key-a)))))

(defn- result->action
  "Convert result of a storage action to action object."
  [type result]
  (let [payload {:result result}]
    (if (e/storage-error? result)
      (a/make-error type :storage payload)
      (a/make-action type payload))))
