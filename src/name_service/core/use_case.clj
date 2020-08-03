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
  "Add value 'val' for key 'key'."
  (fn [key val]))

(defn update-key-binding [{:keys [storage]}]
  "Update value of key-a to val-b where key-b has value val-b."
  (fn [key-a val-a key-b val-b]))

(defn delete-key-binding [{:keys [storage]}]
  "Delete key 'key' where key 'key' has value 'val'."
  (fn [key val]))

(defn get-key [{:keys [storage]}]
  "Get value of key-a, where key-b has value val-b."
  (fn [key-a key-b val-b]))