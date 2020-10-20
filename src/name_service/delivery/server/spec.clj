(ns name-service.delivery.server.spec
  (:require [clojure.spec.alpha :as s]))

; For now, delete is supported only for one key.
; (s/def ::delete (s/coll-of string? :distinct true :count 1))
(s/def ::delete string?)

; For now, we can only update one keymap at a time.
; In the future we can change from :count to :min-count.
(s/def ::update (s/map-of keyword? string? :count 1))

(s/def ::append
  (s/map-of keyword? string? :count 1))

(s/def ::where
  (s/map-of keyword? string? :count 1))

(s/def ::query-params
  (s/map-of keyword? string? :count 1))

(s/def ::delete-request
  (s/keys :req-un [::where]
          :opt-un [::delete]))

(s/def ::post-request
  (s/keys :req-un [::where]
          :opt-un [::append]))

(s/def ::update-request
  (s/keys :req-un [::where
                   ::update]))

(s/def ::get-request
  (s/keys :req-un [::query-params
                   ::path-params]))
