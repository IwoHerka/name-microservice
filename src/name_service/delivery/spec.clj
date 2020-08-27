(ns name-service.delivery.spec
  (:require [clojure.spec.alpha :as s]))

;for now delete is supported only for one key
;(s/def ::delete (s/coll-of string? :distinct true :count 1))
(s/def ::delete string?)

;for now we can only update one key->map at once
;in the future change :count -> :min-count
(s/def ::update (s/map-of keyword? string? :count 1))

(s/def ::where (s/map-of keyword? string? :count 1))

(s/def ::query-params (s/map-of keyword? string? :count 1))

(s/def ::delete-request (s/keys :req-un [::where]
                                :opt-un [::delete]))

(s/def ::update-request (s/keys :req-un [::where ::update]))

(s/def ::get-request
  (s/keys :req-un [::query-params ::path-params]))

(s/def ::post-request
  (s/keys :req-un [::where]
          :opt-un [::add]))
