(ns name-service.delivery.state
  (:require [environ.core :refer [env]]
            [mount.core :refer [defstate]]
            [name-service.core.use-case :as uc]
            ; By importing make-storage from mongo namespace,
            ; we are choosing an implementation.
            [name-service.delivery.storage.mongo :refer [make-storage]]))

(defstate store              :start (make-storage (env :coll-name) (env :uri)))
(defstate add-key-binding    :start (partial uc/add-key-binding    store))
(defstate append-key-binding :start (partial uc/append-key-binding store))
(defstate update-key-binding :start (partial uc/update-key-binding store))
(defstate delete-keymap      :start (partial uc/delete-keymap      store))
(defstate delete-key-binding :start (partial uc/delete-key-binding store))
(defstate get-key            :start (partial uc/get-key            store))
