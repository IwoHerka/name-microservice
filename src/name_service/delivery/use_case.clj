(ns name-service.delivery.use-case
  (:require [ring.util.response :as ring-resp]
            [name-service.delivery.delete :as delete]
            [name-service.delivery.update :as update]
            [name-service.delivery.add :as add]
            [name-service.delivery.get :as get]))

(defn post-binding [request]
  (add/dispatch request))

(defn get-binding [request]
  (get/dispatch request))

(defn patch-binding [request]
  (update/dispatch request))

(defn delete-binding [request]
  (delete/dispatch request))
