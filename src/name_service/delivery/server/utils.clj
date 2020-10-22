(ns name-service.delivery.server.utils
  (:require [cheshire.core :as json]
            [io.pedestal.http :as bootstrap]
            [io.pedestal.http.body-params :as body-params]
            [name-service.delivery.server.http :as http]))

(defn first-key-to-str [map]
  (-> map keys first name))

(defn first-value-to-str [map]
  (-> map vals first))

(defn merge-with-payload->json [coll payload]
  (json/encode (merge payload coll)))

(defn inject-interceptors [service-map]
  (-> service-map
    bootstrap/default-interceptors
    (update ::bootstrap/interceptors conj (body-params/body-params))
    (update ::bootstrap/interceptors conj bootstrap/html-body)))

(defn action->response
  "Converts an action to a response."
  [{:keys [:error :payload]}]
  (cond
    (= error :key-does-not-exist)
    (http/bad-request (json/encode {:error "Key does not exist."}))

    (= error :key-exists)
    (http/bad-request (json/encode {:error "Key already exists."}))

    (= error :storage)
    (http/bad-request (json/encode {:error "Saving data failed." :result payload}))

    (map? error)
    (http/bad-request (json/encode {:error error}))

    (nil? error)
    (http/ok (merge-with-payload->json {:error nil} payload))))

(defn json->keymap [keymap]
  "Convert JSON representation of a keymap to internal reresentation."
  (-> keymap
      clojure.walk/stringify-keys
      vec
      flatten))
