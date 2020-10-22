(ns name-service.delivery.server.server
  "Entry point for Pedestal-powered web server.
   -main starts the application, run-dev starts dev server."
  (:gen-class)
  (:require [io.pedestal.http :as server]
            [io.pedestal.http.route :as route]
            [mount.core :as mount]
            [name-service.delivery.storage.memory :refer [make-storage]]
            [name-service.delivery.server.service :as service]
            [name-service.delivery.server.utils :refer [inject-interceptors]]))

; This is an adapted service map, that can be started and stopped
; From the REPL you can call server/start and server/stop on this service
(defonce runnable-service (server/create-server service/service))

(defn -run-dev
  [mock-storage?]
  (println "\nCreating your [DEV] server...")
  (if mock-storage?
    (mount/start-with
      {#'name-service.delivery.state/store (make-storage)})
    (mount/start))
  (-> service/service ; start with production configuration
      (merge {:env :dev
              ; do not block thread that starts web server
              ::server/join? false
              ; Routes can be a function that resolve routes,
              ; we can use this to set the routes to be reloadable
              ::server/routes #(route/expand-routes (deref #'service/routes))
              ; all origins are allowed in dev mode
              ::server/allowed-origins {:creds true :allowed-origins (constantly true)}
              ; Content Security Policy (CSP) is mostly turned off in dev mode
              ::server/secure-headers {:content-security-policy-settings {:object-src "'none'"}}})
      ; Wire up interceptor chains
      inject-interceptors
      server/dev-interceptors
      server/create-server
      server/start))

(defn run-dev
  "The entry-point for 'lein run-dev'"
  ([] (-run-dev false))
  ([mock-storage?] (-run-dev mock-storage?)))

(defn -main
  "The entry-point for 'lein run'"
  [& args]
  (println "\nCreating your server...")
  (mount/start)
  (server/start runnable-service))
