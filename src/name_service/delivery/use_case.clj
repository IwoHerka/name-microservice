(ns name-service.delivery.use-case
  (:require [ring.util.response :as ring-resp]
            [name-service.delivery.delete :as delete]
            [name-service.delivery.update :as update]))

(defn post-binding [request])

(defn get-binding [request]
  (ring-resp/response "Hello World!"))

(defn patch-binding [request]
  (update/dispatch request))

(defn delete-binding [request]
  (delete/dispatch request))
