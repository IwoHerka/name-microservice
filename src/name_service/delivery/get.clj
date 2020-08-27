(ns name-service.delivery.get
  (:require [clojure.spec.alpha :as s]
    [name-service.delivery.spec :as a]
    [name-service.delivery.state :refer [get-key]]
    [name-service.delivery.utils :refer [bad-request
                                         keymap->arguments
                                         action->response]]))

(defn- get-value
  [{:keys [path-params query-params]}]
  (let [params (merge path-params query-params)]
    (->> params
      (keymap->arguments)
      (drop 1)
      (apply get-key)
      (action->response))))

(defn dispatch
  [request]
  (if (s/valid? ::a/get-request request)
    (get-value request)
    (bad-request
      (s/explain-data ::a/get-request request))))
