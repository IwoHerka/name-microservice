(ns name-service.delivery.use-case
  (:require [name-service.delivery.delete :as delete]
            [name-service.delivery.update :as update]
            [ring.util.response :as ring-resp]))

(defn post-binding [request])

(defn get-binding [request]
  (ring-resp/response "Hello World!"))

(defn patch-binding [request]
  (update/dispatch request))

(defn delete-binding [request]
  (delete/dispatch request))
