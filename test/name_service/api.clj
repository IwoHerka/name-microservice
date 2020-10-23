(ns name-service.api
  "Pedestal API tests."
  (:require [clojure.test :refer :all]
            [name-service.delivery.state :as uc]
            [name-service.delivery.server.service :as service]
            [name-service.delivery.server.utils :refer [inject-interceptors]]
            [name-service.delivery.storage.memory :as mem]
            [name-service.delivery.state :refer [store]]
            [io.pedestal.http :as bootstrap]
            [io.pedestal.http.route :as route]
            [io.pedestal.test :refer :all]
            [cheshire.core :as json]
            [mount.core :as mount]
            [monger.collection :as mc]))

(use-fixtures
  :each
  (fn [f]
    (mount/stop)
    (mount/start-with
      {#'name-service.delivery.state/store (mem/make-storage)})
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

(deftest test-add-keymap-returns-200
  (is (= 200 (:status (response-for service
                        :post (url-for :post-binding)
                        :headers headers
                        :body (json/encode {:where {:x "a"}}))))))

(deftest test-add-keymap-returns-400-when-to-many-where
  (is (= 400 (:status (response-for service
                        :post (url-for :post-binding)
                        :headers headers
                        :body (json/encode {:where {:x "a"
                                                    :z "c"}})))))
  (is (= 400 (:status (response-for service
                        :post (url-for :post-binding)
                        :headers headers
                        :body (json/encode {:append {:x "a"}
                                            :where  {:y "b"
                                                     :z "c"}}))))))

(deftest test-append-keymap-returns-200
  (testing "Add value A for key X, where key Y has value B.")
  (is (= 200 (:status (response-for service
                        :post (url-for :post-binding)
                        :headers headers
                        :body (json/encode {:append {:x "a"}
                                            :where  {:y "b"}}))))))

(deftest test-append-keymap-returns-400-when-value-in-params-are-wrong
  (is (= 400 (:status (response-for service
                        :post (url-for :post-binding)
                        :headers headers
                        :body (json/encode {:append {:x 1}
                                            :where  {:y 2}})))))

  (is (= 400 (:status (response-for service
                        :post (url-for :post-binding)
                        :headers headers
                        :body (json/encode {:append {:x "a"}
                                            :where  {:y 2}})))))

  (is (= 400 (:status (response-for service
                        :post (url-for :post-binding)
                        :headers headers
                        :body (json/encode {:append {:x 1}
                                            :where  {:y "c"}}))))))

(deftest test-append-keymap-returns-400-when-no-where-params
  (is (= 400 (:status (response-for service
                        :post (url-for :post-binding)
                        :headers headers
                        :body (json/encode {:append {:x "a"}})))))

  (is (= 400 (:status (response-for service
                        :post (url-for :post-binding)
                        :headers headers
                        :body (json/encode {:append {:x 1}}))))))

(deftest test-quering-key-bindings-returns-200
  (testing "Get value of key X where key Y has value A")
  (is (= 200 (:status (response-for service
                        :get (url-for :get-binding
                               :path-params  {:key "x"}
                               :query-params {:y "a"}))))))

(deftest test-quering-key-bindings-returns-400-when-to-many-query-params
  (is (= 400 (:status (response-for service
                        :get (url-for
                               :get-binding
                               :path-params  {:key "x"}
                               :query-params {:y "a"
                                              :z "c"}))))))

(deftest test-quering-key-bindings-returns-400-when-no-query-params
  (is (= 400 (:status (response-for service
                        :get (url-for
                               :get-binding
                               :path-params {:key "x"}))))))

(deftest test-quering-key-bindings-returns-404-no-path-params
  (is (= 404 (:status (response-for service
                        :get (url-for
                               :get-binding
                               :query-params {:y "a"}))))))
