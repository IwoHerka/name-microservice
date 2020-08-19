(ns name-service.delivery.delete
  (:require [clojure.spec.alpha :as s]
            [name-service.delivery.spec :as spec]
            [name-service.delivery.utils :as utils]
            [name-service.delivery.state :as state]
            [cheshire.core :as json]))


(defn- delete-keymap [kv]
  (let [key (-> kv utils/map-key-first)
        value (-> kv utils/map-value-first)]
    (utils/action->response (state/delete-keymap key value))))

(defn- delete-by-key [key-delete kv]
  (let [key (-> kv utils/map-key-first)
        value (-> kv utils/map-value-first)]
    (-> key-delete
        (state/delete-key-binding key value)
        (utils/action->response))))

(defn- delete-key-binding [json-params]
  (if-let [key (:delete json-params)]
    (delete-by-key key (:where json-params))
    (delete-keymap (:where json-params))))

(defn dispatch
  [{:keys [json-params]}]
  (if (s/valid? ::spec/delete-request json-params)
    (delete-key-binding json-params)
    (-> json-params
        (s/explain-data ::spec/delete-request)
        (json/encode)
        (utils/bad-request))))
