(ns name-service.delivery.storage.spec
  (:require [clojure.spec.alpha :as s]
            [name-service.core.spec :as cs]
            [name-service.delivery.storage.mongo :as mongo]))

(s/fdef mongo/make-storage :ret ::cs/storage)
