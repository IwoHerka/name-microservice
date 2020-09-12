(ns name-service.delivery.state
  (:require [mount.core :refer [defstate]]
            [environ.core :refer [env]]
            [name-service.core.use-case :as uc]
            ;; By importing make-storage from mongo namespace,
            ;; we are choosing an implementation.
            [name-service.storage.mongo :refer [make-storage]]))

(defstate store              :start (make-storage (env :coll-name) (env :uri)))
(defstate add-key-binding    :start (uc/add-key-binding    {:storage store}))
(defstate append-key-binding :start (uc/append-key-binding {:storage store}))
(defstate update-key-binding :start (uc/update-key-binding {:storage store}))
(defstate delete-keymap      :start (uc/delete-keymap      {:storage store}))
(defstate delete-key-binding :start (uc/delete-key-binding {:storage store}))
(defstate get-key            :start (uc/get-key            {:storage store}))