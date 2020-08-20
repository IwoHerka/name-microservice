(ns name-service.core.use-case
  (:require [name-service.core.entity :as entity]
            [name-service.core.action :as action]))

(defn- result->action
  [type result]
  (let [payload {:result result}]
    (if (entity/storage-error? result)
      (action/make-error type :storage payload)
      (action/make-action type payload))))

(defn add-key-binding [{:keys [storage]}]
  "Add value A for key X."
  (fn [key val]
    (let [keymap {key val}]
      (result->action :entity/add
        (entity/save storage keymap nil nil)))))

(defn append-key-binding [{:keys [storage]}]
  "Add a value A for key X, where key Y has value B."
  (fn [key-a val-a key-b val-b]
    (let [keymap (entity/fetch storage key-b val-b)]
      (if (entity/storage-error? keymap)
        (action/make-error :entity/append keymap)
        (if (contains? keymap key-a)
          (action/make-error :entity/append :key-exists)
          (result->action :entity/append
            (entity/save storage
              (assoc keymap key-a val-a) key-b val-b)))))))

(defn update-key-binding [{:keys [storage]}]
  "Update key X to value A where key Y has value B or
  Update key X to value A where key X has value B"
  (fn [key-a val-a key-b val-b]
    (let [keymap (entity/fetch storage key-b val-b)]
      (if (entity/storage-error? keymap)
        (action/make-error :entity/update keymap)
        (if (contains? keymap key-a)
          (result->action :entity/update
            (entity/save storage
              (assoc keymap key-a val-a) key-b val-b))
          (action/make-error :entity/update :key-does-not-exist))))))

(defn delete-key-binding [{:keys [storage]}]
  "Delete value for key X where key Y has value A"
  (fn [key-a key-b val-b]
    (let [keymap (entity/fetch storage key-b val-b)]
      (if (entity/storage-error? keymap)
        (action/make-error :entity/delete-key-binding keymap)
        (if (contains? keymap key-a)
          (result->action :entity/delete-key-binding
            (entity/save storage
              (dissoc keymap key-a) key-b val-b))
          (action/make-error :entity/delete-key-binding :key-does-not-exist))))))

(defn delete-keymap [{:keys [storage]}]
  "Delete keymap where key X has value A"
  (fn [key val]
    (result->action :entity/delete-keymap
      (entity/delete storage key val))))

(defn get-key [{:keys [storage]}]
  "Get value of key-a, where key-b has value val-b"
  (fn [key-a key-b val-b]
    (let [keymap (entity/fetch storage key-b val-b)]
      (if (entity/storage-error? keymap)
        (action/make-error :entity/get keymap)
        (result->action :entity/get
          (get keymap key-a))))))