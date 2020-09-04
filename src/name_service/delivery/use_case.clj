(ns name-service.delivery.use-case
  (:require  [name-service.delivery.append :as append]
             [name-service.delivery.delete :as delete]
             [name-service.delivery.update :as update]
             [name-service.delivery.querying :as querying]))

(defn post-binding [request]
  (append/dispatch request))

(defn get-binding [request]
  (querying/dispatch request))

(defn patch-binding [request]
  (update/dispatch request))

(defn delete-binding [request]
  (delete/dispatch request))
