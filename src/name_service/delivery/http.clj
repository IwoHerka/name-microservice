(ns name-service.delivery.http)

(defn- response [status body & {:as headers}]
  {:status status :body body :headers (merge {"Content-Type" "application/json"} headers)})

(def ok          (partial response 200))
(def bad-request (partial response 400))