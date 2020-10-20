(ns name-service.delivery.server.get
  (:require [clojure.spec.alpha :as s]
            [name-service.delivery.server.http :as http]
            [name-service.delivery.server.spec :as spec]
            [name-service.delivery.server.utils :as utils]
            [name-service.delivery.state :as state]))

(defn- get-key [{:keys [path-params query-params]}]
  (let [params (merge path-params query-params)]
    (->> params
      (utils/keymap->arguments)
      (drop 1)
      (apply state/get-key)
      (utils/action->response))))

(defn dispatch [request]
  (if (s/valid? ::spec/get-request request)
    (get-key request)
    (http/bad-request
      (s/explain-data ::spec/get-request request))))
