(ns name-service.delivery.utils
  (:require [name-service.core.action :as action]
            [cheshire.core :as json]))

(defn bad-request
  [error]
  {:status 400 :body error})

(defn map-key-first [map]
  (-> map
      keys
      first
      name))

(defn map-value-first [map]
  (-> map
      vals
      first))

(defn merge-with-payload->json [coll payload]
  (->>
    coll
    (merge payload)
    (json/encode)))

(defn action->response
  "Converts an action to a response."
  [{:keys [::action/type ::action/error ::action/payload]}]
  (let [type-name (name type)]
    (cond
      (= error :key-does-not-exist)
      {:status 400
       :body   (json/encode {:error "Key does not exist." :type type-name})}
      (= error :key-exists)
      {:status 400
       :body   (json/encode {:error "Key exist." :type type-name})}
      (= error :storage)
      {:status 400
       :body
               (json/encode {:error "Error occurred on storage action." :type type-name :result payload})}
      (map? error)
      {:status 400
       :body   (merge-with-payload->json {:type type-name} error)}
      (nil? error)
      {:status 200
       :body   (merge-with-payload->json {:error nil :type type-name} payload)})))

(defn keymap->arguments
  [keymap]
  (->> keymap
    (clojure.walk/stringify-keys)
    (mapcat identity)
    (vec)))
