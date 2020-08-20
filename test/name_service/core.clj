(ns name-service.core
  (:require [clojure.test :refer :all]
            [name-service.utils :as utils]
            [name-service.delivery.state :as uc]
            [mount.core :as mount]))

(use-fixtures :each
  (fn
    [f]
    (mount/stop)
    (mount/start-with
      {#'name-service.delivery.state/store (utils/->TestStorage (atom #{}))})
    (f)))

(deftest test-add-key-binding
  (testing "Adding value A for key X.")
  (is (uc/add-key-binding "slack" "name1@gmail.com")
    {"slack" "cool-name@gmail.com"}))

(deftest test-append-key-binding
  (testing "Adding a value A for key X, where key Y has value B.")
  (uc/add-key-binding "slack" "name1@gmail.com")
  (is (uc/append-key-binding "jira" "name2@gmail.com" "slack" "name1@gmail.com")
    {"slack" "name1@gmail.com" "jira" "name2@gmail.com"}))

(deftest test-update-key-binding-case-1
  (testing "Update key X to value A where key Y has value B")
  (uc/add-key-binding "slack" "name1@gmail.com")
  (uc/append-key-binding "jira" "name2@gmail.com" "slack" "name1@gmail.com")
  (is (uc/update-key-binding "jira" "new-name2@gmail.com" "slack" "name1@gmail.com")
    {"slack" "name1@gmail.com" "jira" "new-name2@gmail.com"}))

(deftest test-update-key-binding-case-2
  (testing "Update key X to value A where key X has value B")
  (uc/add-key-binding "slack" "name1@gmail.com")
  (is (uc/update-key-binding "slack" "name2@gmail.com" "slack" "name1@gmail.com")
    {"slack" "name2@gmail.com"}))

(deftest test-delete-key-binding
  (testing "Delete value for key X where key Y has value A")
  (uc/add-key-binding "slack" "name1@gmail.com")
  (uc/append-key-binding "jira" "name2@gmail.com" "slack" "name1@gmail.com")
  (is (uc/delete-key-binding "slack" "jira" "name2@gmail.com")
    {"jira" "name2@gmail.com"}))

(deftest test-delete-keymap
  (testing "Delete keymap where key X has value A")
  (uc/add-key-binding "slack" "name1@gmail.com")
  (is (uc/delete-keymap "slack" "name1@gmail.com") {}))

(deftest test-get-key
  (testing "Get value of key-a, where key-b has value val-b")
  (uc/add-key-binding "slack" "name1@gmail.com")
  (uc/append-key-binding "jira" "name2@gmail.com" "slack" "name1@gmail.com")
  (is (uc/get-key "slack" "jira" "name2@gmail.com") "name1@gmail.com"))