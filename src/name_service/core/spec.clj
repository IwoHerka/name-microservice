(ns name-service.core.spec
  (:require [clojure.spec.alpha :as s]
            [name-service.core.entity :as entity]))

(s/def ::key string?)
(s/def ::val string?)
(s/def ::keymap (s/map-of ::key ::val))

(s/def ::storage #(satisfies? entity/KeyMapStorage %))
(s/def ::storage-error keyword?)
(s/def ::storage-result (s/or :data (s/or :val ::val :vals (s/* ::val))
                              :error ::storage-error))

(s/fdef entity/storage-error?
        :args (s/cat :result ::storage-result)
        :ret  boolean?)

(s/fdef entity/save
        :args (s/cat :storage ::storage :keymap ::keymap :key ::key :val ::val)
        :ret  ::storage-result)

(s/fdef entity/delete
        :args (s/cat :storage ::storage :key ::key :val ::val)
        :ret  boolean?)

(s/fdef entity/fetch
        :args (s/cat :storage ::storage :key ::key :val ::val)
        :ret ::storage-result)