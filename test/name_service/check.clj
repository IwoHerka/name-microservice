(ns name-service.check
  (:require [clojure.test :refer :all]
            [clojure.spec.test.alpha :as stest]
            [name-service.core.use-case :as uc]
            [name-service.delivery.storage.memory :refer [make-storage]]
            [name-service.core.spec :as spec]
            [clojure.spec.gen.alpha :as gen]))

(def gen-storage (gen/return (make-storage)))

(deftest check-add-key-binding
  (let [ret (-> `uc/add-key-binding
                (stest/check {:gen {::spec/storage (fn [] gen-storage)}})
                first
                :clojure.spec.test.check/ret)]
    (is (:pass? ret) (str ret))))

(deftest check-append-key-binding
  (let [ret (-> `uc/append-key-binding
                (stest/check {:gen {::spec/storage (fn [] gen-storage)}})
                first
                :clojure.spec.test.check/ret)]
    (is (:pass? ret) (str ret))))

(deftest check-update-key-binding
  (let [ret (-> `uc/update-key-binding
                (stest/check {:gen {::spec/storage (fn [] gen-storage)}})
                first
                :clojure.spec.test.check/ret)]
    (is (:pass? ret) (str ret))))

(deftest check-delete-key-binding
  (let [ret (-> `uc/delete-key-binding
                (stest/check {:gen {::spec/storage (fn [] gen-storage)}})
                first
                :clojure.spec.test.check/ret)]
    (is (:pass? ret) (str ret))))

(deftest check-delete-keymap
  (let [ret (-> `uc/delete-keymap
                (stest/check {:gen {::spec/storage (fn [] gen-storage)}})
                first
                :clojure.spec.test.check/ret)]
    (is (:pass? ret) (str ret))))

(deftest check-get-key
  (let [ret (-> `uc/get-key
                (stest/check {:gen {::spec/storage (fn [] gen-storage)}})
                first
                :clojure.spec.test.check/ret)]
    (is (:pass? ret) (str ret))))
