(ns name-service.api
  (:require [clojure.test :refer :all]
            [io.pedestal.test :refer :all]
            [io.pedestal.http :as bootstrap]
            [mount.core :as mount]
            [name-service.delivery.service :as service]))

(use-fixtures
  :each
  (fn
    [f]
    (mount/stop)
    (mount/start)
    ; TODO: Write TestStorage for testing purposes.
    ;(mount/start-with
    ;  {#'name-service.delivery.state/store (->TestStorage (atom {}))})
    (f)))

(def service
  (::bootstrap/service-fn (bootstrap/create-servlet service/service)))

(deftest home-page-test
  (is (=
        (:body (response-for service :get "/"))
        "Hello World!"))
  (is (=
        (:headers (response-for service :get "/"))
        {"Content-Type" "text/html;charset=UTF-8"
         "Strict-Transport-Security" "max-age=31536000; includeSubdomains"
         "X-Frame-Options" "DENY"
         "X-Content-Type-Options" "nosniff"
         "X-XSS-Protection" "1; mode=block"
         "X-Download-Options" "noopen"
         "X-Permitted-Cross-Domain-Policies" "none"
         "Content-Security-Policy" "object-src 'none'; script-src 'unsafe-inline' 'unsafe-eval' 'strict-dynamic' https: http:;"})))