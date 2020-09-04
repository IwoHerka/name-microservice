(ns name-service.delivery.update
  (:require [cheshire.core :as json]
            [clojure.spec.alpha :as s]
            [name-service.delivery.http :as http]
            [name-service.delivery.spec :as spec]
            [name-service.delivery.state :as state]
            [name-service.delivery.utils :as utils]))

(defn- update-key-binding [json-params]
  (let [kv-a (:update json-params)
        kv-b (:where json-params)
        key-a (utils/first-key-to-str kv-a)
        val-a (utils/first-value-to-str kv-a)
        key-b (utils/first-key-to-str kv-b)
        val-b (utils/first-value-to-str kv-b)]
    (utils/action->response (state/update-key-binding key-a val-a key-b val-b))))

(defn dispatch
  [{:keys [json-params]}]
  (if (s/valid? ::spec/update-request json-params)
    (update-key-binding json-params)
    (-> json-params
        (s/explain-data ::spec/update-request)
        (json/encode)
        (http/bad-request))))