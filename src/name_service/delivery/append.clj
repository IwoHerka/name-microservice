(ns name-service.delivery.append
  (:require [clojure.spec.alpha :as s]
            [name-service.delivery.http :as http]
            [name-service.delivery.spec :as spec]
            [name-service.delivery.state :as state]
            [name-service.delivery.utils :as utils]))

(defn- append [json-params]
  (let [where-keymap (get json-params :where)]
    (if-let [append-keymap (get json-params :append)]
      (->> where-keymap
        (merge append-keymap)
        (utils/keymap->arguments)
        (apply state/append-key-binding)
        (utils/action->response))
      (->> where-keymap
        (utils/keymap->arguments)
        (apply state/add-key-binding)
        (utils/action->response)))))

(defn dispatch [{:keys [json-params]}]
  (if (s/valid? ::spec/post-request json-params)
    (append json-params)
    (http/bad-request
      (s/explain-data ::spec/post-request json-params))))
