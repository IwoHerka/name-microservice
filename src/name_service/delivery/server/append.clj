(ns name-service.delivery.server.append
  (:require [clojure.spec.alpha :as s]
            [name-service.delivery.server.http :as http]
            [name-service.delivery.server.spec :as spec]
            [name-service.delivery.server.utils :as utils]
            [name-service.delivery.state :as state]))

(defn- append-key [json-params]
  (if-let [append-keymap (:append json-params)]
    (->> json-params
         :where ; Get the "where" dict.
         (merge append-keymap)
         utils/json->keymap
         (apply state/append-key-binding)
         utils/action->response)
    (->> json-params
         :where
         utils/json->keymap
         (apply state/add-key-binding)
         utils/action->response)))

(defn dispatch [{:keys [json-params]}]
  (if (s/valid? ::spec/post-request json-params)
    (append-key json-params)
    (http/bad-request (s/explain-data ::spec/post-request json-params))))
