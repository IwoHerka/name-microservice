(ns name-service.delivery.storage.mongo
  (:require [clojure.spec.alpha :as s]
            [clojure.walk :as w]
            [monger.collection :as mc]
            [monger.core :as mg]
            [name-service.core.entity :as entity]
            [name-service.core.spec :as cs]))

(defn- fetch
  [{:keys [db coll-name]} key val]
  (-> (mc/find-one-as-map db coll-name {key val})
      (dissoc :_id)
      w/stringify-keys))

(defn- save
  [{:keys [db coll-name] :as storage} keymap key val]
  (if (s/valid? ::cs/keymap keymap)
    (if (and key val)
      (-> (if (empty? (fetch storage key val))
            (mc/insert-and-return db coll-name keymap)
            (mc/find-and-modify db
                                coll-name
                                {key val}
                                keymap
                                {:return-new true}))
          (dissoc :_id)
          w/stringify-keys)
      (->
        (mc/insert-and-return db coll-name keymap)
        (dissoc :_id)
        w/stringify-keys))
      :not-valid))

(defn- delete
  [{:keys [db coll-name]} key val]
  (if (and (s/valid? ::cs/key key) (s/valid? ::cs/val val))
    ; Return true if number of affected documents (getN)
    ; is larger then 0. Otherwise call is invalid.
    (< 0 (.getN (mc/remove db coll-name {key val})))
    :not-valid))

(defrecord MongoStorage [db]
  entity/KeyMapStorage
  (-fetch [_ key val] (fetch db key val))
  (-save [_ keymap key val] (save db keymap key val))
  (-delete [_ key val] (delete db key val)))

(defn make-storage [coll-name uri]
  (->MongoStorage {:db (-> (mg/connect-via-uri uri) :db)
                   :coll-name coll-name}))
