(ns name-service.delivery.update
  (:require [clojure.spec.alpha :as s]
            [name-service.delivery.spec :as spec]
            [name-service.delivery.state :as state]
            [name-service.delivery.utils :as utils]
            [cheshire.core :as json]))


(defn- update-key-binding [json-params]
  (let [kv-a (:update json-params)
        kv-b (:where json-params)
        key-a (-> kv-a utils/map-key-first)
        val-a (-> kv-a utils/map-value-first)
        key-b (-> kv-b utils/map-key-first)
        val-b (-> kv-b utils/map-value-first)]
    (utils/action->response (state/update-key-binding key-a val-a key-b val-b))))

(defn dispatch
  [{:keys [json-params]}]
  (if (s/valid? ::spec/update-request json-params)
    (update-key-binding json-params)
    (-> json-params
        (s/explain-data ::spec/update-request)
        (json/encode)
        (utils/bad-request))))
