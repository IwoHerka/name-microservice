(ns name-service.delivery.use-case
  (:require [ring.util.response :as ring-resp]))

(defn post-binding [request])

(defn get-binding [request]
  (ring-resp/response "Hello World!"))

(defn patch-binding [request])

(defn delete-binding [request])