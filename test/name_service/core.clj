(ns name-service.core
  "Core use-cases' tests."
  (:require [clojure.test :refer :all]
            [clojure.spec.test.alpha :as stest]
            [name-service.delivery.storage.memory :as mem]
            [name-service.delivery.state :as s]
            [name-service.core.use-case :as uc]
            [name-service.core.entity :as e]
            [mount.core :as mount]))

(stest/instrument `e/save)
(stest/instrument `e/fetch)
(stest/instrument `e/delete)
(stest/instrument `uc/add-key-binding)
(stest/instrument `uc/append-key-binding)
(stest/instrument `uc/update-key-binding)
(stest/instrument `uc/delete-key-binding)
(stest/instrument `uc/delete-keymap)
(stest/instrument `uc/get-key)

(use-fixtures
  :each
  (fn [f]
    (mount/stop)
    (mount/start-with
      {#'name-service.delivery.state/store (mem/make-storage)})
    (f)))

(deftest test-add-key-binding
  (testing "Adding value A for key X.")
  (let [result (s/add-key-binding "slack" "name1@gmail.com")
        keymap (:result (:payload result))
        error (:error result)]
    (is (and (= keymap {"slack" "name1@gmail.com"})
             (= error nil)))))

(deftest test-append-key-binding
  (testing "Adding a value A for key X, where key Y has value B.")
  (s/add-key-binding "slack" "name1@gmail.com")
  (let [result (s/append-key-binding "jira" "name2@gmail.com" "slack"
                                     "name1@gmail.com")
        keymap (:result (:payload result))
        error (:error result)]
    (is (and (= keymap {"slack" "name1@gmail.com" "jira" "name2@gmail.com"})
             (= error nil)))))

(deftest test-update-key-binding-case-1
  (testing "Update key X to value A where key Y has value B")
  (s/add-key-binding "slack" "name1@gmail.com")
  (s/append-key-binding "jira" "name2@gmail.com" "slack" "name1@gmail.com")
  (let [result (s/update-key-binding "jira" "new-name2@gmail.com" "slack"
                                     "name1@gmail.com")
        keymap (:result (:payload result))
        error (:error result)]
    (is (and (= keymap {"slack" "name1@gmail.com" "jira" "new-name2@gmail.com"})
             (= error nil)))))

(deftest test-update-key-binding-case-2
  (testing "Update key X to value A where key X has value B")
  (s/add-key-binding "slack" "name1@gmail.com")
  (let [result (s/update-key-binding "slack" "name2@gmail.com" "slack"
                                     "name1@gmail.com")
        keymap (:result (:payload result))
        error (:error result)]
    (is (and (= keymap {"slack" "name2@gmail.com"})
             (= error nil)))))

(deftest test-delete-key-binding
  (testing "Delete value for key X where key Y has value A")
  (s/add-key-binding "slack" "name1@gmail.com")
  (s/append-key-binding "jira" "name2@gmail.com" "slack" "name1@gmail.com")
  (let [result (s/delete-key-binding "slack" "jira" "name2@gmail.com")
        keymap (:result (:payload result))
        error (:error result)]
    (is (and (= keymap {"jira" "name2@gmail.com"})
             (= error nil)))))

(deftest test-delete-keymap
  (testing "Delete keymap where key X has value A")
  (s/add-key-binding "slack" "name1@gmail.com")
  (is (:result (:payload (s/delete-keymap "slack" "name1@gmail.com"))) {}))

(deftest test-get-key
  (testing "Get value of key-a, where key-b has value val-b")
  (s/add-key-binding "slack" "name1@gmail.com")
  (s/append-key-binding "jira" "name2@gmail.com" "slack" "name1@gmail.com")
  (is (:result (:payload (s/get-key "slack" "jira" "name2@gmail.com")))
      "name1@gmail.com"))
