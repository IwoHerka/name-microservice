(ns name-service.api
  (:require [clojure.test :refer :all]
            [io.pedestal.test :refer :all]
            [io.pedestal.http :as bootstrap]
            [mount.core :as mount]
            [name-service.delivery.service :as service]
            [name-service.delivery.state :as uc]
            [name-service.utils :as utils]
            [name-service.delivery.spec :as spec]))

(use-fixtures :each
              (fn
                [f]
                (mount/stop)
                (mount/start-with
                  {#'name-service.delivery.state/store (utils/->TestStorage (atom #{}))})
                (f)))


(def service
  (::bootstrap/service-fn (bootstrap/create-servlet service/service)))

(def headers {"Content-Type" "application/json"})

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

(deftest test-delete-keymap-wrong-params
  (is (= 400 (:status (response-for service
                                    :delete "/"
                                    :headers headers
                                    :body "{\"where\":{ \"jira\" : 2}}")))))

(deftest test-delete-key-binding-wrong-params
  (is (= 400 (:status (response-for service
                                    :delete "/"
                                    :headers headers
                                    :body "{\"delete\": \"slack\",
                                     \"where\": {\"jira\":\"name2@gmail.com\", \"slack\":\"name1@gmail.com\"}}")))))

(deftest test-update-key-binding-wrong-params
  (is (= 400 (:status (response-for service
                                    :patch "/"
                                    :headers headers
                                    :body "{\"add\": {\"jira\": \"new-name2@gmail.com\"},
                                     \"where\": {\"slack\": \"name1@gmail.com\"}}")))))

(deftest test-delete-keymap
  (testing "Delete keymap where key X has value A")
  (uc/add-key-binding "jira" "name1@gmail.com")
  (is (= 200 (:status (response-for service
                                    :delete "/"
                                    :headers headers
                                    :body "{\"where\":{ \"jira\" : \"name1@gmail.com\"}}")))))

(deftest test-delete-key-binding-valid
  (testing "Delete value for key X where key Y has value A")
  (uc/add-key-binding "slack" "name1@gmail.com")
  (uc/append-key-binding "jira" "name2@gmail.com" "slack" "name1@gmail.com")
  (is (= 200 (:status (response-for service
                                    :delete "/"
                                    :headers headers
                                    :body "{\"delete\": \"slack\",
                                     \"where\": {\"jira\":\"name2@gmail.com\"}}"))))

  (deftest test-delete-key-binding-invalid
    (testing "Delete value for key X where key Y has value A - key X not exist")
    (uc/add-key-binding "slack" "name1@gmail.com")
    (uc/append-key-binding "jira" "name2@gmail.com" "slack" "name1@gmail.com")
    (is (= 400 (:status (response-for service
                                      :delete "/"
                                      :headers headers
                                      :body "{\"delete\": \"git\",
                                     \"where\": {\"jira\":\"name2@gmail.com\"}}"))))))

(deftest test-update-key-binding-valid
  (testing "Update key X to value A where key Y has value B")
  (uc/add-key-binding "slack" "name1@gmail.com")
  (uc/append-key-binding "jira" "name2@gmail.com" "slack" "name1@gmail.com")
  (is (= 200 (:status (response-for service
                                  :patch "/"
                                  :headers headers
                                  :body "{\"update\": {\"jira\": \"new-name2@gmail.com\"},
                                     \"where\": {\"slack\": \"name1@gmail.com\"}}")))))

(deftest test-update-key-binding-invalid
  (testing "Update key X to value A where key Y has value B - key X not exist")
  (uc/add-key-binding "slack" "name1@gmail.com")
  (uc/append-key-binding "jira" "name2@gmail.com" "slack" "name1@gmail.com")
  (is (= 400 (:status (response-for service
                                    :patch "/"
                                    :headers headers
                                    :body "{\"update\": {\"git\": \"new-name2@gmail.com\"},
                                     \"where\": {\"slack\": \"name1@gmail.com\"}}")))))