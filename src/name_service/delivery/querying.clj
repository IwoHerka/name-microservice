(ns name-service.delivery.querying
  (:require [clojure.spec.alpha :as s]
            [name-service.delivery.http :as http]
            [name-service.delivery.spec :as spec]
            [name-service.delivery.state :as state]
            [name-service.delivery.utils :as utils]))

(defn- querying [{:keys [path-params query-params]}]
  (let [params (merge path-params query-params)]
    (->> params
      (utils/keymap->arguments)
      (drop 1)
      (apply state/get-key)
      (utils/action->response))))

(defn dispatch [request]
  (if (s/valid? ::spec/get-request request)
    (querying request)
    (http/bad-request
      (s/explain-data ::spec/get-request request))))
