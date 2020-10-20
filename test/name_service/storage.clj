(ns name-service.storage
  "MongoDB storage tests."
  (:require [clojure.test :refer :all]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [name-service.core.entity :as entity]
            [name-service.core.spec :as cs]
            [name-service.delivery.storage.mongo :as mongo]
            [name-service.delivery.state :refer [store]]
            [monger.collection :as mc]
            [mount.core :as mount]))

(use-fixtures
  :each
  (fn [f]
    (let [store (mongo/make-storage "test" "mongodb://mongodb:27017/dev?authSource=admin")]
      (mount/stop)
      (mount/start-with {#'store store})
      (mc/drop (:db (:db store)) (:coll-name (:db store))))
    (f)))

(defn gen
  [spec]
  (first (gen/sample (s/gen spec) 1)))

(defn gen-keymap []
  (gen ::cs/keymap))

(deftest fetch-correct-values
  (let [keymap (gen-keymap)
        where (rand-nth (vec keymap))]
    (entity/save store keymap "a" "b")
    (is (= keymap
           (entity/fetch store (first where) (second where))))))

(deftest remove-value-from-db
  (let [many-map (gen ::cs/multiple-keymaps)
        keymap (rand-nth (vec many-map))
        where (rand-nth (vec keymap))]
    (mc/insert-batch (:db (:db store)) (:coll-name (:db store)) many-map)
    (entity/delete store (first where) (second where))
    (is (= (- (- (count many-map) 1)
              (mc/count (:db (:db store)) (:coll-name (:db store)) {(first where) (second where)}))
           (mc/count (:db (:db store)) (:coll-name (:db store)))))))

(deftest add-new-keymap
  (let [keymap (gen-keymap)]
    (is (= keymap
           (entity/save store keymap "a" "b")))))

(deftest add-key-binding
  (let [keymap (gen-keymap)
        where (rand-nth (vec keymap))
        keymap' (assoc keymap (gen ::cs/key) (gen ::cs/val))]
    (entity/save store keymap nil nil)
    (is (= keymap'
           (entity/save store keymap' (first where) (second where))))))

(deftest remove-key-binding
  (let [keymap (gen-keymap)
        where (rand-nth (vec keymap))
        keymap' (dissoc keymap (gen ::cs/key))]
    (entity/save store keymap nil nil)
    (is (= keymap'
           (entity/save store keymap' (first where) (second where))))))

(deftest add-empty-object
  (entity/save store {"user" "one"} nil nil)
  (is (= {"abc"  "xyz" "user" "one"}
         (entity/save store {"user" "one" "abc" "xyz"} "user" "one"))))
