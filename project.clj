(defproject name-service "0.0.1-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [io.pedestal/pedestal.service "0.5.8"]
                 [io.pedestal/pedestal.jetty "0.5.8"]
                 [com.novemberain/monger "3.5.0"]
                 [ch.qos.logback/logback-classic "1.2.3" :exclusions [org.slf4j/slf4j-api]]
                 [org.clojure/test.check "1.0.0"]
                 [org.slf4j/jul-to-slf4j "1.7.26"]
                 [org.slf4j/jcl-over-slf4j "1.7.26"]
                 [org.slf4j/log4j-over-slf4j "1.7.26"]
                 [environ "1.2.0"]
                 [mount "0.1.11"]]
  :plugins [[lein-environ "1.2.0"]]
  :min-lein-version "2.0.0"
  :resource-paths ["config", "resources"]
  :profiles {:dev {:env {:coll-name "dev" :uri "mongodb://mongodb:27017/dev?authSource=admin"}
                   :aliases {"run-dev" ["trampoline" "run" "-m" "name-service.delivery.server/run-dev"]}
                   :dependencies [[io.pedestal/pedestal.service-tools "0.5.8"]]}
             :uberjar {:aot [name-service.delivery.server]}}
  :main ^{:skip-aot true} name-service.delivery.server)
