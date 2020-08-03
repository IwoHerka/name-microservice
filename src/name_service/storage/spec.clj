(ns name-service.storage.spec
  (:require [name-service.storage.mongo :as mongo]
            [name-service.core.spec :as cs]
            [clojure.spec.alpha :as s]))

(s/fdef mongo/make-storage :ret ::cs/storage)