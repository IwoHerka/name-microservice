(ns name-service.api
  (:require [cheshire.core :as json]
            [clojure.test :refer :all]
            [io.pedestal.http :as bootstrap]
            [io.pedestal.http.route :as route]
            [io.pedestal.test :refer :all]
            [mount.core :as mount]
            [name-service.delivery.service :as service]
            [name-service.delivery.state :as uc]
            [name-service.delivery.utils :refer [inject-interceptors]]
            [name-service.utils :as utils]))

(use-fixtures :each
              (fn
                [f]
                (mount/stop)
                (mount/start-with
                  {#'name-service.delivery.state/store (utils/->TestStorage (atom #{}))})
                (f)))

(def url-for
  (route/url-for-routes (route/expand-routes service/routes)))

(def service
  (::bootstrap/service-fn (-> service/service
                              inject-interceptors
                              bootstrap/create-servlet)))


(def headers {"Content-Type" "application/json"})

(deftest test-delete-keymap-returns-400-when-params-are-wrong
  (is (= 400 (:status (response-for service
                                    :delete (url-for :delete-binding)
                                    :headers headers
                                    :body (json/encode {:where {:jira 2}}))))))

(deftest test-delete-key-binding-returns-400-when-params-are-wrong
  (is (= 400 (:status (response-for service
                                    :delete (url-for :delete-binding)
                                    :headers headers
                                    :body (json/encode {:delete "slack"
                                                        :where {:jira "name2@gmail.com" :slack "name1@gmail.com"}}))))))

(deftest test-update-key-binding-returns-400-when-params-are-wrong
  (is (= 400 (:status (response-for service
                                    :patch (url-for :patch-binding)
                                    :headers headers
                                    :body (json/encode {:add {:jira "new-name2@gmail.com"
                                                              :where {:slack "name1@gmail.com"}}}))))))

(deftest test-delete-keymap-returns-200-when-keymap-exist
  (testing "Delete keymap where key X has value A")
  (uc/add-key-binding "jira" "name1@gmail.com")
  (is (= 200 (:status (response-for service
                                    :delete (url-for :delete-binding)
                                    :headers headers
                                    :body (json/encode {:where {:jira "name1@gmail.com"}}))))))

(deftest test-delete-key-binding-returns-200-when-keymap-exist
  (testing "Delete value for key X where key Y has value A")
  (uc/add-key-binding "slack" "name1@gmail.com")
  (uc/append-key-binding "jira" "name2@gmail.com" "slack" "name1@gmail.com")
  (is (= 200 (:status (response-for service
                                    :delete (url-for :delete-binding)
                                    :headers headers
                                    :body (json/encode {:delete "slack" :where {:jira "name2@gmail.com"}}))))))

(deftest test-delete-key-binding-returns-400-when-key-does-not-exist
  (testing "Delete value for key X where key Y has value A - key X not exist")
  (uc/add-key-binding "slack" "name1@gmail.com")
  (uc/append-key-binding "jira" "name2@gmail.com" "slack" "name1@gmail.com")
  (is (= 400 (:status (response-for service
                                    :delete (url-for :delete-binding)
                                    :headers headers
                                    :body (json/encode {:delete "git" :where {:jira "name2@gmail.com"}}))))))

(deftest test-update-key-binding-returns-200-when-key-exists-in-keymap
  (testing "Update key X to value A where key Y has value B")
  (uc/add-key-binding "slack" "name1@gmail.com")
  (uc/append-key-binding "jira" "name2@gmail.com" "slack" "name1@gmail.com")
  (is (= 200 (:status (response-for service
                                    :patch (url-for :patch-binding)
                                    :headers headers
                                    :body (json/encode {:update {:jira "new-name2@gmail.com"}
                                                        :where {:slack "name1@gmail.com"}}))))))

(deftest test-update-key-binding-returns-400-when-key-does-not-exist-in-keymap
  (testing "Update key X to value A where key Y has value B - key X not exist")
  (uc/add-key-binding "slack" "name1@gmail.com")
  (uc/append-key-binding "jira" "name2@gmail.com" "slack" "name1@gmail.com")
  (is (= 400 (:status (response-for service
                                    :patch (url-for :patch-binding)
                                    :headers headers
                                    :body (json/encode {:update {:git "new-name2@gmail.com"}
                                                        :where {:slack "name1@gmail.com"}}))))))