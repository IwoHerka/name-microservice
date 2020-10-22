(ns name-service.core.spec
  (:require [clojure.spec.alpha :as s]
            [name-service.core.entity :as entity]
            [name-service.core.use-case :as uc]))

(s/def ::key              (s/and string? not-empty))
(s/def ::val              (s/and string? not-empty))
(s/def ::val              (s/and string? not-empty))
(s/def ::keymap           (s/and (s/map-of ::key ::val) not-empty))
(s/def ::multiple-keymaps (s/and (s/coll-of ::keymap) not-empty))

(s/def ::storage        #(satisfies? entity/KeyMapStorage %))
(s/def ::storage-error  keyword?)
(s/def ::storage-result (s/or :data (s/or :val ::val :keymap ::keymap)
                              :error ::storage-error))

(s/fdef entity/fetch
        :args (s/cat :storage ::storage :key ::key :val ::val)
        :ret ::storage-result)

(s/fdef entity/save
        :args (s/and (s/cat :storage ::storage
                     :keymap ::keymap
                     :key (s/nilable ::key)
                     :val (s/nilable ::val))
                     #(or (and (:key %) (:val %))
                          (every? nil? [(:key %) (:val %)])))
        :ret  ::storage-result)

(s/fdef entity/delete
        :args (s/cat :storage ::storage :key ::key :val ::val)
        :ret  boolean?)

(s/fdef entity/storage-error?
        :args (s/cat :result ::storage-result)
        :ret  boolean?)

(s/fdef uc/append-key-binding
        :args (s/cat :storage ::storage
                     :key-a ::key
                     :val-a ::val
                     :key-b ::key
                     :val-a ::val)
        :ret map?)

(s/fdef uc/add-key-binding
        :args (s/cat :storage ::storage
                     :key ::key
                     :val ::val)
        :ret map?)

(s/fdef uc/update-key-binding
        :args (s/cat :storage ::storage
                     :key-a ::key
                     :val-a ::val
                     :key-b ::key
                     :val-a ::val)
        :ret map?)

(s/fdef uc/delete-key-binding
        :args (s/cat :storage ::storage
                     :key-a ::key
                     :key-b ::key
                     :val-b ::val)
        :ret map?)

(s/fdef uc/delete-keymap
        :args (s/cat :storage ::storage
                     :key ::key
                     :val ::val)
        :ret map?)

(s/fdef uc/get-key
        :args (s/cat :storage ::storage
                     :key-a ::key
                     :key-b ::key
                     :val-b ::val)
        :ret map?)
