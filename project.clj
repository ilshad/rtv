(defproject rtv "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [com.datomic/datomic-pro "0.9.4572"]
                 [com.stuartsierra/component "0.2.1"]
                 [com.draines/postal "1.11.1"]
                 [compojure "1.1.6"]
                 [environ "0.4.0"]
                 [ring "1.2.2"]
                 [hiccup "1.0.5"]
                 [enlive "1.1.5"]
                 [cheshire "5.3.1"]
                 [com.cemerick/friend "0.2.0"]
                 [ring/ring-json "0.2.0"]
                 [amazonica "0.1.28"]
                 [clj-http "0.7.7"]]
  :plugins [[lein-environ "0.4.0"]]
  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.4"]]
                   :env {:datomic-uri "datomic:mem://rtv"
                         :http-server-port 7777
                         :facebook {:client-id "123"
                                    :client-secret "xxx"}
                         :aws {:access-key "foo"
                               :secret-key "bar"
                               :endpoint "http://localhost:4567"}}}})