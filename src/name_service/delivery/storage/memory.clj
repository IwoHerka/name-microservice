(ns name-service.delivery.storage.memory
  "Storage dev/test utilities."
  (:require [name-service.core.entity :as entity]))

(defn- fetch
  [db key val]
  (first (filter #(= val (get % key)) @db)))

(defn- delete
  [db key val]
  (let [keymap (fetch db key val)]
    (swap! db disj keymap)))

(defn- save
  [db keymap key val]
  (if (and key val)
    (let [old-keymap (fetch db key val)]
      (if (> (count old-keymap) 0)
        (delete db key val))))
  (swap! db conj keymap)
  keymap)

; Atom-based in-memory storage.
; This storage expects (and works only with) atoms with value of a set.
(defrecord MemoryStorage [db]
  entity/KeyMapStorage
  (-fetch [_ key val] (fetch db key val))
  (-save [_ keymap key val] (save db keymap key val))
  (-delete [_ key val] (delete db key val)))

(defn make-storage []
  (->MemoryStorage (atom #{})))
