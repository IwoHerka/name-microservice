(ns name-service.delivery.add
  (:require [clojure.spec.alpha :as s]
    [name-service.delivery.spec :as a]
    [name-service.delivery.state :refer [add-key-binding
                                         append-key-binding]]
    [name-service.delivery.utils :refer [bad-request
                                         keymap->arguments
                                         action->response]]))

(defn- add-or-append
  [{:keys [json-params]}]
  (let [where-keymap (get json-params :where)]
    (if-let [append-keymap (get json-params :append)]
      (->> where-keymap
        (merge append-keymap)
        (keymap->arguments)
        (apply append-key-binding)
        (action->response))
      (->> where-keymap
        (keymap->arguments)
        (apply add-key-binding)
        (action->response)))))

(defn dispatch
  [request]
  (if (s/valid? ::a/post-request request)
    (add-or-append request)
    (bad-request
      (s/explain-data ::a/post-request request))))